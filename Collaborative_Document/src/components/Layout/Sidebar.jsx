import React from 'react';
import { Layout, Menu } from 'antd';
import { 
  HomeOutlined, 
  FolderOutlined, 
  FileTextOutlined,
  SettingOutlined 
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';

const { Sider } = Layout;

const Sidebar = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = [
    {
      key: '/',
      icon: <HomeOutlined />,
      label: '首页',
    },
    {
      key: '/knowledge-bases',
      icon: <FolderOutlined />,
      label: '知识库',
    },
    {
      key: '/documents',
      icon: <FileTextOutlined />,
      label: '文档',
    },
    {
      key: '/settings',
      icon: <SettingOutlined />,
      label: '设置',
    },
  ];

  return (
    <Sider theme="dark" width={200}>
      <div className="sidebar-logo">
        协同文档
      </div>
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        onClick={({ key }) => navigate(key)}
      />
    </Sider>
  );
};

export default Sidebar;