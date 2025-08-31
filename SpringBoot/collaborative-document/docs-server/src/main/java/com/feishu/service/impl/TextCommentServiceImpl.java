package com.feishu.service.impl;

import com.feishu.constant.MessageConstant;
import com.feishu.context.BaseContext;
import com.feishu.dto.RevisionCreateDTO;
import com.feishu.dto.RevisionUpdateDTO;
import com.feishu.dto.TextCommentCreateDTO;
import com.feishu.dto.TextCommentUpdateDTO;
import com.feishu.entity.Revision;
import com.feishu.entity.TextComment;
import com.feishu.entity.User;
import com.feishu.repository.RevisionRepository;
import com.feishu.repository.TextCommentRepository;
import com.feishu.repository.UserRepository;
import com.feishu.result.Result;
import com.feishu.service.RevisionService;
import com.feishu.service.TextCommentService;
import com.feishu.vo.CommentCreateVO;
import com.feishu.vo.RevisionCreateVO;
import com.feishu.vo.TextCommentVO;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TextCommentServiceImpl implements TextCommentService {

    @Autowired
    private TextCommentRepository textCommentRepository;

    @Autowired
    private UserRepository userRepository;

    private TextCommentVO convertToVO(TextComment textComment) {
        TextCommentVO textCommentVO = new TextCommentVO();
        BeanUtils.copyProperties(textComment, textCommentVO);
        textCommentVO.setId(textComment.getId().toString());
        ObjectId userId = textComment.getUserId();
        textCommentVO.setUserId(userId.toString());
        textCommentVO.setDocumentId(textComment.getDocumentId().toString());
        User user = userRepository.findById(userId);
        String username = user.getUsername();
        String picture = user.getPicture();
        textCommentVO.setUsername(username);
        textCommentVO.setPicture(picture);
        return textCommentVO;
    }

    @Override
    public TextCommentVO create(TextCommentCreateDTO commentCreateDTO) {
        String userId = BaseContext.getCurrentId();
        ObjectId objectId = new ObjectId(userId);
        TextComment textComment = new TextComment();
        textComment.setUserId(objectId);
        textComment.setDelta(commentCreateDTO.getDelta());
        textComment.setDocumentId(new ObjectId(commentCreateDTO.getDocumentId()));
        textComment.setUpdateAt(LocalDateTime.now());
        textComment.setCreateAt(LocalDateTime.now());
        textComment.setPosition(commentCreateDTO.getPosition());
        textComment.setContent(commentCreateDTO.getContent());
        textCommentRepository.save(textComment);
        return convertToVO(textComment);
    }

    @Override
    public List<TextCommentVO> getComments(String documentId) {
        List<TextComment> commentsList = textCommentRepository.findByDocumentId(new ObjectId(documentId));
        return commentsList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Result deleteComment(String id) {
        String userId = BaseContext.getCurrentId();
        ObjectId objectId = new ObjectId(id);
        TextComment textComment = textCommentRepository.findById(objectId).orElse(null);
        String authorId = textComment.getUserId().toString();
        if (!userId.equals(authorId)) {
            return Result.error(MessageConstant.UNAUTHOR);
        }
        textCommentRepository.deleteById(objectId);
        return Result.success();
    }

    @Override
    public Result update(TextCommentUpdateDTO textCommentUpdateDTO) {
        String userId = BaseContext.getCurrentId();
        ObjectId objectId = new ObjectId(textCommentUpdateDTO.getId());
        TextComment textComment = textCommentRepository.findById(objectId).orElse(null);
        String authorId = textComment.getUserId().toString();
        if (!userId.equals(authorId)) {
            return Result.error(MessageConstant.UNAUTHOR);
        }
        textComment.setDelta(textCommentUpdateDTO.getDelta());
        textComment.setPosition(textCommentUpdateDTO.getPosition());
        textComment.setContent(textCommentUpdateDTO.getContent());
        textComment.setUpdateAt(LocalDateTime.now());
        textCommentRepository.save(textComment);
        TextCommentVO textCommentVO = convertToVO(textComment);
        return Result.success(textCommentVO);
    }
}
