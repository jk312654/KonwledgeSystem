import React, { useState, useRef, useEffect, useMemo, useCallback } from 'react';
import ReactQuill from 'react-quill';
import 'quill/dist/quill.snow.css';
import { Button, Space, message } from 'antd';
import { SaveOutlined, ShareAltOutlined } from '@ant-design/icons';

const QuillDocumentEditor = ({ docId, initialValue }) => {
  const [value, setValue] = useState(initialValue || '');
  const quillRef = useRef();

  // 图片上传处理
  const handleImageUpload = useCallback(() => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();

    input.onchange = async () => {
      const file = input.files[0];
      if (file) {
        const formData = new FormData();
        formData.append('image', file);
        
        try {
          const imageUrl = URL.createObjectURL(file);
          
          const quill = quillRef.current.getEditor();
          const range = quill.getSelection();
          quill.insertEmbed(range.index, 'image', imageUrl);
        } catch (error) {
          message.error('图片上传失败');
        }
      }
    };
  }, []);

  // 保存文档
  const handleSave = useCallback(async () => {
    try {
      message.success('文档保存成功');
    } catch (error) {
      message.error('保存失败');
    }
  }, [docId, value]);

  // 防抖自动保存
  const debounceAutoSave = useCallback(
    debounce((content) => {
      console.log('自动保存:', content);
    }, 2000),
    []
  );

  // 使用 useMemo 确保 modules 在函数定义后初始化
  const modules = useMemo(() => ({
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
        ['link', 'image', 'video', 'formula'],
        ['code-block'],
        ['clean']
      ],
      handlers: {
        'image': handleImageUpload,
        'save': handleSave
      }
    },
    clipboard: {
      matchVisual: false
    },
    history: {
      delay: 1000,
      maxStack: 50,
      userOnly: true
    }
  }), [handleImageUpload, handleSave]);

  const formats = [
    'header', 'font', 'size',
    'bold', 'italic', 'underline', 'strike',
    'color', 'background',
    'script',
    'list', 'bullet', 'indent',
    'direction', 'align',
    'link', 'image', 'video', 'formula',
    'code-block'
  ];

  // 处理内容变化
  const handleChange = (content, delta, source, editor) => {
    setValue(content);
    
    if (source === 'user') {
      debounceAutoSave(content);
    }
  };

  return (
    <div className="quill-editor-container">
      <div className="editor-header">
        <Space>
          <Button 
            icon={<SaveOutlined />} 
            onClick={handleSave}
            type="primary"
          >
            保存
          </Button>
          <Button icon={<ShareAltOutlined />}>
            分享
          </Button>
        </Space>
      </div>

      <ReactQuill
        ref={quillRef}
        theme="snow"
        value={value}
        onChange={handleChange}
        modules={modules}
        formats={formats}
        placeholder="开始编辑您的文档..."
        style={{ height: 'calc(100vh - 200px)' }}
      />
    </div>
  );
};

// 防抖函数
function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

export default QuillDocumentEditor;