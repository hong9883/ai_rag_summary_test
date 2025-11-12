package com.medicine.rag.repository;

import com.medicine.rag.model.QueryHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 질문 이력 리포지토리
 */
@Repository
public interface QueryHistoryRepository extends JpaRepository<QueryHistory, Long> {

    Page<QueryHistory> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<QueryHistory> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT q.promptType, COUNT(q) FROM QueryHistory q GROUP BY q.promptType")
    List<Object[]> countByPromptType();

    @Query("SELECT AVG(q.responseTime) FROM QueryHistory q WHERE q.promptType = :promptType")
    Double averageResponseTimeByPromptType(QueryHistory.PromptType promptType);

    @Query("SELECT COUNT(q) FROM QueryHistory q")
    Long countTotalQueries();
}
