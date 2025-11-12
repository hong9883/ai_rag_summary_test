package com.medicine.rag.service;

import com.medicine.rag.config.OpenSearchConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * OpenSearch 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenSearchService {

    private final OpenSearchClient openSearchClient;
    private final OpenSearchConfig openSearchConfig;

    /**
     * 인덱스 초기화
     */
    public void initializeIndex() throws IOException {
        String indexName = openSearchConfig.getIndexName();

        ExistsRequest existsRequest = new ExistsRequest.Builder()
                .index(indexName)
                .build();

        boolean exists = openSearchClient.indices().exists(existsRequest).value();

        if (!exists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                    .index(indexName)
                    .build();

            openSearchClient.indices().create(createIndexRequest);
            log.info("OpenSearch 인덱스 생성: {}", indexName);
        }
    }

    /**
     * 문서 인덱싱
     */
    public String indexDocument(String id, Map<String, Object> document) throws IOException {
        IndexRequest<Map<String, Object>> request = new IndexRequest.Builder<Map<String, Object>>()
                .index(openSearchConfig.getIndexName())
                .id(id)
                .document(document)
                .build();

        IndexResponse response = openSearchClient.index(request);
        return response.id();
    }

    /**
     * 전문 검색
     */
    public List<Map<String, Object>> search(String queryText, int size) throws IOException {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(openSearchConfig.getIndexName())
                .query(q -> q
                        .multiMatch(m -> m
                                .query(queryText)
                                .fields("title", "content", "fileName")
                        )
                )
                .size(size)
                .build();

        SearchResponse<Map> response = openSearchClient.search(searchRequest, Map.class);

        List<Map<String, Object>> results = new ArrayList<>();
        for (Hit<Map> hit : response.hits().hits()) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", hit.id());
            result.put("score", hit.score());
            result.put("source", hit.source());
            results.add(result);
        }

        return results;
    }

    /**
     * 문서 삭제
     */
    public void deleteDocument(String id) throws IOException {
        DeleteRequest request = new DeleteRequest.Builder()
                .index(openSearchConfig.getIndexName())
                .id(id)
                .build();

        openSearchClient.delete(request);
    }

    /**
     * 인덱스 통계
     */
    public Map<String, Object> getStatistics() throws IOException {
        CountRequest countRequest = new CountRequest.Builder()
                .index(openSearchConfig.getIndexName())
                .build();

        CountResponse countResponse = openSearchClient.count(countRequest);

        Map<String, Object> stats = new HashMap<>();
        stats.put("documentCount", countResponse.count());
        stats.put("indexName", openSearchConfig.getIndexName());

        return stats;
    }
}
