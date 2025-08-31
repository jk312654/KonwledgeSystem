const WebSocket = require('ws');
const http = require('http');
const Y = require('yjs');

const server = http.createServer();
const wss = new WebSocket.Server({ server });

// 存储文档
const docs = new Map();

wss.on('connection', (ws, req) => {
  console.log('WebSocket connected');
  
  // 简单的消息处理
  ws.on('message', (message) => {
    console.log('Received message:', message);
    // 这里可以添加 Yjs 文档同步逻辑
  });
  
  ws.on('close', () => {
    console.log('WebSocket disconnected');
  });
});

const port = 1234;
server.listen(port, () => {
  console.log(`WebSocket 服务器运行在 ws://localhost:${port}`);
});