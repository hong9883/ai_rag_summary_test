package com.medicine.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 통계 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {

    // 벡터 DB 통계
    private VectorDBStats vectorDBStats;

    // 프롬프트 사용 통계
    private PromptUsageStats promptUsageStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VectorDBStats {
        private Long totalDocuments;
        private Long totalChunks;
        private Map<String, Long> documentsByType;
        private Long totalVectorSize;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromptUsageStats {
        private Long totalQueries;
        private Map<String, Long> usageByPromptType;
        private Map<String, Double> avgResponseTimeByPromptType;
    }
}
