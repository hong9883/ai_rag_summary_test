# 의약품 RAG 요약 프로그램

의약품 관련 문서를 업로드하고 AI 기반 RAG(Retrieval-Augmented Generation) 시스템을 통해 질문-답변을 제공하는 웹 애플리케이션입니다.

## 주요 기능

### 1. 문서 업로드 기능
- 다양한 파일 형식 지원: PDF, TXT, DOC, DOCX, HWP
- 자동 텍스트 추출 및 청크 분할
- 벡터 DB(Chroma) 및 OpenSearch에 자동 인덱싱

### 2. 질문 및 답변 기능 (RAG)
- 7가지 최적화된 프롬프트 방식 제공:
  - **기본**: 명확한 답변
  - **구조화**: 체계적인 구조로 답변
  - **간단**: 2-3문장의 간단명료한 답변
  - **상세**: 포괄적이고 상세한 답변
  - **포인트**: 핵심 내용을 bullet point로 정리
  - **사실 확인**: 사실 여부 검증
  - **단계별 사고**: 사고 과정을 단계별로 제시
- 벡터 검색과 전문 검색을 결합한 하이브리드 검색
- 실시간 답변 생성

### 3. 질문 이력 관리
- 모든 질문과 답변 기록 저장
- 이력 조회 및 상세 정보 확인
- 페이징 처리

### 4. 통계 기능
- 벡터 DB 구성 현황 (총 문서 수, 청크 수, 파일 타입별 통계)
- 프롬프트 방식별 이용 현황
- 평균 응답 시간 통계

## 기술 스택

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: MySQL 8.0

### Frontend
- **HTML5**
- **CSS3**
- **JavaScript (Vanilla)**

### AI & ML
- **LLM**: Ollama (로컬)
- **Vector DB**: Chroma
- **Search Engine**: OpenSearch 2.11.0

### Libraries
- **Apache POI**: DOC/DOCX 파싱
- **Apache PDFBox**: PDF 파싱
- **OkHttp**: HTTP 클라이언트
- **Spring Data JPA**: ORM

## 시스템 요구사항

- **Java**: 17 이상
- **Maven**: 3.6 이상
- **Docker**: 20.10 이상
- **Docker Compose**: 2.0 이상
- **메모리**: 최소 8GB RAM (Ollama LLM 실행 시)
- **디스크**: 최소 10GB 여유 공간

## 설치 및 실행

### 1. 프로젝트 클론

```bash
git clone https://github.com/hong9883/ai_rag_summary_test.git
cd ai_rag_summary_test
```

### 2. 인프라 서비스 시작 (Docker Compose)

```bash
# Docker Compose로 MySQL, Ollama, Chroma, OpenSearch 시작
docker-compose up -d

# 서비스 상태 확인
docker-compose ps
```

### 3. Ollama 모델 설치

```bash
# Ollama 컨테이너에 접속
docker exec -it medicine-rag-ollama bash

# 모델 설치 (llama2 예시)
ollama pull llama2

# 또는 다른 모델 설치 가능
# ollama pull mistral
# ollama pull codellama

# 컨테이너 종료
exit
```

### 4. 애플리케이션 설정

`src/main/resources/application.yml` 파일을 확인하고 필요시 수정:

```yaml
# Ollama 모델 설정
ollama:
  model: llama2  # 설치한 모델명으로 변경
```

### 5. 애플리케이션 빌드 및 실행

```bash
# Maven 빌드
./mvnw clean package

# 애플리케이션 실행
./mvnw spring-boot:run

# 또는 빌드된 JAR 실행
java -jar target/medicine-rag-summary-1.0.0.jar
```

### 6. 웹 브라우저로 접속

```
http://localhost:8080
```

## 사용 방법

### 1. 문서 업로드
1. "문서 업로드" 메뉴 클릭
2. PDF, TXT, DOC, DOCX, HWP 파일 선택
3. "업로드" 버튼 클릭
4. 자동으로 텍스트 추출 및 벡터 DB에 저장

### 2. 질문하기
1. "질문하기" 메뉴 클릭
2. 질문 입력
3. 프롬프트 방식 선택
4. "질문하기" 버튼 클릭
5. AI가 생성한 답변 확인

### 3. 이력 조회
1. "질문 이력" 메뉴 클릭
2. 과거 질문 목록 확인
3. "상세" 버튼으로 전체 내용 확인

### 4. 통계 확인
1. "통계" 메뉴 클릭
2. 벡터 DB 현황 및 프롬프트 사용 통계 확인

## API 엔드포인트

### 문서 관리
- `POST /api/documents/upload` - 문서 업로드
- `GET /api/documents` - 문서 목록 조회
- `GET /api/documents/{id}` - 문서 상세 조회
- `DELETE /api/documents/{id}` - 문서 삭제

### 질문-답변
- `POST /api/queries` - 질문 처리
- `GET /api/queries/history` - 질문 이력 조회 (페이징)
- `GET /api/queries/history/{id}` - 질문 이력 상세 조회

### 통계
- `GET /api/statistics` - 전체 통계 조회

## 프로젝트 구조

```
ai_rag_summary_test/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/medicine/rag/
│   │   │       ├── MedicineRagApplication.java
│   │   │       ├── config/          # 설정 클래스
│   │   │       ├── controller/      # REST 컨트롤러
│   │   │       ├── service/         # 비즈니스 로직
│   │   │       ├── repository/      # 데이터 액세스
│   │   │       ├── model/           # 엔티티 모델
│   │   │       ├── dto/             # 데이터 전송 객체
│   │   │       └── util/            # 유틸리티
│   │   └── resources/
│   │       ├── application.yml      # 애플리케이션 설정
│   │       ├── static/              # 정적 리소스
│   │       │   ├── css/
│   │       │   └── js/
│   │       └── templates/           # HTML 템플릿
│   └── test/                        # 테스트 코드
├── docker-compose.yml               # Docker Compose 설정
├── pom.xml                          # Maven 의존성
└── README.md                        # 프로젝트 문서
```

## 문제 해결

### Ollama 연결 오류
- Ollama 서비스가 실행 중인지 확인: `docker-compose ps`
- 모델이 설치되었는지 확인: `docker exec medicine-rag-ollama ollama list`

### MySQL 연결 오류
- MySQL 컨테이너 상태 확인: `docker logs medicine-rag-mysql`
- `application.yml`의 데이터베이스 설정 확인

### Chroma 벡터 DB 오류
- Chroma 서비스 상태 확인: `curl http://localhost:8000/api/v1/heartbeat`

### OpenSearch 오류
- OpenSearch 상태 확인: `curl http://localhost:9200/_cluster/health`

## 개발 환경 설정

### IDE 설정 (IntelliJ IDEA)
1. File > Open > pom.xml 선택
2. Maven 프로젝트로 임포트
3. Java SDK 17 설정
4. Lombok 플러그인 설치

### 로컬 개발 시 주의사항
- Docker 서비스들이 실행 중이어야 함
- `application.yml`의 URL들이 localhost로 설정되어 있는지 확인

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE.md](LICENSE.md) 파일을 참조하세요.

## 기여

이슈 리포트, 기능 제안, 풀 리퀘스트를 환영합니다!

## 연락처

문의사항이 있으시면 이슈를 등록해주세요.

---

**주의**: 이 시스템은 의약품 정보를 참고용으로만 제공합니다. 실제 의약품 사용 전에는 반드시 전문가와 상담하세요.
