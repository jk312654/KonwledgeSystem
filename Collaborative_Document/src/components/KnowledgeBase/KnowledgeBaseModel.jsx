// 知识库管理弹窗
import React from 'react';
import { Modal, Form, Input, Select } from 'antd';

const { Option } = Select;

const KnowledgeBaseModel = ({ visible, onCancel, onOk, initialValues }) => {
  const [form] = Form.useForm();

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      onOk(values);
      form.resetFields();
    } catch (error) {
      console.error('Validation failed:', error);
    }
  };

  return (
    <Modal
      title={initialValues ? '编辑知识库' : '新建知识库'}
      open={visible}
      onCancel={onCancel}
      onOk={handleOk}
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={initialValues}
      >
        <Form.Item
          name="name"
          label="知识库名称"
          rules={[{ required: true, message: '请输入知识库名称' }]}
        >
          <Input placeholder="请输入知识库名称" />
        </Form.Item>
        
        <Form.Item
          name="description"
          label="描述"
        >
          <Input.TextArea 
            placeholder="请输入知识库描述" 
            rows={3}
          />
        </Form.Item>
        
        <Form.Item
          name="icon"
          label="图标"
          initialValue="📚"
        >
          <Select>
            <Option value="📚">📚 书籍</Option>
            <Option value="📋">📋 文档</Option>
            <Option value="💡">💡 想法</Option>
            <Option value="🎯">🎯 目标</Option>
            <Option value="👥">👥 团队</Option>
            <Option value="🔧">🔧 工具</Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default KnowledgeBaseModel;