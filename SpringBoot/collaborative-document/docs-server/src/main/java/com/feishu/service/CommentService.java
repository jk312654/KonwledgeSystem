package com.feishu.service;

import com.feishu.dto.CommentCreateDTO;
import com.feishu.result.Result;
import com.feishu.vo.CommentCreateVO;

import java.util.List;

public interface CommentService {
    CommentCreateVO create(CommentCreateDTO commentCreateDTO);

    List<CommentCreateVO> getComments(String documentId);

    void deleteComment(String commentId);

    void deleteAllComment(String documentId);

}
