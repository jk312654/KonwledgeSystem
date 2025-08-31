import { configureStore, createSlice } from '@reduxjs/toolkit';

// 用户切片
const userSlice = createSlice({
  name: 'user',
  initialState: {
    currentUser: {
      id: 'user-1',
      name: '当前用户',
      avatar: null,
    }
  },
  reducers: {
    setUser: (state, action) => {
      state.currentUser = action.payload;
    }
  }
});

// 文档切片
const documentSlice = createSlice({
  name: 'document',
  initialState: {
    documents: [],
    currentDocument: null,
    collaborators: []
  },
  reducers: {
    setDocuments: (state, action) => {
      state.documents = action.payload;
    },
    setCurrentDocument: (state, action) => {
      state.currentDocument = action.payload;
    },
    updateCollaborators: (state, action) => {
      state.collaborators = action.payload;
    }
  }
});

// 知识库切片
const knowledgeBaseSlice = createSlice({
  name: 'knowledgeBase',
  initialState: {
    knowledgeBases: [],
    currentKB: null
  },
  reducers: {
    setKnowledgeBases: (state, action) => {
      state.knowledgeBases = action.payload;
    },
    addKnowledgeBase: (state, action) => {
      state.knowledgeBases.push(action.payload);
    },
    updateKnowledgeBase: (state, action) => {
      const index = state.knowledgeBases.findIndex(kb => kb.id === action.payload.id);
      if (index !== -1) {
        state.knowledgeBases[index] = action.payload;
      }
    },
    deleteKnowledgeBase: (state, action) => {
      state.knowledgeBases = state.knowledgeBases.filter(kb => kb.id !== action.payload);
    }
  }
});

export const { setUser } = userSlice.actions;
export const { setDocuments, setCurrentDocument, updateCollaborators } = documentSlice.actions;
export const { setKnowledgeBases, addKnowledgeBase, updateKnowledgeBase, deleteKnowledgeBase } = knowledgeBaseSlice.actions;

const store = configureStore({
  reducer: {
    user: userSlice.reducer,
    document: documentSlice.reducer,
    knowledgeBase: knowledgeBaseSlice.reducer
  }
});

export default store;