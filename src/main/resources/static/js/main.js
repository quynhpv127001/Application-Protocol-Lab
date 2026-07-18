// Hàm ghi log ra giao diện
function logToConsole(message, type = 'info') {
    const consoleDiv = document.getElementById('outputConsole');
    const time = new Date().toLocaleTimeString();
    
    let color = '#4ade80'; // xanh lá cho success
    if (type === 'error') color = '#ef4444'; // đỏ
    if (type === 'warn') color = '#fbbf24'; // vàng

    const logLine = `<div style="color: ${color}; margin-bottom: 4px;">[${time}] ${message}</div>`;
    consoleDiv.innerHTML += logLine;
    consoleDiv.scrollTop = consoleDiv.scrollHeight;
}

// 1. Gọi CRUD API
async function callCrudApi() {
    const method = document.getElementById('crudMethod').value;
    const url = document.getElementById('crudUrl').value;
    const bodyStr = document.getElementById('crudBody').value;
    
    logToConsole(`Đang gọi ${method} ${url}...`, 'warn');
    
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        }
    };
    
    if (method !== 'GET' && method !== 'DELETE') {
        options.body = bodyStr;
    }

    try {
        const response = await fetch(url, options);
        let resData = '';
        if (response.status !== 204) { // DELETE usually returns 204 No Content
            const text = await response.text();
            try {
                resData = JSON.stringify(JSON.parse(text), null, 2);
            } catch (e) {
                resData = text;
            }
        }
        logToConsole(`HTTP ${response.status}\n${resData}`);
    } catch (error) {
        logToConsole(`Lỗi kết nối: ${error.message}`, 'error');
    }
}

// 2. Server-Sent Events (SSE)
let sseSource = null;

function startSse() {
    if (sseSource != null) return;
    
    logToConsole('Đang mở kết nối SSE tới /api/v1/streaming/sse...', 'warn');
    sseSource = new EventSource('/api/v1/streaming/sse');
    
    sseSource.onmessage = function(event) {
        logToConsole(`[SSE Event ID: ${event.lastEventId}] Data: ${event.data}`);
    };
    
    sseSource.onerror = function(err) {
        logToConsole('Đã ngắt kết nối SSE hoặc sự kiện hoàn tất.', 'error');
        stopSse();
    };

    document.getElementById('btnSseStart').style.display = 'none';
    document.getElementById('btnSseStop').style.display = 'inline-flex';
}

function stopSse() {
    if (sseSource) {
        sseSource.close();
        sseSource = null;
        logToConsole('Đã đóng kết nối SSE.', 'warn');
    }
    document.getElementById('btnSseStart').style.display = 'inline-flex';
    document.getElementById('btnSseStop').style.display = 'none';
}
