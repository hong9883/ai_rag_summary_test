package com.medicine.rag.dto;

import com.medicine.rag.model.QueryHistory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 질문 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {

    @NotBlank(message = "질문을 입력해주세요")
    private String question;

    @NotNull(message = "프롬프트 방식을 선택해주세요")
    private QueryHistory.PromptType promptType;

    private Integer topK = 5; // 검색할 관련 문서 수
}
