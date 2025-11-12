package com.medicine.rag.service;

import com.medicine.rag.dto.StatisticsResponse;
import com.medicine.rag.model.QueryHistory;
import com.medicine.rag.repository.DocumentRepository;
import com.medicine.rag.repository.QueryHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 통계 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final DocumentRepository documentRepository;
    private final QueryHistoryRepository queryHistoryRepository;
    private final ChromaService chromaService;

    /**
     * 전체 통계 조회
     */
    public StatisticsResponse getStatistics() throws IOException {
        // 벡터 DB 통계
        StatisticsResponse.VectorDBStats vectorDBStats = getVectorDBStatistics();

        // 프롬프트 사용 통계
        StatisticsResponse.PromptUsageStats promptUsageStats = getPromptUsageStatistics();

        return StatisticsResponse.builder()
                .vectorDBStats(vectorDBStats)
                .promptUsageStats(promptUsageStats)
                .build();
    }

    /**
     * 벡터 DB 통계
     */
    private StatisticsResponse.VectorDBStats getVectorDBStatistics() throws IOException {
        Long totalDocuments = documentRepository.countTotalDocuments();
        Long totalChunks = documentRepository.countTotalChunks();

        // 파일 타입별 문서 수
        List<Object[]> documentsByType = documentRepository.countByFileType();
        Map<String, Long> documentsByTypeMap = new HashMap<>();
        for (Object[] row : documentsByType) {
            documentsByTypeMap.put((String) row[0], (Long) row[1]);
        }

        // Chroma 통계
        Map<String, Object> chromaStats = chromaService.getStatistics();
        Long totalVectorSize = (Long) chromaStats.get("count");

        return StatisticsResponse.VectorDBStats.builder()
                .totalDocuments(totalDocuments != null ? totalDocuments : 0L)
                .totalChunks(totalChunks != null ? totalChunks : 0L)
                .documentsByType(documentsByTypeMap)
                .totalVectorSize(totalVectorSize)
                .build();
    }

    /**
     * 프롬프트 사용 통계
     */
    private StatisticsResponse.PromptUsageStats getPromptUsageStatistics() {
        Long totalQueries = queryHistoryRepository.countTotalQueries();

        // 프롬프트 타입별 사용 횟수
        List<Object[]> usageByPromptType = queryHistoryRepository.countByPromptType();
        Map<String, Long> usageByPromptTypeMap = new HashMap<>();
        for (Object[] row : usageByPromptType) {
            QueryHistory.PromptType promptType = (QueryHistory.PromptType) row[0];
            Long count = (Long) row[1];
            usageByPromptTypeMap.put(promptType.getDescription(), count);
        }

        // 프롬프트 타입별 평균 응답 시간
        Map<String, Double> avgResponseTimeMap = new HashMap<>();
        for (QueryHistory.PromptType promptType : QueryHistory.PromptType.values()) {
            Double avgTime = queryHistoryRepository.averageResponseTimeByPromptType(promptType);
            if (avgTime != null) {
                avgResponseTimeMap.put(promptType.getDescription(), avgTime);
            }
        }

        return StatisticsResponse.PromptUsageStats.builder()
                .totalQueries(totalQueries != null ? totalQueries : 0L)
                .usageByPromptType(usageByPromptTypeMap)
                .avgResponseTimeByPromptType(avgResponseTimeMap)
                .build();
    }
}
