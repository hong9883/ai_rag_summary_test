package com.medicine.rag.service;

import com.medicine.rag.config.FileStorageConfig;
import com.medicine.rag.dto.DocumentUploadRequest;
import com.medicine.rag.model.Document;
import com.medicine.rag.repository.DocumentRepository;
import com.medicine.rag.util.DocumentParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 문서 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentParser documentParser;
    private final ChromaService chromaService;
    private final OpenSearchService openSearchService;
    private final FileStorageConfig fileStorageConfig;

    private static final int CHUNK_SIZE = 1000; // 청크 크기
    private static final int CHUNK_OVERLAP = 200; // 청크 오버랩

    /**
     * 문서 업로드 및 처리
     */
    @Transactional
    public DocumentUploadRequest uploadDocument(MultipartFile file) throws IOException {
        // 파일 검증
        validateFile(file);

        String originalFileName = file.getOriginalFilename();
        String fileType = getFileExtension(originalFileName);
        String fileName = generateUniqueFileName(originalFileName);

        // 파일 저장
        Path uploadPath = Paths.get(fileStorageConfig.getUploadDir()).resolve(fileName);
        Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

        // 텍스트 추출
        File uploadedFile = uploadPath.toFile();
        String extractedText = documentParser.extractText(uploadedFile, fileType);

        // 텍스트 청크 분할
        List<String> chunks = documentParser.chunkText(extractedText, CHUNK_SIZE, CHUNK_OVERLAP);

        // Vector DB에 저장
        String vectorId = UUID.randomUUID().toString();
        Map<String, String> metadata = new HashMap<>();
        metadata.put("fileName", originalFileName);
        metadata.put("fileType", fileType);
        metadata.put("uploadDate", LocalDateTime.now().toString());

        // 각 청크를 Vector DB에 저장
        for (int i = 0; i < chunks.size(); i++) {
            String chunkId = vectorId + "_chunk_" + i;
            Map<String, String> chunkMetadata = new HashMap<>(metadata);
            chunkMetadata.put("chunkIndex", String.valueOf(i));
            chromaService.addDocument(chunkId, chunks.get(i), chunkMetadata);
        }

        // OpenSearch에 인덱싱
        String searchIndexId = UUID.randomUUID().toString();
        Map<String, Object> searchDocument = new HashMap<>();
        searchDocument.put("fileName", originalFileName);
        searchDocument.put("fileType", fileType);
        searchDocument.put("content", extractedText);
        searchDocument.put("uploadDate", LocalDateTime.now().toString());
        openSearchService.indexDocument(searchIndexId, searchDocument);

        // 데이터베이스에 저장
        Document document = Document.builder()
                .fileName(fileName)
                .originalFileName(originalFileName)
                .fileType(fileType)
                .fileSize(file.getSize())
                .filePath(uploadPath.toString())
                .extractedText(extractedText)
                .vectorId(vectorId)
                .searchIndexId(searchIndexId)
                .chunkCount(chunks.size())
                .build();

        document = documentRepository.save(document);

        log.info("문서 업로드 완료: {} (청크: {}개)", originalFileName, chunks.size());

        return DocumentUploadRequest.builder()
                .documentId(document.getId())
                .fileName(originalFileName)
                .fileType(fileType)
                .fileSize(file.getSize())
                .vectorId(vectorId)
                .chunkCount(chunks.size())
                .message("문서가 성공적으로 업로드되었습니다.")
                .build();
    }

    /**
     * 문서 목록 조회
     */
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    /**
     * 문서 상세 조회
     */
    public Optional<Document> getDocument(Long id) {
        return documentRepository.findById(id);
    }

    /**
     * 문서 삭제
     */
    @Transactional
    public void deleteDocument(Long id) throws IOException {
        Optional<Document> documentOpt = documentRepository.findById(id);
        if (documentOpt.isPresent()) {
            Document document = documentOpt.get();

            // 파일 삭제
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);

            // OpenSearch에서 삭제
            openSearchService.deleteDocument(document.getSearchIndexId());

            // 데이터베이스에서 삭제
            documentRepository.delete(document);

            log.info("문서 삭제 완료: {}", document.getOriginalFileName());
        }
    }

    /**
     * 파일 검증
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("파일 이름이 없습니다.");
        }

        String fileType = getFileExtension(fileName).toLowerCase();
        List<String> allowedTypes = Arrays.asList("pdf", "txt", "doc", "docx", "hwp");

        if (!allowedTypes.contains(fileType)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + fileType);
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * 고유한 파일 이름 생성
     */
    private String generateUniqueFileName(String originalFileName) {
        String fileExtension = getFileExtension(originalFileName);
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        return baseName + "_" + System.currentTimeMillis() + "." + fileExtension;
    }
}
