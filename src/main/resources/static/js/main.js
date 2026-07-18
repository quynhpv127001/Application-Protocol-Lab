// Hàm escape HTML để hiển thị an toàn
function escapeHtml(unsafe) {
    if (unsafe == null) return "";
    return unsafe.toString()
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
}

// Hàm ghi log ra giao diện
function logToConsole(message, type = 'info') {
    const consoleDiv = document.getElementById('outputConsole');
    const time = new Date().toLocaleTimeString();
    
    let color = '#4ade80'; // xanh lá cho success
    if (type === 'error') color = '#ef4444'; // đỏ
    if (type === 'warn') color = '#fbbf24'; // vàng

    const escapedMessage = escapeHtml(message);
    const logLine = `<div style="color: ${color}; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px dashed #334155;">
        <span style="color: #94a3b8; font-size: 0.75rem;">[${time}]</span>
        <pre style="margin: 4px 0 0 0; white-space: pre-wrap; font-family: inherit;">${escapedMessage}</pre>
    </div>`;
    consoleDiv.innerHTML += logLine;
    consoleDiv.scrollTop = consoleDiv.scrollHeight;
}

// =====================================
// 1. CRUD API
// =====================================
async function fetchUsers() {
    logToConsole('Đang gọi GET /api/v1/users...', 'warn');
    try {
        const res = await fetch('/api/v1/users');
        logToConsole(`[GET] HTTP ${res.status}\n${JSON.stringify(await res.json(), null, 2)}`);
    } catch (e) { logToConsole(e.message, 'error'); }
}

async function createUser() {
    logToConsole('Đang gọi POST /api/v1/users...', 'warn');
    try {
        const payload = { name: "Alex (New)", email: `alex${Date.now()}@example.com` };
        const res = await fetch('/api/v1/users', {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        logToConsole(`[POST] HTTP ${res.status}\n${JSON.stringify(data, null, 2)}`);
        
        // Auto-fill the ID input for convenience
        if (data.id) document.getElementById('userIdInput').value = data.id;
    } catch (e) { logToConsole(e.message, 'error'); }
}

async function updateUser(method) {
    const id = document.getElementById('userIdInput').value;
    if (!id) return logToConsole('Vui lòng nhập User ID trước!', 'error');
    logToConsole(`Đang gọi ${method} /api/v1/users/${id}...`, 'warn');
    try {
        const payload = method === 'PUT' 
            ? { name: "Name Replaced (PUT)", email: "put@example.com" } 
            : { name: "Name Patched (PATCH)" };
        
        const res = await fetch(`/api/v1/users/${id}`, {
            method: method, headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const text = await res.text();
        logToConsole(`[${method}] HTTP ${res.status}\n${text ? JSON.stringify(JSON.parse(text), null, 2) : ''}`);
    } catch (e) { logToConsole(e.message, 'error'); }
}

async function deleteUser() {
    const id = document.getElementById('userIdInput').value;
    if (!id) return logToConsole('Vui lòng nhập User ID trước!', 'error');
    logToConsole(`Đang gọi DELETE /api/v1/users/${id}...`, 'warn');
    try {
        const res = await fetch(`/api/v1/users/${id}`, { method: 'DELETE' });
        logToConsole(`[DELETE] HTTP ${res.status}`);
    } catch (e) { logToConsole(e.message, 'error'); }
}

// =====================================
// 2. FILE I/O
// =====================================
async function uploadFile() {
    const fileInput = document.getElementById('fileInput');
    if (!fileInput.files.length) return logToConsole('Chưa chọn file để upload!', 'error');
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);
    logToConsole('Đang upload file (Multipart Form Data)...', 'warn');
    try {
        const res = await fetch('/api/v1/files/upload', { method: 'POST', body: formData });
        const text = await res.text();
        logToConsole(`[UPLOAD] HTTP ${res.status}\nĐã lưu file lên RAM với tên: ${text}`);
        document.getElementById('fileNameInput').value = text.trim();
    } catch (e) { logToConsole(e.message, 'error'); }
}

function downloadFile(stream) {
    const fileName = document.getElementById('fileNameInput').value;
    if (!fileName) return logToConsole('Vui lòng nhập Tên File cần tải!', 'error');
    const url = stream ? `/api/v1/files/download-stream/${fileName}` : `/api/v1/files/download/${fileName}`;
    logToConsole(`Đang chuyển hướng trình duyệt tải file: ${url}`, 'info');
    window.open(url, '_blank');
}

// =====================================
// 3. LONG POLLING
// =====================================
async function subscribeLongPolling() {
    const id = document.getElementById('lpIdInput').value;
    logToConsole(`[LP Subscribe] Đang treo kết nối chờ data cho room: ${id}...`, 'warn');
    try {
        const res = await fetch(`/api/v1/streaming/long-polling/${id}`);
        logToConsole(`[LP Nhận Data] HTTP ${res.status}\n${await res.text()}`);
    } catch (e) { logToConsole(e.message, 'error'); }
}

async function triggerLongPolling() {
    const id = document.getElementById('lpIdInput').value;
    logToConsole(`[LP Trigger] Bắn dữ liệu đánh thức room: ${id}...`, 'info');
    try {
        const res = await fetch(`/api/v1/streaming/long-polling/${id}/trigger`, {
            method: 'POST', headers: { 'Content-Type': 'text/plain' }, body: 'Data Triggered at ' + new Date().toLocaleTimeString()
        });
        logToConsole(`[LP Trigger Xong] HTTP ${res.status}\n${await res.text()}`);
    } catch (e) { logToConsole(e.message, 'error'); }
}

// =====================================
// 4. SSE
// =====================================
let sseSource = null;
function startSse() {
    if (sseSource != null) return;
    logToConsole('Đang mở kết nối SSE tới /api/v1/streaming/sse...', 'warn');
    sseSource = new EventSource('/api/v1/streaming/sse');
    sseSource.onmessage = (event) => logToConsole(`[SSE Data] ID: ${event.lastEventId} | ${event.data}`);
    sseSource.onerror = (err) => { logToConsole('Đã ngắt SSE.', 'error'); stopSse(); };
    document.getElementById('btnSseStart').style.display = 'none';
    document.getElementById('btnSseStop').style.display = 'inline-flex';
}
function stopSse() {
    if (sseSource) { sseSource.close(); sseSource = null; logToConsole('Đã đóng kết nối SSE.', 'info'); }
    document.getElementById('btnSseStart').style.display = 'inline-flex';
    document.getElementById('btnSseStop').style.display = 'none';
}

// =====================================
// 5. EXTERNAL API (RestClient)
// =====================================
async function testExternalApi() {
    const url = document.getElementById('externalUrlInput').value;
    logToConsole(`Đang gọi External API thông qua Backend RestClient...`, 'warn');
    try {
        const res = await fetch(`/api/v1/external-api/fetch?url=${encodeURIComponent(url)}`);
        const text = await res.text();
        try {
            logToConsole(`[External] HTTP ${res.status}\n${JSON.stringify(JSON.parse(text), null, 2)}`);
        } catch(e) {
            logToConsole(`[External] HTTP ${res.status}\n${text}`);
        }
    } catch (e) { logToConsole(e.message, 'error'); }
}
