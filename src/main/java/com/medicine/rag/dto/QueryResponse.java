package com.medicine.rag.dto;

import com.medicine.rag.model.QueryHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 질문 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {
    private Long queryId;
    private String question;
    private String answer;
    private QueryHistory.PromptType promptType;
    private Integer responseTime;
    private Integer relevantDocuments;
    private List<String> sources;
    private LocalDateTime timestamp;
}
