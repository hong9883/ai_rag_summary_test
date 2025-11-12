package com.medicine.rag.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenSearch 설정
 */
@Configuration
@Slf4j
public class OpenSearchConfig {

    @Value("${opensearch.host}")
    private String host;

    @Value("${opensearch.port}")
    private Integer port;

    @Value("${opensearch.scheme}")
    private String scheme;

    @Value("${opensearch.index-name}")
    private String indexName;

    @Bean
    public OpenSearchClient openSearchClient() {
        RestClient restClient = RestClient.builder(
                new HttpHost(host, port, scheme)
        ).build();

        RestClientTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );

        return new OpenSearchClient(transport);
    }

    public String getIndexName() {
        return indexName;
    }
}
