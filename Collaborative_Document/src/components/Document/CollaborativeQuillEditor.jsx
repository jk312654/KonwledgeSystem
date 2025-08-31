import React, { useEffect, useRef, useState } from 'react';
import ReactQuill from 'react-quill';
import 'quill/dist/quill.snow.css';
import { Button, Space, message, Avatar, Tooltip } from 'antd';
import { SaveOutlined, ShareAltOutlined, UserOutlined } from '@ant-design/icons';
import * as Y from 'yjs';
import { QuillBinding } from 'y-quill';
import { WebsocketProvider } from 'y-websocket';

const CollaborativeQuillEditor = ({ docId, initialValue, currentUser }) => {
  const quillRef = useRef();
  const [collaborators, setCollaborators] = useState(new Map());
  const [ydoc] = useState(() => new Y.Doc());
  const [provider, setProvider] = useState(null);
  const [binding, setBinding] = useState(null);
  const [isConnected, setIsConnected] = useState(false);

  // 生成随机颜色
  const generateUserColor = (userId) => {
    const colors = [
      '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', 
      '#FFEAA7', '#DDA0DD', '#98D8C8', '#F7DC6F',
      '#BB8FCE', '#85C1E9', '#F8C471', '#82E0AA'
    ];
    const hash = userId.split('').reduce((a, b) => {
      a = ((a << 5) - a) + b.charCodeAt(0);
      return a & a;
    }, 0);
    return colors[Math.abs(hash) % colors.length];
  };

  // Quill 模块配置
  const modules = {
    toolbar: {
      container: [
        [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
        [{ 'font': [] }],
        [{ 'size': ['small', false, 'large', 'huge'] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{ 'color': [] }, { 'background': [] }],
        [{ 'script': 'sub' }, { 'script': 'super' }],
        [{ 'list': 'ordered' }, { 'list': 'bullet' }],
        [{ 'indent': '-1' }, { 'indent': '+1' }],
        [{ 'direction': 'rtl' }],
        [{ 'align': [] }],
        ['link', 'image', 'video'],
        ['code-block'],
        ['clean']
      ]
    },
    history: {
      userOnly: true // 只记录用户操作，不记录协同操作
    }
  };

  const formats = [
    'header', 'font', 'size',
    'bold', 'italic', 'underline', 'strike',
    'color', 'background',
    'script',
    'list', 'bullet', 'indent',
    'direction', 'align',
    'link', 'image', 'video',
    'code-block'
  ];

  useEffect(() => {
    if (!quillRef.current) return;

    const quill = quillRef.current.getEditor();
    
    // 创建 WebSocket 提供者
    const wsProvider = new WebsocketProvider(
      'ws://localhost:1234', // WebSocket 服务器地址
      `document-${docId}`,
      ydoc
    );

    // 监听连接状态
    wsProvider.on('status', (event) => {
      setIsConnected(event.status === 'connected');
      if (event.status === 'connected') {
        message.success('已连接到协同编辑服务器');
      } else if (event.status === 'disconnected') {
        message.warning('与协同编辑服务器断开连接');
      }
    });

    // 设置用户信息
    wsProvider.awareness.setLocalStateField('user', {
      id: currentUser.id,
      name: currentUser.name,
      color: generateUserColor(currentUser.id),
      avatar: currentUser.avatar
    });

    // 监听其他用户状态变化
    wsProvider.awareness.on('change', () => {
      const states = wsProvider.awareness.getStates();
      const newCollaborators = new Map();
      
      states.forEach((state, clientId) => {
        if (clientId !== wsProvider.awareness.clientID && state.user) {
          newCollaborators.set(clientId, state.user);
        }
      });
      
      setCollaborators(newCollaborators);
    });

    // 创建 Yjs 文本类型
    const ytext = ydoc.getText('quill');
    
    // 创建 Quill 绑定
    const quillBinding = new QuillBinding(ytext, quill, wsProvider.awareness);

    setProvider(wsProvider);
    setBinding(quillBinding);

    // 清理函数
    return () => {
      quillBinding.destroy();
      wsProvider.destroy();
    };
  }, [docId, currentUser, ydoc]);

  // 保存文档
  const handleSave = async () => {
    try {
      const content = quillRef.current.getEditor().getContents();
      // 这里可以将内容保存到服务器
      console.log('保存内容:', content);
      message.success('文档保存成功');
    } catch (error) {
      message.error('保存失败');
    }
  };

  // 分享文档
  const handleShare = () => {
    const shareUrl = `${window.location.origin}/doc/${docId}`;
    navigator.clipboard.writeText(shareUrl).then(() => {
      message.success('分享链接已复制到剪贴板');
    }).catch(() => {
      message.error('复制失败');
    });
  };

  return (
    <div className="collaborative-editor-container">
      {/* 编辑器头部 */}
      <div className="editor-header">
        <div className="editor-actions">
          <Space>
            <Button 
              icon={<SaveOutlined />} 
              onClick={handleSave}
              type="primary"
            >
              保存
            </Button>
            <Button 
              icon={<ShareAltOutlined />}
              onClick={handleShare}
            >
              分享
            </Button>
          </Space>
        </div>
        
        {/* 协作者列表 */}
        <div className="collaborators-list">
          <Space>
            <span className="connection-status">
              <span 
                className={`status-dot ${isConnected ? 'connected' : 'disconnected'}`}
              />
              {isConnected ? '已连接' : '未连接'}
            </span>
            
            {Array.from(collaborators.values()).map((user, index) => (
              <Tooltip key={user.id} title={`${user.name} 正在编辑`}>
                <Avatar 
                  size="small"
                  style={{ 
                    backgroundColor: user.color,
                    border: `2px solid ${user.color}`,
                    marginLeft: index > 0 ? -8 : 0
                  }}
                  icon={user.avatar ? undefined : <UserOutlined />}
                  src={user.avatar}
                >
                  {!user.avatar && user.name.charAt(0).toUpperCase()}
                </Avatar>
              </Tooltip>
            ))}
            
            {collaborators.size > 0 && (
              <span className="collaborator-count">
                {collaborators.size} 人正在协作
              </span>
            )}
          </Space>
        </div>
      </div>

      {/* Quill 编辑器 */}
      <ReactQuill
        ref={quillRef}
        theme="snow"
        modules={modules}
        formats={formats}
        placeholder="开始协同编辑您的文档..."
        style={{ height: 'calc(100vh - 200px)' }}
      />
    </div>
  );
};

export default CollaborativeQuillEditor;