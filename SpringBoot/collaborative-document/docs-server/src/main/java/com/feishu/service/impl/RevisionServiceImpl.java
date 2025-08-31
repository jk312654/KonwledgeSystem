package com.feishu.service.impl;

import com.feishu.constant.MessageConstant;
import com.feishu.context.BaseContext;
import com.feishu.dto.RevisionCreateDTO;
import com.feishu.dto.RevisionUpdateDTO;
import com.feishu.entity.Comment;
import com.feishu.entity.Revision;
import com.feishu.entity.User;
import com.feishu.repository.RevisionRepository;
import com.feishu.repository.UserRepository;
import com.feishu.result.Result;
import com.feishu.service.RevisionService;
import com.feishu.vo.CommentCreateVO;
import com.feishu.vo.RevisionCreateVO;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RevisionServiceImpl implements RevisionService {

    @Autowired
    private RevisionRepository revisionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public RevisionCreateVO create(RevisionCreateDTO revisionCreateDTO) {
        String userId = BaseContext.getCurrentId();
        ObjectId objectId = new ObjectId(userId);
        Revision revision = new Revision();
        revision.setUserId(objectId);
        revision.setDelta(revisionCreateDTO.getDelta());
        revision.setDocumentId(new ObjectId(revisionCreateDTO.getDocumentId()));
        revision.setStatus("pending");
        revision.setUpdateAt(LocalDateTime.now());
        revision.setCreateAt(LocalDateTime.now());
        revision.setType(revisionCreateDTO.getType());
        revision.setPosition(revisionCreateDTO.getPosition());
        revisionRepository.save(revision);
        return convertToVO(revision);
    }

    @Override
    public Result update(RevisionUpdateDTO revisionUpdateDTO) {
        String userId = BaseContext.getCurrentId();
        ObjectId objectId = new ObjectId(revisionUpdateDTO.getId());
        Revision revision = revisionRepository.findById(objectId).orElse(null);
        String authorId = revision.getUserId().toString();
        if (!userId.equals(authorId)) {
            return Result.error(MessageConstant.UNAUTHOR);
        }
        revision.setPosition(revisionUpdateDTO.getPosition());
        revision.setDelta(revisionUpdateDTO.getDelta());
        revision.setStatus(revisionUpdateDTO.getStatus());
        revision.setUpdateAt(LocalDateTime.now());
        revisionRepository.save(revision);
        RevisionCreateVO revisionCreateVO = convertToVO(revision);
        return Result.success(revisionCreateVO);
    }

    @Override
    public Result delete(String id) {
        String userId = BaseContext.getCurrentId();
        ObjectId objectId = new ObjectId(id);
        Revision revision = revisionRepository.findById(objectId).orElse(null);
        String authorId = revision.getUserId().toString();
        if (!userId.equals(authorId)) {
            return Result.error(MessageConstant.UNAUTHOR);
        }
        revisionRepository.deleteById(objectId);
        return Result.success();
    }

    @Override
    public List<RevisionCreateVO> getById(String documentId) {
        List<Revision> commentsList = revisionRepository.findByDocumentId(new ObjectId(documentId));
        return commentsList.stream()
                .filter(revision -> !"accepted".equals(revision.getStatus()))
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private RevisionCreateVO convertToVO(Revision revision) {
        RevisionCreateVO revisionCreateVO = new RevisionCreateVO();
        BeanUtils.copyProperties(revision, revisionCreateVO);
        revisionCreateVO.setId(revision.getId().toString());
        ObjectId userId = revision.getUserId();
        revisionCreateVO.setUserId(userId.toString());
        revisionCreateVO.setDocumentId(revision.getDocumentId().toString());
        User user = userRepository.findById(userId);
        String username = user.getUsername();
        String picture = user.getPicture();
        revisionCreateVO.setUsername(username);
        revisionCreateVO.setPicture(picture);
        return revisionCreateVO;
    }
}
