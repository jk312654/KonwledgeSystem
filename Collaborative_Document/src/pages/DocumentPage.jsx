import React from 'react';
import { Layout } from 'antd';
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import Sidebar from '../components/Layout/Sidebar.jsx';
import Header from '../components/Layout/Header.jsx';
import DocumentEditor from '../components/Document/DocumentEditor.jsx';
import QuillDocumentEditor from '../components/Document/QuillDocumentEditor';
import CollaborativeQuillEditor from '../components/Document/CollaborativeQuillEditor';

const { Content } = Layout;

const DocumentPage = () => {
    // 从URL参数中获取docId
    const { docId } = useParams();
    // 初始文档内容（可以从API获取）
    const currentUser = useSelector(state => state.user.currentUser);
    return (
        <Layout style={{ minHeight: '100vh' }}>
          <Sidebar />
          <Layout style={{ flex: 1 }}>
            <Header />
            <Content style={{ 
              margin: '16px 12px', // 减少左右边距
              background: '#fff',
              padding: '0' // 移除内边距
            }}>
              {/* <DocumentEditor docId="1" /> */}
              {/* <QuillDocumentEditor 
                docId={docId} 
                initialValue={documentContent}/> */}
              <CollaborativeQuillEditor 
                docId={docId} 
                currentUser={currentUser}
              />
            </Content>
          </Layout>
        </Layout>
      );
};

export default DocumentPage;