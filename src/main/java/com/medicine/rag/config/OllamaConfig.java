package com.medicine.rag.config;

import lombok.Getter;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Ollama 설정
 */
@Configuration
@Getter
public class OllamaConfig {

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.model}")
    private String model;

    @Value("${ollama.timeout}")
    private Integer timeout;

    @Bean(name = "ollamaHttpClient")
    public OkHttpClient ollamaHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .readTimeout(Duration.ofSeconds(timeout))
                .writeTimeout(Duration.ofSeconds(timeout))
                .build();
    }
}
