package com.smile.prism.search.api;

import com.smile.prism.search.service.DiscoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller providing high-performance discovery capabilities.
 * Implements the Query side of the CQRS pattern for the Prism Engine.
 */
@RestController
@RequestMapping("/api/v1/discovery")
@RequiredArgsConstructor
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    /**
     * Executes advanced fuzzy search against the Elasticsearch index.
     * Uses POST to allow for complex query criteria and filters.
     *
     * @param request The search criteria including query string and filters.
     * @return SearchResponse containing hits, total count, and highlighting fragments.
     */
    @PostMapping("/search")
    public ResponseEntity<SearchResponse> advancedSearch(@RequestBody @Valid DiscoveryRequest request) {
        return ResponseEntity.ok(discoveryService.discover(request));
    }
}