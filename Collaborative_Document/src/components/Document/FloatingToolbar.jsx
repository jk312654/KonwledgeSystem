// 浮动工具栏
import React, { useEffect, useRef, useState } from 'react';
import { Button, Space } from 'antd';
import {
  BoldOutlined,
  ItalicOutlined,
  UnderlineOutlined,
  CommentOutlined,
} from '@ant-design/icons';
import { Editor, Range } from 'slate';
import { ReactEditor, useSlate } from 'slate-react';

const FloatingToolbar = ({ selection, onToggleMark, onAddComment }) => {
  const ref = useRef();
  const editor = useSlate();
  const [position, setPosition] = useState({ top: 0, left: 0 });

  useEffect(() => {
    if (!selection || Range.isCollapsed(selection)) {
      return;
    }

    const domSelection = window.getSelection();
    if (domSelection.rangeCount === 0) {
      return;
    }

    const domRange = domSelection.getRangeAt(0);
    const rect = domRange.getBoundingClientRect();
    
    setPosition({
      top: rect.top + window.pageYOffset - 40,
      left: rect.left + window.pageXOffset + rect.width / 2 - 100,
    });
  }, [selection]);

  return (
    <div
      ref={ref}
      className="floating-toolbar"
      style={{
        position: 'absolute',
        top: position.top,
        left: position.left,
        zIndex: 1000,
        backgroundColor: '#fff',
        border: '1px solid #d9d9d9',
        borderRadius: '4px',
        padding: '4px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
      }}
    >
      <Space size="small">
        <Button
          size="small"
          icon={<BoldOutlined />}
          onMouseDown={(e) => {
            e.preventDefault();
            onToggleMark('bold');
          }}
        />
        <Button
          size="small"
          icon={<ItalicOutlined />}
          onMouseDown={(e) => {
            e.preventDefault();
            onToggleMark('italic');
          }}
        />
        <Button
          size="small"
          icon={<UnderlineOutlined />}
          onMouseDown={(e) => {
            e.preventDefault();
            onToggleMark('underline');
          }}
        />
        <Button
          size="small"
          icon={<CommentOutlined />}
          onMouseDown={(e) => {
            e.preventDefault();
            onAddComment();
          }}
        />
      </Space>
    </div>
  );
};

export default FloatingToolbar;