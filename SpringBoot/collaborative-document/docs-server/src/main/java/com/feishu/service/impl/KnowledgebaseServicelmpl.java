package com.feishu.service.impl;

import com.feishu.context.BaseContext;
import com.feishu.dto.KnowledgebaseCreateDTO;
import com.feishu.dto.KnowledgebasePutDTO;
import com.feishu.entity.Documents;
import com.feishu.entity.Folder;
import com.feishu.entity.Knowledgebase;
import com.feishu.entity.User;
import com.feishu.utils.ObjectIdUtils;
import com.feishu.repository.DocumentsRepository;
import com.feishu.repository.FolderRepository;
import com.feishu.repository.KnowledgebaseRepository;
import com.feishu.repository.UserRepository;
import com.feishu.service.FolderService;
import com.feishu.service.KnowledgebaseService;
import com.feishu.vo.DocumentVO;
import com.feishu.vo.FolderVO;
import com.feishu.vo.KnowledgebaseVO;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class KnowledgebaseServicelmpl implements KnowledgebaseService {

    @Autowired
    private KnowledgebaseRepository knowledgebaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private DocumentsRepository documentsRepository;




    @Override
    public KnowledgebaseVO create(KnowledgebaseCreateDTO knowledgebaseCreateDTO) {
        Knowledgebase knowledgebase = new Knowledgebase();
        KnowledgebaseVO knowledgebaseVO = new KnowledgebaseVO();
        String authorId = BaseContext.getCurrentId();

        if(knowledgebaseRepository.findByTitle(knowledgebaseCreateDTO.getTitle()) != null){
            throw new IllegalArgumentException("该知识库标题已经存在，无法创建知识库");
        }

        /*// TODO 测试代码
        if (authorId == null || authorId.isBlank()) {
            // 测试时使用默认值，正式环境务必去掉 该authorId对应的用户名为"范涛"
            authorId = "685fbae90dedea6c60b76492"; // 你可以换成一个有效 ObjectId
            log.warn("BaseContext 中无用户 ID，使用默认 authorId={}（仅供测试）", authorId);
        }*/
        ObjectId objectId = new ObjectId(authorId);

        // 根据objectId查找用户信息
        User user = userRepository.findById(objectId);
        if (user == null) {
            log.error("用户 ID={} 不存在，创建知识库失败", objectId);
            throw new IllegalArgumentException("用户不存在，无法创建知识库");
        }

        // 基本属性拷贝
        BeanUtils.copyProperties(knowledgebaseCreateDTO, knowledgebase);
        knowledgebase.setAuthorId(objectId);
        knowledgebase.setAuthorName(user.getUsername());
        knowledgebase.setCreateAt(LocalDateTime.now());
        knowledgebase.setUpdateAt(LocalDateTime.now());
        knowledgebase.setType("knowledgebase");
        knowledgebase.setChildrenId(ObjectIdUtils.toObjectIdList(new ArrayList<>()));
        knowledgebase.setXietongAuthor(ObjectIdUtils.toObjectIdList(new ArrayList<>())); // 初始化协同作者字段为空
        knowledgebaseRepository.save(knowledgebase);// 保存知识库
        // 构造返回对象
        BeanUtils.copyProperties(knowledgebase, knowledgebaseVO);
        knowledgebaseVO.setAuthorId(knowledgebase.getAuthorId().toString());
        knowledgebaseVO.setId(knowledgebase.getId().toString());
        knowledgebaseVO.setXietongAuthor(ObjectIdUtils.toStringList(knowledgebase.getXietongAuthor()));
        return knowledgebaseVO;
    }

    @Override
    public KnowledgebaseVO getById(String id) {
        ObjectId objectId = new ObjectId(id);
        Knowledgebase kb = knowledgebaseRepository.findById(objectId).orElse(null);
        if (kb == null) {
            return null;
        }
        KnowledgebaseVO vo = new KnowledgebaseVO();


        BeanUtils.copyProperties(kb, vo);
        // 处理 xietongAuthor 将ObjectId类型转换成String
        if (kb.getXietongAuthor() != null) {
            vo.setXietongAuthor(ObjectIdUtils.toStringList(kb.getXietongAuthor()));
        }
        // 处理 childrenId 将ObjectId类型转换成String
        if (kb.getChildrenId() != null) {
            vo.setChildrenId(ObjectIdUtils.toStringList(kb.getChildrenId()));
        }
        vo.setAuthorId(kb.getAuthorId().toString());
        vo.setId(kb.getId().toString());
        vo.setAuthorName(userRepository.findById(kb.getAuthorId()).getUsername());

        // 使用递归方式加载3层目录和所有文档
        List<Object> children = new ArrayList<>();

        // 顶层文件夹（知识库下）
        List<Folder> topFolders = folderRepository.findByParentId(kb.getId());
        for (Folder folder : topFolders) {
            children.add(buildFolderTree(folder, 1, 4));
        }

        // 顶层文档（直接挂在知识库下的文档）
        List<Documents> topDocs = documentsRepository.findByParentId(kb.getId());
        for (Documents doc : topDocs) {
            DocumentVO dvo = new DocumentVO();
            BeanUtils.copyProperties(doc, dvo);
            dvo.setId(doc.getId().toString());
            dvo.setParentId(doc.getParentId().toString());
            dvo.setAuthorId(doc.getAuthorId().toString());
            dvo.setKnowledgebaseId(doc.getKnowledgebaseId().toString());
            dvo.setCreateAt(doc.getCreateAt());
            dvo.setUpdateAt(doc.getUpdateAt());
            children.add(dvo);
        }
        vo.setChildren(children);
        return vo;
    }

    @Override
    public List<KnowledgebaseVO> listByCurrentUser() {
        String currentUserId = BaseContext.getCurrentId();
        /*// TODO 测试代码
        if (currentUserId == null || currentUserId.isBlank()) {
            // 测试时使用默认值，正式环境务必去掉
            currentUserId = "685fbae90dedea6c60b76492"; // 你可以换成一个有效 ObjectId
        }*/
        ObjectId userObjectId = new ObjectId(currentUserId);
        // 查我创建的
        List<Knowledgebase> owned = knowledgebaseRepository.findByAuthorId(userObjectId);

        // 最终返回给前端的列表
        List<KnowledgebaseVO> result = new ArrayList<>();

        for (Knowledgebase kb : owned) {
            KnowledgebaseVO vo = new KnowledgebaseVO();
            BeanUtils.copyProperties(kb, vo);

            // 处理 xietongAuthor 将ObjectId类型转换成String
            if (kb.getXietongAuthor() != null) {
                vo.setXietongAuthor(ObjectIdUtils.toStringList(kb.getXietongAuthor()));
            }
            // 处理 childrenId 将ObjectId类型转换成String
            if (kb.getChildrenId() != null) {
                vo.setChildrenId(ObjectIdUtils.toStringList(kb.getChildrenId()));
            }
            vo.setAuthorId(kb.getAuthorId().toString());
            vo.setId(kb.getId().toString());
            vo.setAuthorName(userRepository.findById(kb.getAuthorId()).getUsername());

            // 使用递归方式加载3层目录和所有文档
            List<Object> children = new ArrayList<>();

            // 顶层文件夹（知识库下）
            List<Folder> topFolders = folderRepository.findByParentId(kb.getId());
            for (Folder folder : topFolders) {
                children.add(buildFolderTree(folder, 1, 4));
            }

            // 顶层文档（直接挂在知识库下的文档）
            List<Documents> topDocs = documentsRepository.findByParentId(kb.getId());
            for (Documents doc : topDocs) {
                DocumentVO dvo = new DocumentVO();
                BeanUtils.copyProperties(doc, dvo);
                dvo.setId(doc.getId().toString());
                dvo.setParentId(doc.getParentId().toString());
                dvo.setAuthorId(doc.getAuthorId().toString());
                dvo.setCreateAt(doc.getCreateAt());
                dvo.setKnowledgebaseId(doc.getKnowledgebaseId().toString());
                dvo.setUpdateAt(doc.getUpdateAt());
                children.add(dvo);
            }
            vo.setChildren(children);
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<KnowledgebaseVO> listByCollaborator() {
        String currentUserId = BaseContext.getCurrentId();
        /*// TODO 测试代码
        if (currentUserId == null || currentUserId.isBlank()) {
            // 测试时使用默认值，正式环境务必去掉
            currentUserId = "685fbae90dedea6c60b76492"; // 你可以换成一个有效 ObjectId
        }*/
        ObjectId userObjectId = new ObjectId(currentUserId);
        // 我协作的
        List<Knowledgebase> collaborated = knowledgebaseRepository.findByXietongAuthorContains(userObjectId);

        // 最终返回给前端的列表
        List<KnowledgebaseVO> result = new ArrayList<>();

        for (Knowledgebase kb : collaborated) {
            KnowledgebaseVO vo = new KnowledgebaseVO();
            BeanUtils.copyProperties(kb, vo);

            // 处理 xietongAuthor 将ObjectId类型转换成String
            if (kb.getXietongAuthor() != null) {
                vo.setXietongAuthor(ObjectIdUtils.toStringList(kb.getXietongAuthor()));
            }
            // 处理 childrenId 将ObjectId类型转换成String
            if (kb.getChildrenId() != null) {
                vo.setChildrenId(ObjectIdUtils.toStringList(kb.getChildrenId()));
            }
            vo.setAuthorId(kb.getAuthorId().toString());
            vo.setId(kb.getId().toString());
            vo.setAuthorName(userRepository.findById(kb.getAuthorId()).getUsername());

            // 使用递归方式加载3层目录和所有文档
            List<Object> children = new ArrayList<>();

            // 顶层文件夹（知识库下）
            List<Folder> topFolders = folderRepository.findByParentId(kb.getId());
            for (Folder folder : topFolders) {
                children.add(buildFolderTree(folder, 1, 4));
            }

            // 顶层文档（直接挂在知识库下的文档）
            List<Documents> topDocs = documentsRepository.findByParentId(kb.getId());
            for (Documents doc : topDocs) {
                DocumentVO dvo = new DocumentVO();
                BeanUtils.copyProperties(doc, dvo);
                dvo.setId(doc.getId().toString());
                dvo.setParentId(doc.getParentId().toString());
                dvo.setAuthorId(doc.getAuthorId().toString());
                dvo.setKnowledgebaseId(doc.getKnowledgebaseId().toString());
                dvo.setCreateAt(doc.getCreateAt());
                dvo.setUpdateAt(doc.getUpdateAt());
                children.add(dvo);
            }
            vo.setChildren(children);
            result.add(vo);
        }
        return result;
    }


    /**
     * 根据 ID 删除知识库，仅允许作者本人删除
     */
    @Override
    public void deleteKnowledgebaseById(String kbId) {
        ObjectId id = safeParseObjectId(kbId, "无效的知识库 ID");
        Knowledgebase kb = knowledgebaseRepository.findById(id).orElse(null);

        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }

        String currentUserId = BaseContext.getCurrentId();
        /*// TODO 测试代码
        if (currentUserId == null || currentUserId.isBlank()) {
            // 测试时使用默认值，正式环境务必去掉
            currentUserId = "685fbae90dedea6c60b76492"; // 你可以换成一个有效 ObjectId
        }*/

        if (!currentUserId.equals(kb.getAuthorId().toHexString())) {
            throw new RuntimeException("您无权删除该知识库");
        }

        // 1. 删除知识库本身
        knowledgebaseRepository.deleteById(id);

        // 2. 删除该知识库下所有目录（根据 knowledgebaseId）
        folderRepository.deleteByKnowledgebaseId(id);

        // 3. 删除该知识库下所有文档（根据 knowledgebaseId）
        documentsRepository.deleteByKnowledgebaseId(id);

    }


    @Override
    public void updateKnowledgebase(String kbId, KnowledgebasePutDTO dto) {
        ObjectId id = new ObjectId(kbId);
        Knowledgebase kb = knowledgebaseRepository.findById(id).orElse(null);

        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }

        String currentUserId = BaseContext.getCurrentId();
        /*// TODO 测试代码
        if (currentUserId == null || currentUserId.isBlank()) {
            // 测试时使用默认值，正式环境务必去掉
            currentUserId = "685fbae90dedea6c60b76492"; // 你可以换成一个有效 ObjectId
        }*/


        if (!currentUserId.equals(kb.getAuthorId().toString())) {
            throw new RuntimeException("您无权修改该知识库");
        }

        // 以下为字段判断后更新
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            kb.setTitle(dto.getTitle());
        }

        if (dto.getIcon() != null && !dto.getIcon().isBlank()) {
            kb.setIcon(dto.getIcon());
        }

        if (dto.getDesc() != null && !dto.getDesc().isBlank()) {
            kb.setDesc(dto.getDesc());
        }

        if (dto.getXietongAuthor() != null) {
            // 将 List<String> 转为 List<ObjectId>
            kb.setXietongAuthor(ObjectIdUtils.toObjectIdList(dto.getXietongAuthor()));
        }

        kb.setUpdateAt(LocalDateTime.now());

        knowledgebaseRepository.save(kb);
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

    // 避免传入非法字符串导致异常
    private ObjectId safeParseObjectId(String hex, String errorMessage) {
        try {
            return new ObjectId(hex);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(errorMessage);
        }
    }


}
