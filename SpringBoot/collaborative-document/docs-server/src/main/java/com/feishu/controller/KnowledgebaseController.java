package com.feishu.controller;


import com.feishu.dto.DocumentsSearchDTO;
import com.feishu.dto.KnowledgebaseCreateDTO;
import com.feishu.dto.KnowledgebasePutDTO;
import com.feishu.entity.Knowledgebase;
import com.feishu.result.Result;
import com.feishu.service.DocumentsService;
import com.feishu.service.KnowledgebaseService;
import com.feishu.vo.DocumentsVO;
import com.feishu.vo.KnowledgebaseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/knowledgebases")
public class KnowledgebaseController {

    @Autowired
    private KnowledgebaseService knowledgebaseService;

    @Autowired
    private DocumentsService documentsService;

    /**
     * 创建知识库
     * @param knowledgebaseCreateDTO
     * @return
     */
    @PostMapping
    public Result<KnowledgebaseVO> create(@RequestBody KnowledgebaseCreateDTO knowledgebaseCreateDTO) {
        KnowledgebaseVO knowledgebaseVO = knowledgebaseService.create(knowledgebaseCreateDTO);
        return Result.success(knowledgebaseVO);
    }

    /**
     * 根据知识库id，查询内容
     * @param id
     * @return
     */
    @GetMapping("/detail/{kbId}")
    public Result<KnowledgebaseVO> getById(@PathVariable("kbId") String id){
        KnowledgebaseVO knowledgebaseVO = knowledgebaseService.getById(id);
        return Result.success(knowledgebaseVO);
    }

    /**
     * 获取当前登录用户相关的所有知识库，只有
     * 自己创建的知识库；
     * @return
     */
    @GetMapping("/my/all")
    public Result<List<KnowledgebaseVO>> list() {
    List<KnowledgebaseVO> list = knowledgebaseService.listByCurrentUser();
    return Result.success(list);
    }

    /**
     * 获取当前登录用户相关的所有知识库，只有：
     * 自己作为协作者参与的知识库；
     * @return
     */
    @GetMapping("/my/collaborate")
    public Result<List<KnowledgebaseVO>> listByCollaborator() {
    List<KnowledgebaseVO> list = knowledgebaseService.listByCollaborator();
    return Result.success(list);
    }


    @DeleteMapping("/remove/{kbId}")
    public Result deleteKnowledgebase(@PathVariable("kbId") String id) {

        knowledgebaseService.deleteKnowledgebaseById(id);
        return Result.success("成功删除");
    }


    @PutMapping("/edit/{kbId}")
    public Result updateKnowledgebase(
        @PathVariable("kbId") String kbId,
        @RequestBody KnowledgebasePutDTO dto) {
        knowledgebaseService.updateKnowledgebase(kbId, dto);
        return Result.success("修改成功");
    }

    @PostMapping("/search")
    public Result<List<DocumentsVO>> searchDocuments(@RequestBody DocumentsSearchDTO documentsSearchDTO) {
        try {
            String knowledgebaseId = documentsSearchDTO.getKnowledgeId();
            String keyword = documentsSearchDTO.getKeyword();
            List<DocumentsVO> documentsVOList= documentsService.searchDocument(knowledgebaseId, keyword);
            return Result.success(documentsVOList);
        } catch (IOException e) {
            // 处理异常，例如记录日志，返回错误结果
            e.printStackTrace();
            return Result.error("搜索失败");
        }
    }
}
