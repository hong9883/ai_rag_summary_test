package com.medicine.rag.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 파일 저장 설정
 */
@Configuration
@Getter
public class FileStorageConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.temp-dir}")
    private String tempDir;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path tempPath = Paths.get(tempDir).toAbsolutePath().normalize();

            Files.createDirectories(uploadPath);
            Files.createDirectories(tempPath);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 디렉토리를 생성할 수 없습니다.", e);
        }
    }
}
