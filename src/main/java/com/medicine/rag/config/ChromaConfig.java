package com.medicine.rag.config;

import lombok.Getter;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Chroma Vector DB 설정
 */
@Configuration
@Getter
public class ChromaConfig {

    @Value("${chroma.base-url}")
    private String baseUrl;

    @Value("${chroma.collection-name}")
    private String collectionName;

    @Value("${chroma.timeout}")
    private Integer timeout;

    @Bean(name = "chromaHttpClient")
    public OkHttpClient chromaHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .readTimeout(Duration.ofSeconds(timeout))
                .writeTimeout(Duration.ofSeconds(timeout))
                .build();
    }
}
