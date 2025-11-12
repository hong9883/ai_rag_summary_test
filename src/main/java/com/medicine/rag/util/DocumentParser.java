package com.medicine.rag.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 문서 파싱 유틸리티
 */
@Component
@Slf4j
public class DocumentParser {

    /**
     * 파일에서 텍스트 추출
     */
    public String extractText(File file, String fileType) throws IOException {
        return switch (fileType.toLowerCase()) {
            case "pdf" -> extractFromPdf(file);
            case "txt" -> extractFromTxt(file);
            case "doc", "docx" -> extractFromWord(file);
            case "hwp" -> extractFromHwp(file);
            default -> throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + fileType);
        };
    }

    /**
     * PDF에서 텍스트 추출
     */
    private String extractFromPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * TXT에서 텍스트 추출
     */
    private String extractFromTxt(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * Word 문서에서 텍스트 추출
     */
    private String extractFromWord(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                content.append(paragraph.getText()).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * HWP에서 텍스트 추출
     * 참고: HWP 파일 형식은 복잡하며, 완전한 파싱을 위해서는 별도의 라이브러리가 필요합니다.
     * 여기서는 기본적인 텍스트 추출만 구현합니다.
     */
    private String extractFromHwp(File file) throws IOException {
        log.warn("HWP 파일 형식은 제한적으로 지원됩니다. 전문적인 HWP 파서 라이브러리 사용을 권장합니다.");
        // HWP 파일의 경우 한컴오피스 API 또는 별도 라이브러리가 필요
        // 현재는 기본 텍스트 파일로 처리
        return extractFromTxt(file);
    }

    /**
     * 텍스트를 청크로 분할
     */
    public List<String> chunkText(String text, int chunkSize, int overlap) {
        java.util.List<String> chunks = new java.util.ArrayList<>();
        int textLength = text.length();
        int start = 0;

        while (start < textLength) {
            int end = Math.min(start + chunkSize, textLength);

            // 문장 단위로 끊기 위해 마지막 마침표 찾기
            if (end < textLength) {
                int lastPeriod = text.lastIndexOf('.', end);
                if (lastPeriod > start) {
                    end = lastPeriod + 1;
                }
            }

            chunks.add(text.substring(start, end).trim());
            start = end - overlap;
        }

        return chunks;
    }
}
