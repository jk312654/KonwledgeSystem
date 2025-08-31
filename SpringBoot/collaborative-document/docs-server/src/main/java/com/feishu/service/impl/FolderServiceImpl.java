package com.feishu.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.feishu.context.BaseContext;
import com.feishu.dto.FolderMoveDTO;
import com.feishu.dto.KnowledgebasePutDTO;
import com.feishu.entity.Documents;
import com.feishu.entity.Folder;
import com.feishu.entity.Knowledgebase;
import com.feishu.entity.User;
import com.feishu.repository.*;
import com.feishu.service.FolderService;
import com.feishu.utils.ObjectIdUtils;
import com.feishu.vo.*;
import org.aspectj.weaver.ast.Var;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FolderServiceImpl implements FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private KnowledgebaseRepository knowledgebaseRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private SummariesRepository summariesRepository;


    @Override
    public FolderCreateVO createFolder(String parentIdStr, String title) {
        ObjectId parentId = new ObjectId(parentIdStr);
        String currentId = BaseContext.getCurrentId();

        /*// TODO 测试代码
        if (currentId == null || currentId.isBlank()) {
            // 测试时使用默认值，正式环境务必去掉
            currentId = "685fbae90dedea6c60b76492"; // 你可以换成一个有效 ObjectId
        }*/
        ObjectId authorId = new ObjectId(currentId);
        User user = userRepository.findById(authorId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在，无法创建目录");
        }
        Folder folder = new Folder();
        folder.setTitle(title);
        folder.setType("folder");
        folder.setId(new ObjectId());
        folder.setParentId(parentId);
        folder.setChildrenId(ObjectIdUtils.toObjectIdList(new ArrayList<>()));
        folder.setCreateAt(LocalDateTime.now());
        folder.setUpdateAt(LocalDateTime.now());
        folder.setAuthorId(authorId);
        folder.setAuthor(user.getUsername());

        // 先查看这个parentId是不是目录
        Optional<Folder> folderOpt = folderRepository.findById(parentId);
        if (folderOpt.isPresent()) {
            Folder parentFolder = folderOpt.get();
            folder.setKnowledgebaseId(parentFolder.getKnowledgebaseId());
            // 添加子目录ID
            parentFolder.getChildrenId().add(folder.getId());
            folderRepository.save(parentFolder);
        } else {
            Optional<Knowledgebase> kbOpt = knowledgebaseRepository.findById(parentId);
            if (kbOpt.isPresent()) {
                Knowledgebase kb = kbOpt.get();
                folder.setKnowledgebaseId(kb.getId());

                // 添加子目录ID
                kb.getChildrenId().add(folder.getId());
                knowledgebaseRepository.save(kb);
            }
        }


        Folder save = folderRepository.save(folder);
        FolderCreateVO folderCreateVO = new FolderCreateVO();
        BeanUtils.copyProperties(save, folderCreateVO);
        folderCreateVO.setId(save.getId().toString());
        folderCreateVO.setAuthorId(currentId);
        folderCreateVO.setParentId(parentIdStr);
        folderCreateVO.setKnowledgebaseId(save.getKnowledgebaseId().toString());
        return folderCreateVO;
    }

    @Override
    public FolderVO getFolderWithChildren(String id) {
        ObjectId folderId = new ObjectId(id);

        // 查询当前目录
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("目录不存在"));

        // 构建完整的目录树（含文档） - 默认递归深度为 Integer.MAX_VALUE（或你可以传1、2等）
        return buildFolderTree(folder, 0, Integer.MAX_VALUE);
    }


    @Override
    public List<FolderVO> getFoldersWithDocs(String parentIdStr) {
        ObjectId parentId = new ObjectId(parentIdStr);
        List<FolderVO> result = new ArrayList<>();

        // 判断 parentId 是目录还是知识库
        Optional<Folder> optFolder = folderRepository.findById(parentId);
        if (optFolder.isPresent()) {
            // parentId 是某个目录的 ID：查询该目录下一级目录，递归构建树
            List<Folder> folders = folderRepository.findByParentId(parentId);
            for (Folder folder : folders) {
                FolderVO tree = buildFolderTree(folder, 0, Integer.MAX_VALUE);
                result.add(tree);
            }
        }
        return result;
    }



    @Override
    public FolderRenameVO renameFolder(String folderIdStr, String newTitle) {

        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("目录名不能为空");
        }
        String currentId = BaseContext.getCurrentId();
        /*// TODO 测试代码
        if (currentId == null || currentId.isBlank()) {
            // 测试时使用默认值，正式环境务必去掉
            currentId = "685fbae90dedea6c60b76492"; // 你可以换成一个有效 ObjectId
        }*/
        ObjectId authorId = new ObjectId(currentId);

        ObjectId folderId = new ObjectId(folderIdStr);
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("目录不存在"));

        folder.setTitle(newTitle);
        folder.setAuthorId(authorId);
        folder.setUpdateAt(LocalDateTime.now());
        Folder updated = folderRepository.save(folder);

        return FolderRenameVO.builder()
                .id(updated.getId().toString())
                .authorId(updated.getAuthorId().toString())
                .newTitle(updated.getTitle())
                .updateAt(updated.getUpdateAt())
                .build();
    }



    @Override
    public FolderMoveVO moveFolder(FolderMoveDTO moveDTO) {
        String folderIdStr = moveDTO.getCurFolderId();
        String newParentIdStr = moveDTO.getNewParentId();
        ObjectId folderId = new ObjectId(folderIdStr);
        Folder folder = folderRepository.findById(folderId)
            .orElseThrow(() -> new IllegalArgumentException("要移动的目录不存在"));

        ObjectId oldParentId = folder.getParentId();
        // 移除旧父节点中的 childrenId
        if (oldParentId != null) {
            // 尝试从 Folder 中移除
            folderRepository.findById(oldParentId).ifPresent(oldParentFolder -> {
                oldParentFolder.getChildrenId().remove(folderId);
                folderRepository.save(oldParentFolder);
            });

            // 尝试从 Knowledgebase 中移除
            knowledgebaseRepository.findById(oldParentId).ifPresent(oldParentKb -> {
                oldParentKb.getChildrenId().remove(folderId);
                knowledgebaseRepository.save(oldParentKb);
            });
        }

        ObjectId newParentId = new ObjectId(newParentIdStr);
        ObjectId objectId = new ObjectId(newParentIdStr);
        folder.setParentId(objectId);
        folder.setUpdateAt(LocalDateTime.now());


        FolderMoveVO folderMoveVO = new FolderMoveVO();
        folderMoveVO.setNewParentId(newParentIdStr);

        // 目标是 Folder
        Optional<Folder> newParentFolderOpt = folderRepository.findById(newParentId);
        if (newParentFolderOpt.isPresent()) {
            Folder newParentFolder = newParentFolderOpt.get();

            // 更新子节点
            if (!newParentFolder.getChildrenId().contains(folderId)) {
                newParentFolder.getChildrenId().add(folderId);
            }
            folder.setKnowledgebaseId(newParentFolder.getKnowledgebaseId());

            folderRepository.save(newParentFolder);
            folderRepository.save(folder);

            folderMoveVO.setNewParentTitle(newParentFolder.getTitle());
            return folderMoveVO;
        }

        // 目标是 Knowledgebase
        Optional<Knowledgebase> newParentKbOpt = knowledgebaseRepository.findById(newParentId);
        if (newParentKbOpt.isPresent()) {
            Knowledgebase newParentKb = newParentKbOpt.get();

            // 更新子节点
            if (!newParentKb.getChildrenId().contains(folderId)) {
                newParentKb.getChildrenId().add(folderId);
            }
            folder.setKnowledgebaseId(newParentKb.getId());

            knowledgebaseRepository.save(newParentKb);
            folderRepository.save(folder);

            folderMoveVO.setNewParentTitle(newParentKb.getTitle());
            return folderMoveVO;
        }
        throw new IllegalArgumentException("目标父节点既不是目录也不是知识库");
    }

    /**
     *
     * @param id
     */
    @Override
    public void deleteFolder(String id) {
        ObjectId folderId = new ObjectId(id);

        // 1. 查找目录
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("目录不存在"));

        // 2. 移除父节点的 childrenId（可能是 Folder 或 Knowledgebase）
        ObjectId parentId = folder.getParentId();
        if (parentId != null) {
            folderRepository.findById(parentId).ifPresent(parentFolder -> {
                parentFolder.getChildrenId().remove(folderId);
                folderRepository.save(parentFolder);
            });

            knowledgebaseRepository.findById(parentId).ifPresent(parentKb -> {
                parentKb.getChildrenId().remove(folderId);
                knowledgebaseRepository.save(parentKb);
            });
        }

        // 3. 删除当前目录下所有 childrenId（文档和目录）
        List<ObjectId> children = folder.getChildrenId();
        if (children != null) {
            for (ObjectId childId : children) {
                // 尝试删除为文档
                documentsRepository.findById(childId).ifPresent(doc -> {
                    delete(doc.getId().toHexString()); // 调用文档删除逻辑
                });

                // 尝试删除为子目录
                folderRepository.findById(childId).ifPresent(subFolder -> {
                    deleteFolder(subFolder.getId().toHexString()); // 递归删除
                });
            }
        }

        // 4. 删除当前目录自身
        folderRepository.deleteById(folderId);
    }


    // 递归查询目录树
    private FolderVO buildFolderTree(Folder folder, int currentDepth, int maxDepth) {
        FolderVO folderVO = new FolderVO();
        BeanUtils.copyProperties(folder, folderVO);
        folderVO.setId(folder.getId().toString());
        folderVO.setAuthorId(folder.getAuthorId().toString());
        folderVO.setParentId(folder.getParentId().toString());
        folderVO.setKnowledgebaseId(folder.getKnowledgebaseId().toString());
        folderVO.setCreateAt(folder.getCreateAt());
        folderVO.setUpdateAt(folder.getUpdateAt());

        // 转换 childrenId 类型
        if (folder.getChildrenId() != null) {
            folderVO.setChildrenId(ObjectIdUtils.toStringList(folder.getChildrenId()));
        } else {
            folderVO.setChildrenId(new ArrayList<>());
        }

        // 可选：构建 children，仅在需要递归层级时加载
        if (currentDepth < maxDepth && folder.getChildrenId() != null) {
            List<Object> children = new ArrayList<>();
            for (ObjectId childId : folder.getChildrenId()) {
                // 优先查找是否为子目录
                Folder subFolder = folderRepository.findById(childId).orElse(null);
                if (subFolder != null) {
                    children.add(buildFolderTree(subFolder, currentDepth + 1, maxDepth));
                    continue;
                }

                // 否则查找是否为文档
                Documents doc = documentsRepository.findById(childId).orElse(null);
                if (doc != null) {
                    DocumentVO dvo = new DocumentVO();
                    BeanUtils.copyProperties(doc, dvo);
                    dvo.setId(doc.getId().toString());
                    dvo.setAuthorId(doc.getAuthorId().toString());
                    dvo.setParentId(doc.getParentId() != null ? doc.getParentId().toString() : null);
                    dvo.setCreateAt(doc.getCreateAt());
                    dvo.setUpdateAt(doc.getUpdateAt());
                    dvo.setKnowledgebaseId(doc.getKnowledgebaseId().toString());
                    children.add(dvo);
                }
            }
            folderVO.setChildren(children);
        } else {
            folderVO.setChildren(null); // 或者空 list，看你前端是否接受 null
        }
        return folderVO;
    }


    // 删除文档 方法和删除文档中的接口一样
    private void delete(String id) {
        ObjectId objectId = new ObjectId(id);

        // 查找文档
        Documents document = documentsRepository.findById(objectId).orElse(null);
        if (document == null) {
            return;
        }

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
    }

}
