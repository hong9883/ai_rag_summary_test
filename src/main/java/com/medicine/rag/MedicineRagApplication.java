package com.medicine.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 의약품 RAG 요약 프로그램 메인 애플리케이션
 */
@SpringBootApplication
@EnableJpaAuditing
public class MedicineRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicineRagApplication.class, args);
    }
}
