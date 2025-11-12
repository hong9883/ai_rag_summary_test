package com.medicine.rag.util;

import com.medicine.rag.model.QueryHistory.PromptType;
import org.springframework.stereotype.Component;

/**
 * 프롬프트 빌더 유틸리티
 */
@Component
public class PromptBuilder {

    /**
     * 프롬프트 방식에 따라 프롬프트 생성
     */
    public String buildPrompt(PromptType promptType, String question, String context) {
        String basePrompt = String.format("""
                다음은 의약품 관련 문서에서 검색된 정보입니다:

                %s

                질문: %s

                """, context, question);

        return basePrompt + getPromptInstruction(promptType);
    }

    /**
     * 프롬프트 방식별 지시문
     */
    private String getPromptInstruction(PromptType promptType) {
        return switch (promptType) {
            case BASIC -> """
                    위 정보를 바탕으로 질문에 대해 명확하게 답변해주세요.
                    """;

            case STRUCTURED -> """
                    위 정보를 바탕으로 다음 구조에 맞춰 답변해주세요:
                    1. 핵심 요약
                    2. 상세 설명
                    3. 주의사항
                    4. 참고사항
                    """;

            case SIMPLE -> """
                    위 정보를 바탕으로 간단명료하게 2-3문장으로 답변해주세요.
                    전문 용어는 쉽게 풀어서 설명해주세요.
                    """;

            case DETAILED -> """
                    위 정보를 바탕으로 상세하고 포괄적으로 답변해주세요.
                    관련된 모든 정보를 포함하고, 예시가 있다면 함께 제공해주세요.
                    """;

            case POINTS -> """
                    위 정보를 바탕으로 핵심 포인트를 bullet point 형식으로 정리해주세요.
                    각 포인트는 명확하고 간결하게 작성해주세요.
                    """;

            case FACT_CHECK -> """
                    위 정보를 바탕으로 질문에 대한 사실 여부를 확인해주세요.
                    다음 형식으로 답변해주세요:
                    - 사실 여부: [예/아니오/부분적으로 사실]
                    - 근거: [문서에서 찾은 근거]
                    - 추가 설명: [필요한 경우]
                    """;

            case STEP_BY_STEP -> """
                    위 정보를 바탕으로 단계별로 생각하며 답변해주세요:
                    1. 먼저 질문을 분석합니다.
                    2. 관련된 정보를 찾습니다.
                    3. 정보를 종합하여 결론을 도출합니다.
                    4. 최종 답변을 제시합니다.

                    각 단계의 사고 과정을 보여주세요.
                    """;
        };
    }
}
