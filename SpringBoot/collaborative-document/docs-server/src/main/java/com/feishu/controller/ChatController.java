package com.feishu.controller;

import com.feishu.entity.RagQ;
import com.feishu.entity.RagQK;
import com.feishu.result.Result;
import com.feishu.service.RagService;
import com.feishu.service.SummariesService;
import com.feishu.vo.RagQVO;
import com.feishu.vo.SummariesVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private RagService ragService;

    @Autowired
    private SummariesService summariesService;

    @GetMapping("/summary/{documentsId}")
    public Result<SummariesVO> summary(@PathVariable String documentsId) {
        SummariesVO summariesVO = summariesService.getSummary(documentsId);
        return Result.success(summariesVO);
    }

    @RequestMapping(value = "/rag/document")
    public Result<RagQVO> docRag(@RequestBody RagQ ragQ) {
        RagQVO ragQVO = ragService.docGetAnswer(ragQ);
        return Result.success(ragQVO);
    }

    @RequestMapping(value = "/rag/knowledgebase")
    public Result<RagQVO> kbRag(@RequestBody RagQK ragQK) {
        RagQVO ragQVO = ragService.kbGetAnswer(ragQK);
        return Result.success(ragQVO);
    }

    @RequestMapping(value = "/QADoc")
    public Result<RagQVO> QADoc(@RequestBody RagQ ragQ) {
        RagQVO ragQVO = ragService.QADocGetAnswer(ragQ);
        return Result.success(ragQVO);
    }
}
