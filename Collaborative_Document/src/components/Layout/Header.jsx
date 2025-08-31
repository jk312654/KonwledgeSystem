import React from 'react';
import { Layout, Input, Avatar, Dropdown, Space } from 'antd';
import { SearchOutlined, UserOutlined, BellOutlined } from '@ant-design/icons';

const { Header: AntHeader } = Layout;
const { Search } = Input;

const Header = () => {
  const userMenuItems = [
    { key: 'profile', label: '个人资料' },
    { key: 'settings', label: '设置' },
    { type: 'divider' },
    { key: 'logout', label: '退出登录' },
  ];

  return (
    <AntHeader style={{ background: '#fff', padding: '0 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <Search
        placeholder="搜索文档..."
        prefix={<SearchOutlined />}
        style={{ width: 300 }}
      />
      
      <Space size="large">
        <BellOutlined style={{ fontSize: 18 }} />
        <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
          <Avatar icon={<UserOutlined />} style={{ cursor: 'pointer' }} />
        </Dropdown>
      </Space>
    </AntHeader>
  );
};

export default Header;