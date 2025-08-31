import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Provider } from 'react-redux';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import store from './store/index.js';  // 添加扩展名
import HomePage from './pages/HomePage.jsx';  // 添加扩展名
import KnowledgeBasePage from './pages/KnowledgeBasePage.jsx';  // 添加扩展名
import DocumentPage from './pages/DocumentPage.jsx';  // 添加扩展名
import './App.css';

function App() {
  return (
    <Provider store={store}>
      <ConfigProvider locale={zhCN}>
        <Router>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/kb/:kbId" element={<KnowledgeBasePage />} />
            <Route path="/doc/:docId" element={<DocumentPage />} />
          </Routes>
        </Router>
      </ConfigProvider>
    </Provider>
  );
}

export default App;