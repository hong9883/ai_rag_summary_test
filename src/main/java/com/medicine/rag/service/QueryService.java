package com.medicine.rag.service;

import com.medicine.rag.dto.QueryRequest;
import com.medicine.rag.dto.QueryResponse;
import com.medicine.rag.model.QueryHistory;
import com.medicine.rag.repository.QueryHistoryRepository;
import com.medicine.rag.util.PromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 질문-답변 서비스 (RAG)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QueryService {

    private final QueryHistoryRepository queryHistoryRepository;
    private final ChromaService chromaService;
    private final OpenSearchService openSearchService;
    private final OllamaService ollamaService;
    private final PromptBuilder promptBuilder;

    /**
     * RAG 질문-답변 처리
     */
    @Transactional
    public QueryResponse processQuery(QueryRequest request) throws IOException {
        long startTime = System.currentTimeMillis();

        // 1. Vector DB에서 유사 문서 검색
        List<Map<String, Object>> vectorResults = chromaService.query(
                request.getQuestion(),
                request.getTopK()
        );

        // 2. OpenSearch에서 전문 검색
        List<Map<String, Object>> searchResults = openSearchService.search(
                request.getQuestion(),
                request.getTopK()
        );

        // 3. 검색 결과 통합
        String context = buildContext(vectorResults, searchResults);
        List<String> sources = extractSources(vectorResults);

        // 4. 프롬프트 생성
        String prompt = promptBuilder.buildPrompt(
                request.getPromptType(),
                request.getQuestion(),
                context
        );

        // 5. LLM으로 답변 생성
        String answer = ollamaService.generate(prompt);

        long endTime = System.currentTimeMillis();
        int responseTime = (int) (endTime - startTime);

        // 6. 이력 저장
        QueryHistory history = QueryHistory.builder()
                .question(request.getQuestion())
                .answer(answer)
                .promptType(request.getPromptType())
                .retrievedContext(context)
                .responseTime(responseTime)
                .relevantDocuments(vectorResults.size() + searchResults.size())
                .build();

        history = queryHistoryRepository.save(history);

        log.info("질문 처리 완료 - 질문: {}, 응답시간: {}ms", request.getQuestion(), responseTime);

        return QueryResponse.builder()
                .queryId(history.getId())
                .question(request.getQuestion())
                .answer(answer)
                .promptType(request.getPromptType())
                .responseTime(responseTime)
                .relevantDocuments(vectorResults.size() + searchResults.size())
                .sources(sources)
                .timestamp(history.getCreatedAt())
                .build();
    }

    /**
     * 질문 이력 조회 (페이징)
     */
    public Page<QueryHistory> getQueryHistory(Pageable pageable) {
        return queryHistoryRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * 질문 이력 상세 조회
     */
    public QueryHistory getQueryHistoryDetail(Long id) {
        return queryHistoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("질문 이력을 찾을 수 없습니다."));
    }

    /**
     * 검색 결과로부터 컨텍스트 생성
     */
    private String buildContext(List<Map<String, Object>> vectorResults,
                                  List<Map<String, Object>> searchResults) {
        StringBuilder context = new StringBuilder();

        context.append("=== 벡터 검색 결과 ===\n");
        for (int i = 0; i < vectorResults.size(); i++) {
            Map<String, Object> result = vectorResults.get(i);
            String document = (String) result.get("document");
            context.append(String.format("[%d] %s\n\n", i + 1, document));
        }

        if (!searchResults.isEmpty()) {
            context.append("\n=== 전문 검색 결과 ===\n");
            for (int i = 0; i < Math.min(3, searchResults.size()); i++) {
                Map<String, Object> result = searchResults.get(i);
                Map<String, Object> source = (Map<String, Object>) result.get("source");
                String content = (String) source.get("content");

                // 긴 내용은 요약
                if (content != null && content.length() > 500) {
                    content = content.substring(0, 500) + "...";
                }

                context.append(String.format("[%d] %s\n\n", i + 1, content));
            }
        }

        return context.toString();
    }

    /**
     * 출처 추출
     */
    private List<String> extractSources(List<Map<String, Object>> vectorResults) {
        List<String> sources = new ArrayList<>();

        for (Map<String, Object> result : vectorResults) {
            Map<String, Object> metadata = (Map<String, Object>) result.get("metadata");
            if (metadata != null && metadata.containsKey("fileName")) {
                String fileName = (String) metadata.get("fileName");
                if (!sources.contains(fileName)) {
                    sources.add(fileName);
                }
            }
        }

        return sources;
    }
}
