package com.feishu.service;

import com.feishu.dto.RevisionCreateDTO;
import com.feishu.dto.RevisionUpdateDTO;
import com.feishu.result.Result;
import com.feishu.vo.RevisionCreateVO;

import java.util.List;

public interface RevisionService {
    RevisionCreateVO create(RevisionCreateDTO revisionCreateDTO);

    Result update(RevisionUpdateDTO revisionUpdateDTO);

    Result delete(String id);

    List<RevisionCreateVO> getById(String documentId);
}
