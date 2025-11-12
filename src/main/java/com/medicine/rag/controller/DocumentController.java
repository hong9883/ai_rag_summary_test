package com.medicine.rag.controller;

import com.medicine.rag.dto.DocumentUploadRequest;
import com.medicine.rag.model.Document;
import com.medicine.rag.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 문서 관리 컨트롤러
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    /**
     * 문서 업로드
     */
    @PostMapping("/upload")
    public ResponseEntity<DocumentUploadRequest> uploadDocument(
            @RequestParam("file") MultipartFile file) {
        try {
            DocumentUploadRequest response = documentService.uploadDocument(file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("문서 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            log.error("잘못된 파일", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 문서 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    /**
     * 문서 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable Long id) {
        return documentService.getDocument(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 문서 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("문서 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
