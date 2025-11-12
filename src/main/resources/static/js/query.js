// 질문 JavaScript

const API_BASE_URL = '/api';

// 질문 폼 제출
document.getElementById('queryForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const question = document.getElementById('question').value;
    const promptType = document.getElementById('promptType').value;
    const topK = parseInt(document.getElementById('topK').value);

    if (!question.trim()) {
        showAlert('질문을 입력해주세요.', 'error');
        return;
    }

    const requestData = {
        question: question,
        promptType: promptType,
        topK: topK
    };

    showLoading(true);
    clearAlert();
    hideAnswer();

    try {
        const response = await fetch(`${API_BASE_URL}/queries`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        });

        if (response.ok) {
            const result = await response.json();
            displayAnswer(result);
            showAlert('답변이 생성되었습니다.', 'success');
        } else {
            showAlert('답변 생성에 실패했습니다.', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('답변 생성 중 오류가 발생했습니다.', 'error');
    } finally {
        showLoading(false);
    }
});

// 답변 표시
function displayAnswer(result) {
    document.getElementById('answerQuestion').textContent = result.question;
    document.getElementById('answerText').textContent = result.answer;
    document.getElementById('answerPromptType').textContent = getPromptTypeDescription(result.promptType);
    document.getElementById('answerResponseTime').textContent = result.responseTime;
    document.getElementById('answerRelevantDocs').textContent = result.relevantDocuments;

    // 출처 표시
    if (result.sources && result.sources.length > 0) {
        const sourcesList = document.getElementById('sourcesList');
        sourcesList.innerHTML = '';
        result.sources.forEach(source => {
            const tag = document.createElement('span');
            tag.className = 'source-tag';
            tag.textContent = source;
            sourcesList.appendChild(tag);
        });
        document.getElementById('sourcesContainer').style.display = 'block';
    } else {
        document.getElementById('sourcesContainer').style.display = 'none';
    }

    document.getElementById('answerContainer').style.display = 'block';

    // 답변으로 스크롤
    document.getElementById('answerContainer').scrollIntoView({ behavior: 'smooth' });
}

// 답변 숨기기
function hideAnswer() {
    document.getElementById('answerContainer').style.display = 'none';
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

// 알림 표시
function showAlert(message, type) {
    const alertContainer = document.getElementById('alertContainer');
    const alertClass = type === 'success' ? 'alert-success' : 'alert-error';
    alertContainer.innerHTML = `<div class="alert ${alertClass}">${message}</div>`;
}

// 알림 제거
function clearAlert() {
    document.getElementById('alertContainer').innerHTML = '';
}

// 로딩 표시
function showLoading(show) {
    document.getElementById('loading').style.display = show ? 'block' : 'none';
}
