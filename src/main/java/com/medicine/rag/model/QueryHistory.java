package com.medicine.rag.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 질문 이력 엔티티
 */
@Entity
@Table(name = "query_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class QueryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PromptType promptType;

    @Column(columnDefinition = "TEXT")
    private String retrievedContext; // RAG에서 검색된 컨텍스트

    @Column(nullable = false)
    private Integer responseTime; // 응답 시간 (ms)

    @Column(nullable = false)
    private Integer relevantDocuments; // 관련 문서 수

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum PromptType {
        BASIC("기본"),
        STRUCTURED("구조화"),
        SIMPLE("간단"),
        DETAILED("상세"),
        POINTS("포인트"),
        FACT_CHECK("사실 확인"),
        STEP_BY_STEP("단계별 사고");

        private final String description;

        PromptType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
