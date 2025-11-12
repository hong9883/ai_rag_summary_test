// 질문 이력 JavaScript

const API_BASE_URL = '/api';
let currentPage = 0;
let totalPages = 0;
const pageSize = 20;

// 페이지 로드 시 이력 조회
document.addEventListener('DOMContentLoaded', () => {
    loadHistory();
});

// 이력 조회
async function loadHistory(page = 0) {
    try {
        const response = await fetch(`${API_BASE_URL}/queries/history?page=${page}&size=${pageSize}`);
        const data = await response.json();

        currentPage = data.number;
        totalPages = data.totalPages;

        const tbody = document.getElementById('historyBody');
        tbody.innerHTML = '';

        if (data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">질문 이력이 없습니다.</td></tr>';
            return;
        }

        data.content.forEach(history => {
            const row = document.createElement('tr');
            const questionPreview = history.question.length > 50
                ? history.question.substring(0, 50) + '...'
                : history.question;

            row.innerHTML = `
                <td>${history.id}</td>
                <td>${questionPreview}</td>
                <td>${getPromptTypeDescription(history.promptType)}</td>
                <td>${history.responseTime}ms</td>
                <td>${history.relevantDocuments}개</td>
                <td>${formatDate(history.createdAt)}</td>
                <td>
                    <button class="btn btn-primary" onclick="viewDetail(${history.id})">상세</button>
                </td>
            `;
            tbody.appendChild(row);
        });

        updatePagination();
    } catch (error) {
        console.error('Error:', error);
        alert('이력을 불러오는 중 오류가 발생했습니다.');
    }
}

// 상세 조회
async function viewDetail(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/queries/history/${id}`);
        const history = await response.json();

        document.getElementById('detailQuestion').textContent = history.question;
        document.getElementById('detailAnswer').textContent = history.answer;
        document.getElementById('detailContext').textContent = history.retrievedContext || '정보 없음';
        document.getElementById('detailPromptType').textContent = getPromptTypeDescription(history.promptType);
        document.getElementById('detailResponseTime').textContent = history.responseTime;
        document.getElementById('detailRelevantDocs').textContent = history.relevantDocuments;
        document.getElementById('detailCreatedAt').textContent = formatDate(history.createdAt);

        document.getElementById('detailContainer').style.display = 'block';
        document.getElementById('detailContainer').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Error:', error);
        alert('상세 정보를 불러오는 중 오류가 발생했습니다.');
    }
}

// 페이지네이션 업데이트
function updatePagination() {
    document.getElementById('pageInfo').textContent = `${currentPage + 1} / ${totalPages || 1}`;
}

// 이전 페이지
function previousPage() {
    if (currentPage > 0) {
        loadHistory(currentPage - 1);
    }
}

// 다음 페이지
function nextPage() {
    if (currentPage < totalPages - 1) {
        loadHistory(currentPage + 1);
    }
}

// 프롬프트 타입 설명
function getPromptTypeDescription(type) {
    const descriptions = {
        'BASIC': '기본',
        'STRUCTURED': '구조화',
        'SIMPLE': '간단',
        'DETAILED': '상세',
        'POINTS': '포인트',
        'FACT_CHECK': '사실 확인',
        'STEP_BY_STEP': '단계별 사고'
    };
    return descriptions[type] || type;
}

// 날짜 포맷팅
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR');
}
