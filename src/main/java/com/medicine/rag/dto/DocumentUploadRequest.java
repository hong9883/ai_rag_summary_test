package com.medicine.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 문서 업로드 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadRequest {
    private Long documentId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String vectorId;
    private Integer chunkCount;
    private String message;
}
