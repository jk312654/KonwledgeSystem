package com.feishu.service.impl;

import com.feishu.context.BaseContext;
import com.feishu.dto.CommentCreateDTO;
import com.feishu.entity.Comment;
import com.feishu.entity.Documents;
import com.feishu.entity.Revision;
import com.feishu.repository.CommentRepository;
import com.feishu.result.Result;
import com.feishu.service.CommentService;
import com.feishu.vo.CommentCreateVO;
import com.feishu.vo.DocumentsVO;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public CommentCreateVO create(CommentCreateDTO commentCreateDTO) {
        Comment comment = new Comment();
        String authorId = BaseContext.getCurrentId();
        String documentId = commentCreateDTO.getDocumentId();
        comment.setId(new ObjectId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setAuthorId(new ObjectId(authorId));
        comment.setDocumentId(new ObjectId(documentId));
        comment.setCreateAt(LocalDateTime.now());
        commentRepository.save(comment);
        return convertToVO(comment);
    }

    @Override
    public List<CommentCreateVO> getComments(String documentId) {
        List<Comment> commentsList = commentRepository.findByDocumentId(new ObjectId(documentId));
        return commentsList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(String commentId) {
        commentRepository.deleteById(new ObjectId(commentId));
    }

    @Override
    public void deleteAllComment(String documentId) {
        commentRepository.deleteByDocumentId(new ObjectId(documentId));
    }

    private CommentCreateVO convertToVO(Comment comment) {
        CommentCreateVO commentCreateVO = new CommentCreateVO();
        BeanUtils.copyProperties(comment, commentCreateVO);
        commentCreateVO.setId(comment.getId().toString());
        commentCreateVO.setAuthorId(comment.getAuthorId().toString());
        commentCreateVO.setDocumentId(comment.getDocumentId().toString());
        return commentCreateVO;
    }
}
