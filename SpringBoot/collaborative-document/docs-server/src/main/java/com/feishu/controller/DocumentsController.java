package com.feishu.controller;

import com.feishu.constant.MessageConstant;
import com.feishu.dto.*;
import com.feishu.result.Result;
import com.feishu.service.DocumentsService;
import com.feishu.vo.DocumentMoveVO;
import com.feishu.vo.DocumentsLatestVisitedVO;
import com.feishu.vo.DocumentsVO;
import com.feishu.vo.FolderMoveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentsController {

    @Autowired
    DocumentsService documentsService;

    @PostMapping
    public Result<DocumentsVO> createDocument(@RequestBody DocumentsCreateDTO documentsCreateDTO) {
        DocumentsVO documentsVO = documentsService.create(documentsCreateDTO);
        return Result.success(documentsVO);

    }

    @GetMapping("/detail/{docId}")
    public Result<DocumentsVO> getDocument(@PathVariable("docId") String id) {
        DocumentsVO documentsVO = documentsService.getDocument(id);
        if (documentsVO == null) {
            return Result.error(MessageConstant.DOCUMENT_NOT_FOUND);
        }
        return Result.success(documentsVO);
    }

    @GetMapping("/of/{parentId}")
    public Result<List<DocumentsVO>> getDocumentByParent(@PathVariable("parentId") String id) {
        List<DocumentsVO> documentsVOList = documentsService.getDocumentByParent(id);
        if (documentsVOList.isEmpty()) {
            return Result.error(MessageConstant.DOCUMENT_NOT_FOUND);
        }
        return Result.success(documentsVOList);
    }

    @PutMapping("/edit")
    public Result<DocumentsVO> updateDocument(@RequestBody DocumentsUpdateDTO documentsUpdateDTO) {
        try {
            DocumentsVO documentsVO = documentsService.update(documentsUpdateDTO);
            return Result.success(documentsVO);
        } catch (IOException e) {
            // 处理异常，例如记录日志，返回错误结果
            e.printStackTrace();
            return Result.error("更新文档失败");
        }
    }

    @DeleteMapping("/remove/{docId}")
    public Result deleteDocument(@PathVariable("docId") String id) {
        documentsService.delete(id);
        return Result.success();
    }

    /**
     * 移动文档
     * @param moveDTO
     * @return
     */
    @PutMapping("/move")
    public Result<DocumentMoveVO> moveDocument(@RequestBody DocumentMoveDTO moveDTO){
        return Result.success(documentsService.moveDocument(moveDTO));
    }

    /**
     * 查询最近访问的文档
     * @return
     */
    @GetMapping("/recent")
    public Result<List<DocumentsLatestVisitedVO>> getRecentDocuments() {
        List<DocumentsLatestVisitedVO> recentDocs = documentsService.getRecentDocuments();
        return Result.success(recentDocs);
    }



}
