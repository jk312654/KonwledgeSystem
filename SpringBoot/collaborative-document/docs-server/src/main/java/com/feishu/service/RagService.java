package com.feishu.service;

import com.feishu.entity.RagQ;
import com.feishu.entity.RagQK;
import com.feishu.vo.RagQVO;

public interface RagService {
    RagQVO docGetAnswer(RagQ ragQ);

    RagQVO kbGetAnswer(RagQK ragQK);

    RagQVO QADocGetAnswer(RagQ ragQ);
}
