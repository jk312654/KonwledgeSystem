package com.feishu.service.impl;

import com.feishu.context.BaseContext;
import com.feishu.dto.DocumentMoveDTO;
import com.feishu.dto.DocumentsCreateDTO;
import com.feishu.dto.DocumentsUpdateDTO;
import com.feishu.entity.DocumentVisitLog;
import com.feishu.entity.Documents;
import com.feishu.entity.Folder;
import com.feishu.entity.Knowledgebase;
import com.feishu.repository.*;
import com.feishu.service.DocumentsService;
import com.feishu.utils.TokenizerUtils;
import com.feishu.vo.DocumentMoveVO;
import com.feishu.vo.DocumentsLatestVisitedVO;
import com.feishu.vo.DocumentsVO;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentsServiceImpl implements DocumentsService {

    private static final Logger log = LoggerFactory.getLogger(DocumentsServiceImpl.class);
    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private SummariesRepository summariesRepository;

    @Autowired
    private DocumentVisitLogRepository documentVisitLogRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private KnowledgebaseRepository knowledgebaseRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RevisionRepository revisionRepository;

    @Autowired
    private TextCommentRepository textCommentRepository;

    @Override
    public DocumentsVO create(DocumentsCreateDTO documentsCreateDTO) {
        Documents documents = new Documents();
        String authorId = BaseContext.getCurrentId();

        /*// TODO 测试代码
        if (authorId == null || authorId.isBlank()) {
            // 测试时使用默认值，正式环境务必去掉
            authorId = "685fbae90dedea6c60b76492"; // 你可以换成一个有效 ObjectId
        }*/

        ObjectId objectId = new ObjectId(authorId);
        BeanUtils.copyProperties(documentsCreateDTO, documents);
        documents.setParentId(new ObjectId(documentsCreateDTO.getParentId()));
        documents.setAuthorId(objectId);
        documents.setId(new ObjectId());
        documents.setType("document");
        documents.setAuthorName(userRepository.findById(objectId).getUsername());
        documents.setCreateAt(LocalDateTime.now());
        documents.setUpdateAt(LocalDateTime.now());

        //
        Optional<Folder> folderOpt = folderRepository.findById(documents.getParentId());
        if (folderOpt.isPresent()) {
            Folder parentFolder = folderOpt.get();
            documents.setKnowledgebaseId(parentFolder.getKnowledgebaseId());
            // 添加子目录ID
            parentFolder.getChildrenId().add(documents.getId());
            folderRepository.save(parentFolder);
        } else {
            Optional<Knowledgebase> kbOpt = knowledgebaseRepository.findById(documents.getParentId());
            if (kbOpt.isPresent()) {
                Knowledgebase kb = kbOpt.get();
                documents.setKnowledgebaseId(kb.getId());

                // 添加子目录ID
                kb.getChildrenId().add(documents.getId());
                knowledgebaseRepository.save(kb);
            }
        }

        documentsRepository.save(documents);
        if (!documents.getContent().isEmpty()) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("docId", documents.getId().toString());
            metadata.put("kbId", documents.getKnowledgebaseId().toString());
            Document doc = new Document(documents.getContent(), metadata);
            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
            List<Document> documentList = tokenTextSplitter.split(doc);
            vectorStore.add(documentList);
        }

        // 记录访问行为（即视为一次访问）
        DocumentVisitLog log = DocumentVisitLog.builder()
                .userId(objectId)
                .documentId(documents.getId())
                .visitTime(LocalDateTime.now())
                .build();
        documentVisitLogRepository.save(log);


        return convertToVO(documents);
    }

    @Override
    public DocumentsVO getDocument(String id) {
        ObjectId docObjectId = new ObjectId(id);
        DocumentsVO vo = documentsRepository.findById(docObjectId)
        .map(this::convertToVO)
        .orElse(null);

        return vo;
    }

    @Override
    public List<DocumentsVO> getDocumentByParent(String id) {
        ObjectId objectId = new ObjectId(id);
        List<Documents> documentsList = documentsRepository.findByParentId(objectId);
        return documentsList.stream()
                .map(this::convertToVO)  // 将每个Documents对象转为DocumentsVO
                .collect(Collectors.toList());
    }

    @Override
    public DocumentsVO update(DocumentsUpdateDTO documentsUpdateDTO) throws IOException {
        ObjectId objectId = new ObjectId(documentsUpdateDTO.getId());
        Documents documents = documentsRepository.findById(objectId).orElse(null);

        if (documents == null) {
            throw new RuntimeException("文档不存在");
        }

        // 只更新非空字段，避免覆盖现有数据
        if (documentsUpdateDTO.getTitle() != null) {
            documents.setTitle(documentsUpdateDTO.getTitle());
        }
        if (documentsUpdateDTO.getContent() != null) {
            documents.setContent(documentsUpdateDTO.getContent());
            documents.setContentTokens(TokenizerUtils.tokenize(documentsUpdateDTO.getContent()));
        }
        if (documentsUpdateDTO.getDelta() != null) {
            documents.setDelta(documentsUpdateDTO.getDelta());
        }

        documents.setUpdateAt(LocalDateTime.now());
        documentsRepository.save(documents);

        if (documentsUpdateDTO.getContent() != null && !documentsUpdateDTO.getContent().isEmpty()){
            updateVectorStore(documents);
        }
        summariesRepository.deleteByDocumentsId(objectId);


        // 编辑文档（即视为一次访问）
        DocumentVisitLog log = DocumentVisitLog.builder()
                .userId(new ObjectId(BaseContext.getCurrentId()))
                .documentId(documents.getId())
                .visitTime(LocalDateTime.now())
                .build();
        documentVisitLogRepository.save(log);
        return convertToVO(documents);

    }

    @Override
    public void delete(String id) {
        ObjectId objectId = new ObjectId(id);

        // 查找文档
        Documents document = documentsRepository.findById(objectId)
                .orElseThrow(() -> new IllegalArgumentException("文档不存在"));

        // 移除父节点的 childrenId
        ObjectId parentId = document.getParentId();
        if (parentId != null) {
            // 父节点可能是 Folder
            folderRepository.findById(parentId).ifPresent(folder -> {
                folder.getChildrenId().remove(objectId);
                folderRepository.save(folder);
            });

            // 父节点可能是 Knowledgebase
            knowledgebaseRepository.findById(parentId).ifPresent(kb -> {
                kb.getChildrenId().remove(objectId);
                knowledgebaseRepository.save(kb);
            });
        }

         // 删除文档数据
        documentsRepository.deleteById(objectId);

        // 删除向量数据
        String filterExpression = String.format("docId == '%s'", id);
        SearchRequest searchRequest = SearchRequest.builder()
                .query("")
                .filterExpression(filterExpression)
                .build();
        List<Document> vectorDocuments = vectorStore.similaritySearch(searchRequest);
        if (!vectorDocuments.isEmpty()) {
            List<String> vectorIds = vectorDocuments.stream()
                    .map(Document::getId)
                    .collect(Collectors.toList());
            vectorStore.delete(vectorIds);
        }
        // 删除文档摘要
        summariesRepository.deleteByDocumentsId(objectId);
        // 删除文档评论
        commentRepository.deleteByDocumentId(objectId);
        // 删除文本评论
        textCommentRepository.deleteByDocumentId(objectId);
        // 删除修订
        revisionRepository.deleteByDocumentId(objectId);
    }

    @Override
    public List<DocumentsLatestVisitedVO> getRecentDocuments() {
        String currentUserId = BaseContext.getCurrentId();

        /*// TODO 测试代码
        if (currentUserId == null || currentUserId.isBlank()) {
            currentUserId = "685eaf9afac7740bbff97b9a"; // 测试用
        }*/

        ObjectId userId = new ObjectId(currentUserId);

        // 获取最近访问记录，按时间倒序
        List<DocumentVisitLog> logs = documentVisitLogRepository
            .findTop20ByUserIdOrderByVisitTimeDesc(userId); // 可调数量

        // 保证文档唯一（去重）
        LinkedHashMap<ObjectId, LocalDateTime> docIdToVisitTime = new LinkedHashMap<>();
        for (DocumentVisitLog log : logs) {
            docIdToVisitTime.putIfAbsent(log.getDocumentId(), log.getVisitTime());
        }

        List<DocumentsLatestVisitedVO> result = new ArrayList<>();
        for (Map.Entry<ObjectId, LocalDateTime> entry : docIdToVisitTime.entrySet()) {
            ObjectId docId = entry.getKey();

            documentsRepository.findById(docId).ifPresent(doc -> {
                ObjectId parentId = doc.getParentId();

                // 先尝试是否是知识库直接子节点
                Knowledgebase kb = knowledgebaseRepository.findById(parentId).orElse(null);
                if (kb != null) {
                    // 是知识库的直接子节点
                    result.add(DocumentsLatestVisitedVO.builder()
                            .documentId(doc.getId().toString())
                            .documentTitle(doc.getTitle())
                            .knowledgebaseId(kb.getId().toString())
                            .knowledgebaseTitle(kb.getTitle())
                            .authorName(doc.getAuthorName())
                            .lastModifiedTime(doc.getUpdateAt())
                            .build());
                } else {
                    // 否则是 Folder 的子节点，向上查找 Folder 再查 Knowledgebase
                    Folder folder = folderRepository.findById(parentId).orElse(null);
                    if (folder != null) {
                        Knowledgebase parentKb = knowledgebaseRepository.findById(folder.getParentId()).orElse(null);
                        if (parentKb != null) {
                            result.add(DocumentsLatestVisitedVO.builder()
                                    .documentId(doc.getId().toString())
                                    .documentTitle(doc.getTitle())
                                    .knowledgebaseId(parentKb.getId().toString())
                                    .knowledgebaseTitle(parentKb.getTitle())
                                    .authorName(doc.getAuthorName())
                                    .lastModifiedTime(doc.getCreateAt())
                                    .build());
                        }
                    }
                }
            });
        }

        return result;
    }

    /**
     * 移动文档
     * @param moveDTO
     * @return
     */
    @Override
    public DocumentMoveVO moveDocument(DocumentMoveDTO moveDTO) {
        String curDocumentIdStr = moveDTO.getCurDocumentId();
        String newParentIdStr = moveDTO.getNewParentId();

        ObjectId documentId = new ObjectId(curDocumentIdStr);
        Documents document = documentsRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("文档不存在"));

        ObjectId oldParentId = document.getParentId();
        // 移除旧父节点 childrenId
        if (oldParentId != null) {
            folderRepository.findById(oldParentId).ifPresent(oldFolder -> {
                if (oldFolder.getChildrenId() != null) {
                    oldFolder.getChildrenId().remove(documentId);
                }
                folderRepository.save(oldFolder);
            });

            knowledgebaseRepository.findById(oldParentId).ifPresent(oldKb -> {
                if (oldKb.getChildrenId() != null) {
                    oldKb.getChildrenId().remove(documentId);
                }
                knowledgebaseRepository.save(oldKb);
            });
        }

        // 设置新父节点
        ObjectId newParentId = new ObjectId(newParentIdStr);
        document.setParentId(newParentId);
        document.setUpdateAt(LocalDateTime.now());

        DocumentMoveVO moveVO = new DocumentMoveVO();
        moveVO.setNewParentId(newParentIdStr);
        moveVO.setUpdateAt(document.getUpdateAt());

        // 尝试作为 Folder 处理
        Optional<Folder> folderOpt = folderRepository.findById(newParentId);
        if (folderOpt.isPresent()) {
            Folder folder = folderOpt.get();
            if (!folder.getChildrenId().contains(documentId)) {
                folder.getChildrenId().add(documentId);
            }
            document.setKnowledgebaseId(folder.getKnowledgebaseId());

            folderRepository.save(folder);
            documentsRepository.save(document);

            moveVO.setNewParentTitle(folder.getTitle());
            return moveVO;
        }

        // 尝试作为 Knowledgebase 处理
        Optional<Knowledgebase> kbOpt = knowledgebaseRepository.findById(newParentId);
        if (kbOpt.isPresent()) {
            Knowledgebase kb = kbOpt.get();
            if (!kb.getChildrenId().contains(documentId)) {
                kb.getChildrenId().add(documentId);
            }
            document.setKnowledgebaseId(kb.getId());

            knowledgebaseRepository.save(kb);
            documentsRepository.save(document);

            moveVO.setNewParentTitle(kb.getTitle());
            return moveVO;
        }

        throw new IllegalArgumentException("目标父节点既不是目录也不是知识库");

    }

    @Override
    public List<DocumentsVO> searchDocument(String knowledgebaseId, String keyword) throws IOException {
        //        List<Documents> documentsList= documentsRepository.findByContentAndKnowledgebaseId(keyword, new ObjectId(knowledgebaseId));
//        return documentsList.stream()
//                .map(this::convertToVO)  // 将每个Documents对象转为DocumentsVO
//                .collect(Collectors.toList());
        Set<String> tokens = TokenizerUtils.tokenize(keyword);

        // 构建复合查询条件（加入knowledgebaseId过滤）
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("knowledgebaseId").is(new ObjectId(knowledgebaseId)),  // 知识库ID过滤
                new Criteria().orOperator(
                        Criteria.where("contentTokens").in(tokens)  // 分词条件
                )
        );

        // 执行查询
        Query query = new Query(criteria);
        List<Documents> documentsList = mongoTemplate.find(query, Documents.class);
        return documentsList.stream()
               .map(this::convertToVO)  // 将每个Documents对象转为DocumentsVO
               .collect(Collectors.toList());
    }


    private DocumentsVO convertToVO(Documents documents) {
        DocumentsVO documentsVO = new DocumentsVO();
        BeanUtils.copyProperties(documents, documentsVO);
        documentsVO.setId(documents.getId().toString());
        documentsVO.setAuthorId(documents.getAuthorId().toString());
        documentsVO.setParentId(documents.getParentId().toString());
        return documentsVO;
    }

    private void updateVectorStore(Documents documents) {
        // 删除旧向量（按文档ID过滤）
        String filterExpression = String.format("docId == '%s'", documents.getId().toString());
        SearchRequest searchRequest = SearchRequest.builder()
                .query("")
                .filterExpression(filterExpression)
                .build();
        List<Document> vectorDocuments = vectorStore.similaritySearch(searchRequest);
        if (vectorDocuments != null && vectorDocuments.isEmpty()) {
            List<String> vectorIds = vectorDocuments.stream()
                    .map(Document::getId)
                    .collect(Collectors.toList());
            vectorStore.delete(vectorIds);
        }
        // 生成新向量并插入
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("docId", documents.getId().toString());
        metadata.put("kbId", documents.getKnowledgebaseId().toString());
        Document doc = new Document(documents.getContent(), metadata);
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<Document> documentList = tokenTextSplitter.split(doc);
        if (documentList.isEmpty()) {
            documentList = Collections.singletonList(doc);
        }
        vectorStore.add(documentList);
    }
}
