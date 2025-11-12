package com.medicine.rag.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicine.rag.config.ChromaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Chroma 벡터 DB 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChromaService {

    private final ChromaConfig chromaConfig;
    private final OkHttpClient chromaHttpClient;
    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 컬렉션 생성 또는 가져오기
     */
    public String getOrCreateCollection() throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", chromaConfig.getCollectionName());

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
                .url(chromaConfig.getBaseUrl() + "/api/v1/collections")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        try (Response response = chromaHttpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("id").asText();
        }
    }

    /**
     * 문서 벡터 추가
     */
    public String addDocument(String documentId, String text, Map<String, String> metadata) throws IOException {
        // Ollama를 통해 임베딩 생성
        double[] embedding = ollamaService.generateEmbedding(text);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ids", Collections.singletonList(documentId));
        requestBody.put("embeddings", Collections.singletonList(embedding));
        requestBody.put("documents", Collections.singletonList(text));
        requestBody.put("metadatas", Collections.singletonList(metadata));

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
                .url(chromaConfig.getBaseUrl() + "/api/v1/collections/" +
                        chromaConfig.getCollectionName() + "/add")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        try (Response response = chromaHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Chroma에 문서 추가 실패: " + response);
            }
            return documentId;
        }
    }

    /**
     * 유사 문서 검색
     */
    public List<Map<String, Object>> query(String queryText, int topK) throws IOException {
        // 쿼리 텍스트의 임베딩 생성
        double[] queryEmbedding = ollamaService.generateEmbedding(queryText);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query_embeddings", Collections.singletonList(queryEmbedding));
        requestBody.put("n_results", topK);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
                .url(chromaConfig.getBaseUrl() + "/api/v1/collections/" +
                        chromaConfig.getCollectionName() + "/query")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        try (Response response = chromaHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Chroma 쿼리 실패: " + response);
            }

            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            List<Map<String, Object>> results = new ArrayList<>();
            JsonNode documentsNode = jsonNode.get("documents").get(0);
            JsonNode metadatasNode = jsonNode.get("metadatas").get(0);
            JsonNode distancesNode = jsonNode.get("distances").get(0);

            for (int i = 0; i < documentsNode.size(); i++) {
                Map<String, Object> result = new HashMap<>();
                result.put("document", documentsNode.get(i).asText());
                result.put("metadata", objectMapper.convertValue(metadatasNode.get(i), Map.class));
                result.put("distance", distancesNode.get(i).asDouble());
                results.add(result);
            }

            return results;
        }
    }

    /**
     * 벡터 DB 통계
     */
    public Map<String, Object> getStatistics() throws IOException {
        Request request = new Request.Builder()
                .url(chromaConfig.getBaseUrl() + "/api/v1/collections/" +
                        chromaConfig.getCollectionName())
                .get()
                .build();

        try (Response response = chromaHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Chroma 통계 조회 실패: " + response);
            }

            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            Map<String, Object> stats = new HashMap<>();
            stats.put("count", jsonNode.get("count").asLong());
            stats.put("name", jsonNode.get("name").asText());

            return stats;
        }
    }
}
