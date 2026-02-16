package com.smile.prism.search.repository;

import com.smile.prism.search.document.EventDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, String> {
    // We will add fuzzy search methods later
}