package com.feishu.service;

import com.feishu.dto.TextCommentCreateDTO;
import com.feishu.dto.TextCommentUpdateDTO;
import com.feishu.result.Result;
import com.feishu.vo.CommentCreateVO;
import com.feishu.vo.TextCommentVO;

import java.util.List;

public interface TextCommentService {

    List<TextCommentVO> getComments(String documentId);

    Result deleteComment(String commentId);

    Result update(TextCommentUpdateDTO commentUpdateDTO);

    TextCommentVO create(TextCommentCreateDTO commentCreateDTO);
}
