package com.medicine.rag.controller;

import com.medicine.rag.dto.StatisticsResponse;
import com.medicine.rag.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 통계 컨트롤러
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 전체 통계 조회
     */
    @GetMapping
    public ResponseEntity<StatisticsResponse> getStatistics() {
        try {
            StatisticsResponse statistics = statisticsService.getStatistics();
            return ResponseEntity.ok(statistics);
        } catch (IOException e) {
            log.error("통계 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
