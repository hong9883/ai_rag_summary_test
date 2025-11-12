// 문서 업로드 JavaScript

const API_BASE_URL = '/api';

// 페이지 로드 시 문서 목록 조회
document.addEventListener('DOMContentLoaded', () => {
    loadDocuments();
});

// 문서 업로드 폼 제출
document.getElementById('uploadForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const fileInput = document.getElementById('file');
    const file = fileInput.files[0];

    if (!file) {
        showAlert('파일을 선택해주세요.', 'error');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    showLoading(true);
    clearAlert();

    try {
        const response = await fetch(`${API_BASE_URL}/documents/upload`, {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            const result = await response.json();
            showAlert(`문서가 성공적으로 업로드되었습니다. (청크: ${result.chunkCount}개)`, 'success');
            fileInput.value = '';
            loadDocuments();
        } else {
            showAlert('문서 업로드에 실패했습니다.', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('문서 업로드 중 오류가 발생했습니다.', 'error');
    } finally {
        showLoading(false);
    }
});

// 문서 목록 조회
async function loadDocuments() {
    try {
        const response = await fetch(`${API_BASE_URL}/documents`);
        const documents = await response.json();

        const tbody = document.getElementById('documentsBody');
        tbody.innerHTML = '';

        if (documents.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">업로드된 문서가 없습니다.</td></tr>';
            return;
        }

        documents.forEach(doc => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${doc.id}</td>
                <td>${doc.originalFileName}</td>
                <td>${doc.fileType.toUpperCase()}</td>
                <td>${formatFileSize(doc.fileSize)}</td>
                <td>${doc.chunkCount}</td>
                <td>${formatDate(doc.createdAt)}</td>
                <td>
                    <button class="btn btn-danger" onclick="deleteDocument(${doc.id})">삭제</button>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('Error:', error);
        showAlert('문서 목록을 불러오는 중 오류가 발생했습니다.', 'error');
    }
}

// 문서 삭제
async function deleteDocument(id) {
    if (!confirm('정말로 이 문서를 삭제하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/documents/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showAlert('문서가 삭제되었습니다.', 'success');
            loadDocuments();
        } else {
            showAlert('문서 삭제에 실패했습니다.', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('문서 삭제 중 오류가 발생했습니다.', 'error');
    }
}

// 파일 크기 포맷팅
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

// 날짜 포맷팅
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR');
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
