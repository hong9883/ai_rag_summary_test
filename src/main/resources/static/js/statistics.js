// 통계 JavaScript

const API_BASE_URL = '/api';

// 페이지 로드 시 통계 조회
document.addEventListener('DOMContentLoaded', () => {
    loadStatistics();
});

// 통계 조회
async function loadStatistics() {
    try {
        const response = await fetch(`${API_BASE_URL}/statistics`);
        const data = await response.json();

        // 벡터 DB 통계
        displayVectorDBStats(data.vectorDBStats);

        // 프롬프트 사용 통계
        displayPromptUsageStats(data.promptUsageStats);
    } catch (error) {
        console.error('Error:', error);
        alert('통계를 불러오는 중 오류가 발생했습니다.');
    }
}

// 벡터 DB 통계 표시
function displayVectorDBStats(stats) {
    document.getElementById('totalDocuments').textContent = stats.totalDocuments || 0;
    document.getElementById('totalChunks').textContent = stats.totalChunks || 0;
    document.getElementById('totalVectorSize').textContent = stats.totalVectorSize || 0;

    // 파일 타입별 문서 수
    const tbody = document.getElementById('documentsByTypeBody');
    tbody.innerHTML = '';

    if (!stats.documentsByType || Object.keys(stats.documentsByType).length === 0) {
        tbody.innerHTML = '<tr><td colspan="2" style="text-align: center;">데이터가 없습니다.</td></tr>';
        return;
    }

    for (const [fileType, count] of Object.entries(stats.documentsByType)) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${fileType.toUpperCase()}</td>
            <td>${count}</td>
        `;
        tbody.appendChild(row);
    }
}

// 프롬프트 사용 통계 표시
function displayPromptUsageStats(stats) {
    document.getElementById('totalQueries').textContent = stats.totalQueries || 0;

    // 프롬프트 방식별 사용 현황
    const tbody = document.getElementById('promptUsageBody');
    tbody.innerHTML = '';

    if (!stats.usageByPromptType || Object.keys(stats.usageByPromptType).length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" style="text-align: center;">데이터가 없습니다.</td></tr>';
        return;
    }

    for (const [promptType, count] of Object.entries(stats.usageByPromptType)) {
        const avgTime = stats.avgResponseTimeByPromptType[promptType];
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${promptType}</td>
            <td>${count}</td>
            <td>${avgTime ? Math.round(avgTime) + 'ms' : '-'}</td>
        `;
        tbody.appendChild(row);
    }
}
