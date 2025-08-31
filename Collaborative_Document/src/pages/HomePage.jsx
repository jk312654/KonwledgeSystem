import React, { useState, useEffect } from 'react';
import { Layout, Card, List, Button, Empty, Space, Typography } from 'antd';
import { PlusOutlined, FolderOutlined, FileTextOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import Sidebar from '../components/Layout/Sidebar';
import Header from '../components/Layout/Header';
import RecentDocuments from '../components/Home/RecentDocuments';
import KnowledgeBaseModel from '../components/KnowledgeBase/KnowledgeBaseModel';

const { Content } = Layout;
const { Title } = Typography;

const HomePage = () => {
  const navigate = useNavigate();
  const [knowledgeBases, setKnowledgeBases] = useState([]);
  const [documents, setDocuments] = useState([]);
  const [modalVisible, setModalVisible] = useState(false);

  useEffect(() => {
    // 模拟获取数据
    fetchKnowledgeBases();
    fetchDocuments();
  }, []);

  const fetchKnowledgeBases = async () => {
    // 模拟API调用
    const mockData = [
      { id: 1, name: '技术文档', icon: '📚', docCount: 12, updatedAt: '2024-01-20' },
      { id: 2, name: '产品规划', icon: '📋', docCount: 8, updatedAt: '2024-01-19' },
      { id: 3, name: '团队协作', icon: '👥', docCount: 5, updatedAt: '2024-01-18' },
    ];
    setKnowledgeBases(mockData);
  };

  const fetchDocuments = async () => {
    // 模拟API调用
    const mockData = [
      { id: 1, title: 'React最佳实践', kbName: '技术文档', updatedAt: '2小时前' },
      { id: 2, title: '2024年产品路线图', kbName: '产品规划', updatedAt: '5小时前' },
      { id: 3, title: '团队周会纪要', kbName: '团队协作', updatedAt: '1天前' },
    ];
    setDocuments(mockData);
  };

  const handleCreateKB = (values) => {
    const newKB = {
      id: Date.now(),
      ...values,
      docCount: 0,
      updatedAt: new Date().toLocaleDateString()
    };
    setKnowledgeBases([newKB, ...knowledgeBases]);
    setModalVisible(false);
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sidebar />
      <Layout>
        <Header />
        <Content style={{ margin: '24px', background: '#f0f2f5' }}>
          <div style={{ maxWidth: 1200, margin: '0 auto' }}>
            {/* 快速操作区 */}
            <Card style={{ marginBottom: 24 }}>
              <Space size="large">
                <Button 
                  type="primary" 
                  icon={<PlusOutlined />}
                  onClick={() => setModalVisible(true)}
                >
                  新建知识库
                </Button>
                <Button 
                  icon={<FileTextOutlined />}
                  onClick={() => navigate('/doc/new')}
                >
                  新建文档
                </Button>
              </Space>
            </Card>

            {/* 知识库列表 */}
            <Card 
              title={<Title level={4}>我的知识库</Title>}
              style={{ marginBottom: 24 }}
            >
              {knowledgeBases.length > 0 ? (
                <List
                  grid={{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 3, xl: 4 }}
                  dataSource={knowledgeBases}
                  renderItem={kb => (
                    <List.Item>
                      <Card 
                        hoverable
                        onClick={() => navigate(`/kb/${kb.id}`)}
                        style={{ textAlign: 'center' }}
                      >
                        <div style={{ fontSize: 48, marginBottom: 8 }}>{kb.icon}</div>
                        <Title level={5}>{kb.name}</Title>
                        <p style={{ color: '#999' }}>{kb.docCount} 篇文档</p>
                      </Card>
                    </List.Item>
                  )}
                />
              ) : (
                <Empty description="暂无知识库" />
              )}
            </Card>

            {/* 最近访问 */}
            <Card title={<Title level={4}>最近访问</Title>}>
              <RecentDocuments documents={documents} />
            </Card>
          </div>
        </Content>
      </Layout>

      <KnowledgeBaseModel
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleCreateKB}
      />
    </Layout>
  );
};

export default HomePage;