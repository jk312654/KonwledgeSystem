package com.feishu.controller;

import com.alibaba.cola.dto.DTO;
import com.feishu.dto.CommentCreateDTO;
import com.feishu.dto.DocumentsCreateDTO;
import com.feishu.dto.DocumentsUpdateDTO;
import com.feishu.dto.RevisionUpdateDTO;
import com.feishu.result.Result;
import com.feishu.service.CommentService;
import com.feishu.vo.CommentCreateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/create")
    public Result<CommentCreateVO> createComment(@RequestBody CommentCreateDTO commentCreateDTO) {
        CommentCreateVO commentCreateVO = commentService.create(commentCreateDTO);
        return Result.success(commentCreateVO);
    }

    @PutMapping("/{documentId}")
    public Result<List<CommentCreateVO>> getComments(@PathVariable("documentId") String documentId) {
        List<CommentCreateVO> commentsList = commentService.getComments(documentId);
        return Result.success(commentsList);
    }

    @DeleteMapping("delete/{commentId}")
    public Result deleteComment(@PathVariable("commentId") String commentId) {
        commentService.deleteComment(commentId);
        return Result.success();
    }

    @DeleteMapping("deleteAll/{documentId}")
    public Result deleteAllComment(@PathVariable("documentId") String documentId) {
        commentService.deleteAllComment(documentId);
        return Result.success();
    }
}
