package com.medicine.rag.repository;

import com.medicine.rag.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 문서 리포지토리
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByVectorId(String vectorId);

    List<Document> findByFileTypeOrderByCreatedAtDesc(String fileType);

    @Query("SELECT COUNT(d) FROM Document d")
    Long countTotalDocuments();

    @Query("SELECT SUM(d.chunkCount) FROM Document d")
    Long countTotalChunks();

    @Query("SELECT d.fileType, COUNT(d) FROM Document d GROUP BY d.fileType")
    List<Object[]> countByFileType();
}
