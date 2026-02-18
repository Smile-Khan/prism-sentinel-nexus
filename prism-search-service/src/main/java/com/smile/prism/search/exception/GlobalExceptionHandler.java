package com.smile.prism.search.exception;

import com.smile.prism.search.api.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

/**
 * Enterprise-grade Exception Handler.
 * Intercepts infrastructure-level failures and converts them into clean API responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where the Elasticsearch index has not been created yet.
     * Instead of a 500 error, we return an empty result set with a 200 OK.
     */
    @ExceptionHandler(NoSuchIndexException.class)
    public ResponseEntity<SearchResponse> handleMissingIndex(NoSuchIndexException ex) {
        log.warn("[GLOBAL-EX-HANDLER] Search attempted on non-existent index: {}", ex.getIndex());

        SearchResponse emptyResponse = new SearchResponse(
                0L,
                Collections.emptyList(),
                "INDEX_NOT_INITIALIZED"
        );

        return ResponseEntity.ok(emptyResponse);
    }
}