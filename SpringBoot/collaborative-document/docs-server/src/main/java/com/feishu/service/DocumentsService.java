package com.feishu.service;

import com.feishu.dto.DocumentMoveDTO;
import com.feishu.dto.DocumentsCreateDTO;
import com.feishu.dto.DocumentsUpdateDTO;
import com.feishu.result.Result;
import com.feishu.vo.DocumentMoveVO;
import com.feishu.vo.DocumentsLatestVisitedVO;
import com.feishu.vo.DocumentsVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

public interface DocumentsService {
    DocumentsVO create(DocumentsCreateDTO documentsCreateDTO);

    DocumentsVO getDocument(String id);

    List<DocumentsVO> getDocumentByParent(String id);

    DocumentsVO update(DocumentsUpdateDTO documentsUpdateDTO) throws IOException;

    void delete(String id);

    List<DocumentsLatestVisitedVO> getRecentDocuments();

    DocumentMoveVO moveDocument( DocumentMoveDTO moveDTO);

    List<DocumentsVO> searchDocument(String knowledgebaseId, String keyword) throws IOException ;
}
