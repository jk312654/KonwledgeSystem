import io from 'socket.io-client';
import { useEffect } from 'react';

class WebSocketService {
  constructor() {
    this.socket = null;
    this.callbacks = {};
  }

  connect(docId) {
    try {
      this.socket = io('ws://localhost:3001', {
        query: { docId },
        timeout: 5000,
        forceNew: true
      });

      this.socket.on('connect', () => {
        console.log('WebSocket connected');
      });

      this.socket.on('disconnect', () => {
        console.log('WebSocket disconnected');
      });

      this.socket.on('connect_error', (error) => {
        console.warn('WebSocket connection failed:', error.message);
        // 可以在这里添加重连逻辑或降级处理
      });
    } catch (error) {
      console.warn('WebSocket initialization failed:', error);
    }
  }

  sendMessage(event, data) {
    if (this.socket) {
      this.socket.emit(event, data);
    }
  }

  onMessage(event, callback) {
    if (this.socket) {
      this.socket.on(event, callback);
    }
  }

  disconnect() {
    if (this.socket) {
      this.socket.disconnect();
    }
  }
}

export const useWebSocket = (docId) => {
  const wsService = new WebSocketService();
  
  useEffect(() => {
    wsService.connect(docId);
    
    return () => {
      wsService.disconnect();
    };
  }, [docId]);

  return {
    sendMessage: wsService.sendMessage.bind(wsService),
    onMessage: wsService.onMessage.bind(wsService)
  };
};