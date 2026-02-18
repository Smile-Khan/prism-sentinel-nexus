package com.smile.prism.search.service;

import com.smile.prism.search.api.DiscoveryRequest;
import com.smile.prism.search.api.SearchResponse;
import com.smile.prism.search.document.EventDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of the Discovery Engine for the Prism Read-Model.
 * <p>
 * This service leverages Elasticsearch Native Queries to provide full-text fuzzy search,
 * filtering, and HTML highlighting capabilities. It is optimized for high-throughput
 * read operations decoupled from the primary write-store.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscoveryService {

    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * Executes a native fuzzy search against the 'prism-events' index.
     *
     * @param request The discovery criteria including query string, category filters, and pagination.
     * @return SearchResponse containing hydrated results and match highlighting.
     */
    public SearchResponse discover(DiscoveryRequest request) {
        if (!StringUtils.hasText(request.query())) {
            return new SearchResponse(0L, Collections.emptyList(), "EMPTY_QUERY");
        }

        log.info("[DISCOVERY-SERVICE] Executing search. Query: {}, Page: {}, Size: {}",
                request.query(), request.page(), request.size());

        HighlightQuery highlightQuery = buildHighlightQuery();

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .bool(b -> {
                            // Full-text match with fuziness to handle typos
                            b.must(m -> m.match(match -> match
                                    .field("title")
                                    .query(request.query())
                                    .fuzziness("AUTO")));

                            // Category filter (Keyword match)
                            if (StringUtils.hasText(request.category())) {
                                b.filter(f -> f.term(t -> t.field("category").value(request.category())));
                            }
                            return b;
                        })
                )
                .withHighlightQuery(highlightQuery)
                .withPageable(PageRequest.of(request.page(), request.size()))
                .build();

        try {
            SearchHits<EventDocument> searchHits = elasticsearchOperations.search(query, EventDocument.class);

            List<SearchResponse.SearchResult> results = searchHits.getSearchHits().stream()
                    .map(hit -> new SearchResponse.SearchResult(
                            hit.getContent(),
                            hit.getHighlightFields()
                    ))
                    .toList();

            log.debug("[DISCOVERY-SERVICE] Search completed. Total hits: {}", searchHits.getTotalHits());

            return new SearchResponse(
                    searchHits.getTotalHits(),
                    results,
                    "SUCCESS"
            );
        } catch (Exception e) {
            log.error("[DISCOVERY-SERVICE] Search execution failed for query: {}", request.query(), e);
            throw e;
        }
    }

    /**
     * Constructs a highlighting configuration for specific fields.
     * Fragments are wrapped in HTML <em> tags by default.
     */
    private HighlightQuery buildHighlightQuery() {
        return new HighlightQuery(
                new Highlight(List.of(new HighlightField("title"))),
                EventDocument.class
        );
    }
}