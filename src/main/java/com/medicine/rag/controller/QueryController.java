package com.medicine.rag.controller;

import com.medicine.rag.dto.QueryRequest;
import com.medicine.rag.dto.QueryResponse;
import com.medicine.rag.model.QueryHistory;
import com.medicine.rag.service.QueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 질문-답변 컨트롤러
 */
@RestController
@RequestMapping("/api/queries")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class QueryController {

    private final QueryService queryService;

    /**
     * 질문 처리
     */
    @PostMapping
    public ResponseEntity<QueryResponse> processQuery(@Valid @RequestBody QueryRequest request) {
        try {
            QueryResponse response = queryService.processQuery(request);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("질문 처리 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 질문 이력 조회
     */
    @GetMapping("/history")
    public ResponseEntity<Page<QueryHistory>> getQueryHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QueryHistory> history = queryService.getQueryHistory(pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * 질문 이력 상세 조회
     */
    @GetMapping("/history/{id}")
    public ResponseEntity<QueryHistory> getQueryHistoryDetail(@PathVariable Long id) {
        try {
            QueryHistory history = queryService.getQueryHistoryDetail(id);
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
