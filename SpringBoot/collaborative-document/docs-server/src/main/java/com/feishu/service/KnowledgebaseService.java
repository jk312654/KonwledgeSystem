package com.feishu.service;

import com.feishu.dto.KnowledgebaseCreateDTO;
import com.feishu.dto.KnowledgebasePutDTO;
import com.feishu.entity.Knowledgebase;
import com.feishu.vo.KnowledgebaseVO;

import java.util.List;

public interface KnowledgebaseService {
    KnowledgebaseVO getById(String id);

    KnowledgebaseVO create(KnowledgebaseCreateDTO knowledgebaseCreateDTO);

    List<KnowledgebaseVO> listByCurrentUser();

    List<KnowledgebaseVO> listByCollaborator();
    void deleteKnowledgebaseById(String id);

    void updateKnowledgebase(String kbId, KnowledgebasePutDTO dto);
}
