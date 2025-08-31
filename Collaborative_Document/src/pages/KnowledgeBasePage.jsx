import React from 'react';
import { Layout, Typography } from 'antd';
import Sidebar from '../components/Layout/Sidebar.jsx';
import Header from '../components/Layout/Header.jsx';

const { Content } = Layout;
const { Title } = Typography;

const KnowledgeBasePage = () => {
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sidebar />
      <Layout>
        <Header />
        <Content style={{ margin: '24px' }}>
          <Title level={2}>知识库页面</Title>
        </Content>
      </Layout>
    </Layout>
  );
};

export default KnowledgeBasePage;