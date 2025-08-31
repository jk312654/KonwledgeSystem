package com.feishu.controller;

import com.feishu.dto.CommentCreateDTO;
import com.feishu.dto.RevisionUpdateDTO;
import com.feishu.dto.TextCommentCreateDTO;
import com.feishu.dto.TextCommentUpdateDTO;
import com.feishu.result.Result;
import com.feishu.service.CommentService;
import com.feishu.service.TextCommentService;
import com.feishu.vo.CommentCreateVO;
import com.feishu.vo.TextCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/textComment")
public class TextCommentController {

    @Autowired
    private TextCommentService textCommentService;

    @PostMapping("/create")
    public Result<TextCommentVO> createComment(@RequestBody TextCommentCreateDTO commentCreateDTO) {
        TextCommentVO commentVO = textCommentService.create(commentCreateDTO);
        return Result.success(commentVO);
    }

    @GetMapping("/{documentId}")
    public Result<List<TextCommentVO>> getComments(@PathVariable("documentId") String documentId) {
        List<TextCommentVO> commentsList = textCommentService.getComments(documentId);
        return Result.success(commentsList);
    }

    @DeleteMapping("delete/{commentId}")
    public Result deleteComment(@PathVariable("commentId") String commentId) {
        textCommentService.deleteComment(commentId);
        return Result.success();
    }

    @PutMapping("/update")
    public Result updateRevision(@RequestBody TextCommentUpdateDTO commentUpdateDTO) {
        Result result = textCommentService.update(commentUpdateDTO);
        return result;
    }

}
