package com.medicine.rag.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicine.rag.config.OllamaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Ollama LLM 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaService {

    private final OllamaConfig ollamaConfig;
    private final OkHttpClient ollamaHttpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 텍스트 생성
     */
    public String generate(String prompt) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", ollamaConfig.getModel());
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
                .url(ollamaConfig.getBaseUrl() + "/api/generate")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        try (Response response = ollamaHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Ollama API 호출 실패: " + response);
            }

            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("response").asText();
        }
    }

    /**
     * 임베딩 생성
     */
    public double[] generateEmbedding(String text) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", ollamaConfig.getModel());
        requestBody.put("prompt", text);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
                .url(ollamaConfig.getBaseUrl() + "/api/embeddings")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        try (Response response = ollamaHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Ollama 임베딩 생성 실패: " + response);
            }

            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode embeddingNode = jsonNode.get("embedding");

            double[] embedding = new double[embeddingNode.size()];
            for (int i = 0; i < embeddingNode.size(); i++) {
                embedding[i] = embeddingNode.get(i).asDouble();
            }

            return embedding;
        }
    }

    /**
     * Ollama 서버 연결 확인
     */
    public boolean isHealthy() {
        try {
            Request request = new Request.Builder()
                    .url(ollamaConfig.getBaseUrl() + "/api/tags")
                    .get()
                    .build();

            try (Response response = ollamaHttpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            log.error("Ollama 서버 연결 실패", e);
            return false;
        }
    }
}
