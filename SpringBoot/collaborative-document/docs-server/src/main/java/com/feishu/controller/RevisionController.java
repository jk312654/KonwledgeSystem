package com.feishu.controller;

import com.feishu.dto.DocumentsCreateDTO;
import com.feishu.dto.RevisionCreateDTO;
import com.feishu.dto.RevisionUpdateDTO;
import com.feishu.result.Result;
import com.feishu.service.RevisionService;
import com.feishu.vo.DocumentsVO;
import com.feishu.vo.RevisionCreateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/revision")
public class RevisionController {
    @Autowired
    RevisionService revisionService;

    @PostMapping("/create")
    public Result<RevisionCreateVO> createRevision(@RequestBody RevisionCreateDTO revisionCreateDTO) {
        RevisionCreateVO revisionCreateVO = revisionService.create(revisionCreateDTO);
        return Result.success(revisionCreateVO);
    }

    @PutMapping("/update")
    public Result updateRevision(@RequestBody RevisionUpdateDTO revisionUpdateDTO) {
        Result result = revisionService.update(revisionUpdateDTO);
        return result;
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteRevision(@PathVariable String id) {
        Result result = revisionService.delete(id);
        return result;
    }

    @GetMapping("/{documentId}")
    public Result<List<RevisionCreateVO>> getRevision(@PathVariable String documentId) {
       List<RevisionCreateVO> revisionCreateVOList = revisionService.getById(documentId);
       return Result.success(revisionCreateVOList);
    }
}
