import React, { useState, useEffect } from 'react';
import { 
  Card, 
  List, 
  Button, 
  Space, 
  Dropdown, 
  Modal, 
  Empty, 
  Input,
  Tag,
  Avatar,
  Tooltip,
  message,
  Spin
} from 'antd';
import {
  FolderOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  ShareAltOutlined,
  MoreOutlined,
  TeamOutlined,
  FileTextOutlined,
  LockOutlined,
  GlobalOutlined,
  SearchOutlined,
  StarOutlined,
  StarFilled,
  ClockCircleOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import KnowledgeBaseModal from './KnowledgeBaseModal';
import './KnowledgeBaseList.css';

const { Search } = Input;
const { confirm } = Modal;

const KnowledgeBaseList = () => {
  const navigate = useNavigate();
  const [knowledgeBases, setKnowledgeBases] = useState([]);
  const [filteredKBs, setFilteredKBs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingKB, setEditingKB] = useState(null);
  const [searchText, setSearchText] = useState('');
  const [viewMode, setViewMode] = useState('grid'); // 'grid' or 'list'

  useEffect(() => {
    fetchKnowledgeBases();
  }, []);

  useEffect(() => {
    // 搜索过滤
    const filtered = knowledgeBases.filter(kb => 
      kb.name.toLowerCase().includes(searchText.toLowerCase()) ||
      kb.description?.toLowerCase().includes(searchText.toLowerCase())
    );
    setFilteredKBs(filtered);
  }, [searchText, knowledgeBases]);

  const fetchKnowledgeBases = async () => {
    setLoading(true);
    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const mockData = [
        {
          id: 1,
          name: '技术文档',
          description: '团队技术文档和开发规范',
          icon: '📚',
          color: '#1890ff',
          docCount: 42,
          memberCount: 8,
          isPublic: false,
          isFavorite: true,
          owner: { id: 1, name: '张三', avatar: null },
          createdAt: '2024-01-15',
          updatedAt: '2024-01-20 14:30',
          lastVisited: '2小时前',
          permissions: ['read', 'write', 'admin']
        },
        {
          id: 2,
          name: '产品规划',
          description: '产品路线图和需求文档',
          icon: '📋',
          color: '#52c41a',
          docCount: 28,
          memberCount: 5,
          isPublic: false,
          isFavorite: true,
          owner: { id: 2, name: '李四', avatar: null },
          createdAt: '2024-01-10',
          updatedAt: '2024-01-19 16:45',
          lastVisited: '1天前',
          permissions: ['read', 'write']
        },
        {
          id: 3,
          name: '团队协作',
          description: '团队管理和协作文档',
          icon: '👥',
          color: '#722ed1',
          docCount: 15,
          memberCount: 12,
          isPublic: true,
          isFavorite: false,
          owner: { id: 3, name: '王五', avatar: null },
          createdAt: '2024-01-05',
          updatedAt: '2024-01-18 10:20',
          lastVisited: '3天前',
          permissions: ['read']
        },
        {
          id: 4,
          name: '项目文档',
          description: '各项目相关文档资料',
          icon: '🎯',
          color: '#fa8c16',
          docCount: 36,
          memberCount: 10,
          isPublic: false,
          isFavorite: false,
          owner: { id: 1, name: '张三', avatar: null },
          createdAt: '2023-12-20',
          updatedAt: '2024-01-17 09:15',
          lastVisited: '1周前',
          permissions: ['read', 'write']
        }
      ];
      
      setKnowledgeBases(mockData);
      setFilteredKBs(mockData);
    } catch (error) {
      message.error('加载知识库失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingKB(null);
    setModalVisible(true);
  };

  const handleEdit = (kb) => {
    setEditingKB(kb);
    setModalVisible(true);
  };

  const handleDelete = (kb) => {
    confirm({
      title: '确认删除',
      content: `确定要删除知识库"${kb.name}"吗？此操作不可恢复。`,
      okText: '确定',
      cancelText: '取消',
      okType: 'danger',
      onOk: async () => {
        try {
          // 模拟删除API
          await new Promise(resolve => setTimeout(resolve, 500));
          setKnowledgeBases(prev => prev.filter(item => item.id !== kb.id));
          message.success('删除成功');
        } catch (error) {
          message.error('删除失败');
        }
      }
    });
  };

  const handleModalOk = async (values) => {
    try {
      if (editingKB) {
        // 更新知识库
        const updated = { ...editingKB, ...values, updatedAt: new Date().toLocaleString() };
        setKnowledgeBases(prev => prev.map(kb => kb.id === editingKB.id ? updated : kb));
        message.success('更新成功');
      } else {
        // 创建知识库
        const newKB = {
          id: Date.now(),
          ...values,
          docCount: 0,
          memberCount: 1,
          isPublic: false,
          isFavorite: false,
          owner: { id: 1, name: '当前用户', avatar: null },
          createdAt: new Date().toLocaleDateString(),
          updatedAt: new Date().toLocaleString(),
          lastVisited: '刚刚',
          permissions: ['read', 'write', 'admin']
        };
        setKnowledgeBases(prev => [newKB, ...prev]);
        message.success('创建成功');
      }
      setModalVisible(false);
    } catch (error) {
      message.error(editingKB ? '更新失败' : '创建失败');
    }
  };

  const toggleFavorite = (kb) => {
    setKnowledgeBases(prev => prev.map(item => 
      item.id === kb.id ? { ...item, isFavorite: !item.isFavorite } : item
    ));
    message.success(kb.isFavorite ? '已取消收藏' : '已添加收藏');
  };

  const handleShare = (kb) => {
    Modal.info({
      title: '分享知识库',
      content: (
        <div>
          <p>分享链接：</p>
          <Input 
            value={`${window.location.origin}/kb/${kb.id}`}
            readOnly
            addonAfter={
              <Button 
                size="small" 
                onClick={() => {
                  navigator.clipboard.writeText(`${window.location.origin}/kb/${kb.id}`);
                  message.success('链接已复制');
                }}
              >
                复制
              </Button>
            }
          />
          <p style={{ marginTop: 16 }}>
            <Space>
              <span>访问权限：</span>
              {kb.isPublic ? (
                <Tag icon={<GlobalOutlined />} color="green">公开</Tag>
              ) : (
                <Tag icon={<LockOutlined />} color="orange">私有</Tag>
              )}
            </Space>
          </p>
        </div>
      )
    });
  };

  const getMoreActions = (kb) => [
    {
      key: 'edit',
      label: '编辑',
      icon: <EditOutlined />,
      onClick: () => handleEdit(kb),
      disabled: !kb.permissions.includes('admin')
    },
    {
      key: 'share',
      label: '分享',
      icon: <ShareAltOutlined />,
      onClick: () => handleShare(kb)
    },
    {
      type: 'divider'
    },
    {
      key: 'delete',
      label: '删除',
      icon: <DeleteOutlined />,
      danger: true,
      onClick: () => handleDelete(kb),
      disabled: !kb.permissions.includes('admin')
    }
  ];

  const renderKBCard = (kb) => (
    <Card
      hoverable
      className="kb-card"
      onClick={() => navigate(`/kb/${kb.id}`)}
      actions={[
        <Tooltip title={kb.isFavorite ? '取消收藏' : '收藏'}>
          <Button
            type="text"
            icon={kb.isFavorite ? <StarFilled style={{ color: '#faad14' }} /> : <StarOutlined />}
            onClick={(e) => {
              e.stopPropagation();
              toggleFavorite(kb);
            }}
          />
        </Tooltip>,
        <Dropdown
          menu={{ items: getMoreActions(kb) }}
          trigger={['click']}
          onClick={(e) => e.stopPropagation()}
        >
          <Button type="text" icon={<MoreOutlined />} />
        </Dropdown>
      ]}
    >
      <Card.Meta
        avatar={
          <div 
            className="kb-icon" 
            style={{ backgroundColor: kb.color + '20', color: kb.color }}
          >
            <span style={{ fontSize: 32 }}>{kb.icon}</span>
          </div>
        }
        title={
          <Space>
            <span>{kb.name}</span>
            {kb.isPublic ? (
              <Tag icon={<GlobalOutlined />} color="green">公开</Tag>
            ) : (
              <Tag icon={<LockOutlined />} color="orange">私有</Tag>
            )}
          </Space>
        }
        description={
          <div className="kb-description">
            <p>{kb.description || '暂无描述'}</p>
            <Space className="kb-stats" size="large">
              <span>
                <FileTextOutlined /> {kb.docCount} 篇文档
              </span>
              <span>
                <TeamOutlined /> {kb.memberCount} 成员
              </span>
            </Space>
            <div className="kb-meta">
              <Space>
                <Avatar size="small" icon={<UserOutlined />}>
                  {kb.owner.name[0]}
                </Avatar>
                <span>{kb.owner.name}</span>
                <span>•</span>
                <span>
                  <ClockCircleOutlined /> {kb.lastVisited}
                </span>
              </Space>
            </div>
          </div>
        }
      />
    </Card>
  );

  const renderKBListItem = (kb) => (
    <List.Item
      className="kb-list-item"
      onClick={() => navigate(`/kb/${kb.id}`)}
      actions={[
        <Tooltip title={kb.isFavorite ? '取消收藏' : '收藏'}>
          <Button
            type="text"
            icon={kb.isFavorite ? <StarFilled style={{ color: '#faad14' }} /> : <StarOutlined />}
            onClick={(e) => {
              e.stopPropagation();
              toggleFavorite(kb);
            }}
          />
        </Tooltip>,
        <Dropdown
          menu={{ items: getMoreActions(kb) }}
          trigger={['click']}
          onClick={(e) => e.stopPropagation()}
        >
          <Button type="text" icon={<MoreOutlined />} />
        </Dropdown>
      ]}
    >
      <List.Item.Meta
        avatar={
          <div 
            className="kb-icon-small" 
            style={{ backgroundColor: kb.color + '20', color: kb.color }}
          >
            <span style={{ fontSize: 24 }}>{kb.icon}</span>
          </div>
        }
        title={
          <Space>
            <span>{kb.name}</span>
            {kb.isPublic ? (
              <Tag icon={<GlobalOutlined />} color="green">公开</Tag>
            ) : (
              <Tag icon={<LockOutlined />} color="orange">私有</Tag>
            )}
          </Space>
        }
        description={
          <Space split="•">
            <span>{kb.description || '暂无描述'}</span>
            <span><FileTextOutlined /> {kb.docCount} 篇文档</span>
            <span><TeamOutlined /> {kb.memberCount} 成员</span>
            <span><ClockCircleOutlined /> {kb.lastVisited}</span>
          </Space>
        }
      />
    </List.Item>
  );

  return (
    <div className="knowledge-base-list">
      {/* 工具栏 */}
      <div className="kb-toolbar">
        <Space>
          <Button 
            type="primary" 
            icon={<PlusOutlined />}
            onClick={handleCreate}
          >
            新建知识库
          </Button>
          <Search
            placeholder="搜索知识库..."
            allowClear
            style={{ width: 300 }}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
          />
        </Space>
        
        <Space>
          <Button.Group>
            <Button 
              type={viewMode === 'grid' ? 'primary' : 'default'}
              onClick={() => setViewMode('grid')}
            >
              网格
            </Button>
            <Button 
              type={viewMode === 'list' ? 'primary' : 'default'}
              onClick={() => setViewMode('list')}
            >
              列表
            </Button>
          </Button.Group>
        </Space>
      </div>

      {/* 知识库列表 */}
      <Spin spinning={loading}>
        {filteredKBs.length > 0 ? (
          viewMode === 'grid' ? (
            <List
              grid={{ 
                gutter: 16, 
                xs: 1, 
                sm: 2, 
                md: 2, 
                lg: 3, 
                xl: 4,
                xxl: 4 
              }}
              dataSource={filteredKBs}
              renderItem={kb => (
                <List.Item>
                {renderKBCard(kb)}
              </List.Item>
            )}
          />
        ) : (
          <List
            className="kb-list-view"
            dataSource={filteredKBs}
            renderItem={renderKBListItem}
          />
        )
      ) : (
        <Empty 
          description={searchText ? '没有找到匹配的知识库' : '暂无知识库'}
          style={{ marginTop: 100 }}
        >
          {!searchText && (
            <Button type="primary" onClick={handleCreate}>
              创建第一个知识库
            </Button>
          )}
        </Empty>
      )}
    </Spin>

    {/* 编辑/创建弹窗 */}
    <KnowledgeBaseModal
      visible={modalVisible}
      onCancel={() => setModalVisible(false)}
      onOk={handleModalOk}
      initialValues={editingKB}
    />
  </div>
);
};

export default KnowledgeBaseList;