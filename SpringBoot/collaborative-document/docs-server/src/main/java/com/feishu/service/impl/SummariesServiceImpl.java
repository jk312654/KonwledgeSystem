package com.feishu.service.impl;

import com.feishu.entity.Documents;
import com.feishu.entity.Summaries;
import com.feishu.repository.DocumentsRepository;
import com.feishu.repository.SummariesRepository;
import com.feishu.service.SummariesService;
import com.feishu.vo.SummariesVO;
import org.bson.types.ObjectId;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SummariesServiceImpl implements SummariesService {

    @Autowired
    private ChatClient summaryChatClient;
    
    @Autowired
    private SummariesRepository summariesRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Override
    public SummariesVO getSummary(String documentsId) {
        ObjectId objectId = new ObjectId(documentsId);
        Summaries findSummaries = summariesRepository.findByDocumentsId(objectId);
        SummariesVO summariesVO = new SummariesVO();
        if (findSummaries != null) {
            BeanUtils.copyProperties(findSummaries, summariesVO);
            return summariesVO;
        }
        Summaries summaries = new Summaries();
        String content = summaryChatClient.prompt(documentsRepository.findById(objectId).orElse(null).getContent())
                .call()
                .content();
        summaries.setSummaryText(content);
        summaries.setDocumentsId(objectId);
        summaries.setCreateAt(LocalDateTime.now());
        summariesRepository.save(summaries);
        BeanUtils.copyProperties(summaries, summariesVO);
        return summariesVO;
    }
}
