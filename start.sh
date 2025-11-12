#!/bin/bash

echo "=========================================="
echo "의약품 RAG 요약 프로그램 시작 스크립트"
echo "=========================================="

# Docker Compose 서비스 시작
echo ""
echo "[1/4] Docker 서비스 시작 중..."
docker-compose up -d

# 서비스가 준비될 때까지 대기
echo ""
echo "[2/4] 서비스가 준비될 때까지 대기 중..."
sleep 10

# Ollama 모델 확인
echo ""
echo "[3/4] Ollama 모델 확인 중..."
docker exec medicine-rag-ollama ollama list

echo ""
echo "Ollama 모델이 설치되지 않았다면 다음 명령어로 설치하세요:"
echo "docker exec -it medicine-rag-ollama ollama pull llama2"

# Spring Boot 애플리케이션 시작
echo ""
echo "[4/4] Spring Boot 애플리케이션 시작 중..."
./mvnw spring-boot:run

echo ""
echo "=========================================="
echo "애플리케이션이 http://localhost:8080 에서 실행 중입니다."
echo "=========================================="
