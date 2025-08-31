// 富文本编辑器
import React, { useState, useCallback, useMemo, useEffect } from 'react';
import { createEditor, Transforms, Editor, Text } from 'slate';
import { Slate, Editable, withReact, useSlate } from 'slate-react';
import { withHistory } from 'slate-history';
import { Button, Space, Tooltip, Dropdown, message } from 'antd';
import {
  BoldOutlined,
  ItalicOutlined,
  UnderlineOutlined,
  CodeOutlined,
  OrderedListOutlined,
  UnorderedListOutlined,
  CommentOutlined,
} from '@ant-design/icons';
import FloatingToolbar from './FloatingToolbar';
import { useWebSocket } from '../../services/websocket';

const DocumentEditor = ({ docId, initialValue }) => {
  const editor = useMemo(() => withHistory(withReact(createEditor())), []);
  const [value, setValue] = useState(initialValue || [
    {
      type: 'paragraph',
      children: [{ text: '开始编辑您的文档...' }],
    },
  ]);
  const [selection, setSelection] = useState(null);
  const [showFloatingToolbar, setShowFloatingToolbar] = useState(false);
  const [collaborators, setCollaborators] = useState([]);
  
  // WebSocket连接用于协同编辑
  const { sendMessage, onMessage } = useWebSocket(docId);

  useEffect(() => {
    // 监听其他用户的编辑
    onMessage('document-change', (data) => {
      if (data.userId !== getCurrentUserId()) {
        Transforms.setNodes(editor, data.operations);
      }
    });

    onMessage('user-cursor', (data) => {
      updateCollaboratorCursor(data);
    });
  }, []);

  const renderElement = useCallback(props => {
    switch (props.element.type) {
      case 'code':
        return <CodeElement {...props} />;
      case 'bulleted-list':
        return <ul {...props.attributes}>{props.children}</ul>;
      case 'numbered-list':
        return <ol {...props.attributes}>{props.children}</ol>;
      case 'list-item':
        return <li {...props.attributes}>{props.children}</li>;
      default:
        return <DefaultElement {...props} />;
    }
  }, []);

  const renderLeaf = useCallback(props => {
    return <Leaf {...props} />;
  }, []);

  const handleChange = (newValue) => {
    setValue(newValue);
    
    // 发送编辑操作到其他用户
    // const operations = editor.operations;
    // if (operations.length > 0) {
    //   sendMessage('document-change', {
    //     docId,
    //     operations,
    //     userId: getCurrentUserId()
    //   });
    // }
  };

  const handleSelect = () => {
    const { selection } = editor;
    setSelection(selection);
    
    if (selection && !Editor.isCollapsed(editor, selection)) {
      setShowFloatingToolbar(true);
    } else {
      setShowFloatingToolbar(false);
    }

    // 发送光标位置
    sendMessage('user-cursor', {
      docId,
      userId: getCurrentUserId(),
      selection
    });
  };

  const toggleMark = (format) => {
    const isActive = isMarkActive(editor, format);
    if (isActive) {
      Editor.removeMark(editor, format);
    } else {
      Editor.addMark(editor, format, true);
    }
  };

  const toggleBlock = (format) => {
    const isActive = isBlockActive(editor, format);
    const isList = ['numbered-list', 'bulleted-list'].includes(format);

    Transforms.unwrapNodes(editor, {
      match: n => ['numbered-list', 'bulleted-list'].includes(n.type),
      split: true,
    });

    const newProperties = {
      type: isActive ? 'paragraph' : isList ? 'list-item' : format,
    };
    Transforms.setNodes(editor, newProperties);

    if (!isActive && isList) {
      const block = { type: format, children: [] };
      Transforms.wrapNodes(editor, block);
    }
  };

  const addComment = () => {
    const { selection } = editor;
    if (selection && !Editor.isCollapsed(editor, selection)) {
      const text = Editor.string(editor, selection);
      message.info(`为 "${text}" 添加评论`);
      // 这里可以实现评论功能
    }
  };

  return (
    <div className="editor-container">
      <Slate 
        editor={editor} 
        value={value} 
        onChange={handleChange}
        onSelect={handleSelect}
      >
        {/* 工具栏 */}
        <div className="editor-toolbar">
          <Space>
            <Tooltip title="加粗">
              <Button
                icon={<BoldOutlined />}
                onMouseDown={(e) => {
                  e.preventDefault();
                  toggleMark('bold');
                }}
              />
            </Tooltip>
            <Tooltip title="斜体">
              <Button
                icon={<ItalicOutlined />}
                onMouseDown={(e) => {
                  e.preventDefault();
                  toggleMark('italic');
                }}
              />
            </Tooltip>
            <Tooltip title="下划线">
              <Button
                icon={<UnderlineOutlined />}
                onMouseDown={(e) => {
                  e.preventDefault();
                  toggleMark('underline');
                }}
              />
            </Tooltip>
            <Tooltip title="代码块">
              <Button
                icon={<CodeOutlined />}
                onMouseDown={(e) => {
                  e.preventDefault();
                  toggleBlock('code');
                }}
              />
            </Tooltip>
            <Tooltip title="有序列表">
              <Button
                icon={<OrderedListOutlined />}
                onMouseDown={(e) => {
                  e.preventDefault();
                  toggleBlock('numbered-list');
                }}
              />
            </Tooltip>
            <Tooltip title="无序列表">
              <Button
                icon={<UnorderedListOutlined />}
                onMouseDown={(e) => {
                  e.preventDefault();
                  toggleBlock('bulleted-list');
                }}
              />
            </Tooltip>
            <Tooltip title="添加评论">
              <Button
                icon={<CommentOutlined />}
                onMouseDown={(e) => {
                  e.preventDefault();
                  addComment();
                }}
              />
            </Tooltip>
          </Space>
        </div>

        {/* 浮动工具栏 */}
        {showFloatingToolbar && (
          <FloatingToolbar
            selection={selection}
            onToggleMark={toggleMark}
            onAddComment={addComment}
          />
        )}

        {/* 编辑区域 */}
        <Editable
          className="editor-content"
          renderElement={renderElement}
          renderLeaf={renderLeaf}
          placeholder="开始编辑您的文档..."
          spellCheck
          autoFocus
        />

        {/* 协作者光标 */}
        {collaborators.map(collaborator => (
          <CollaboratorCursor
            key={collaborator.userId}
            user={collaborator}
            selection={collaborator.selection}
          />
        ))}
      </Slate>
    </div>
  );
};

// 元素组件
const DefaultElement = props => {
  return <p {...props.attributes}>{props.children}</p>;
};

const CodeElement = props => {
  return (
    <pre {...props.attributes}>
      <code>{props.children}</code>
    </pre>
  );
};

// 叶子组件
const Leaf = ({ attributes, children, leaf }) => {
  if (leaf.bold) {
    children = <strong>{children}</strong>;
  }
  if (leaf.italic) {
    children = <em>{children}</em>;
  }
  if (leaf.underline) {
    children = <u>{children}</u>;
  }
  if (leaf.comment) {
    children = (
      <span className="comment-highlight" title={leaf.comment}>
        {children}
      </span>
    );
  }
  return <span {...attributes}>{children}</span>;
};

// 协作者光标组件
const CollaboratorCursor = ({ user, selection }) => {
  if (!selection) return null;
  
  return (
    <div 
      className="collaborator-cursor"
      style={{
        backgroundColor: user.color,
        position: 'absolute',
        // 根据selection计算位置
      }}
    >
      <span className="collaborator-name">{user.name}</span>
    </div>
  );
};

// 工具函数
const isMarkActive = (editor, format) => {
  const marks = Editor.marks(editor);
  return marks ? marks[format] === true : false;
};

const isBlockActive = (editor, format) => {
  const [match] = Editor.nodes(editor, {
    match: n => n.type === format,
  });
  return !!match;
};

const getCurrentUserId = () => {
  // 获取当前用户ID
  return localStorage.getItem('userId') || 'user-' + Date.now();
};

const updateCollaboratorCursor = (data) => {
  // 更新协作者光标位置
};

export default DocumentEditor;