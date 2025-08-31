import React from 'react';
import { List, Typography, Space } from 'antd';
import { FileTextOutlined, ClockCircleOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const { Text } = Typography;

const RecentDocuments = ({ documents }) => {
  const navigate = useNavigate();

  return (
    <List
      itemLayout="horizontal"
      dataSource={documents}
      renderItem={item => (
        <List.Item 
          className="document-list-item"
          onClick={() => navigate(`/doc/${item.id}`)}
        >
          <List.Item.Meta
            avatar={<FileTextOutlined style={{ fontSize: 24 }} />}
            title={item.title}
            description={
              <Space>
                <Text type="secondary">{item.kbName}</Text>
                <Text type="secondary">•</Text>
                <Space size={4}>
                  <ClockCircleOutlined />
                  <Text type="secondary">{item.updatedAt}</Text>
                </Space>
              </Space>
            }
          />
        </List.Item>
      )}
    />
  );
};

export default RecentDocuments;