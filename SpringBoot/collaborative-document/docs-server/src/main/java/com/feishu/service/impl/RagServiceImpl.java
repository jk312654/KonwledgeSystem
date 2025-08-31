package com.feishu.service.impl;

import com.feishu.context.BaseContext;
import com.feishu.entity.RagQ;
import com.feishu.entity.RagQK;
import com.feishu.entity.Summaries;
import com.feishu.repository.DocumentsRepository;
import com.feishu.service.RagService;
import com.feishu.vo.RagQVO;
import com.feishu.vo.SummariesVO;
import org.bson.types.ObjectId;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@Service
public class RagServiceImpl implements RagService {

    @Autowired
    private ChatClient ragChatClient;

    @Autowired
    private ChatClient QAChatClient;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Override
    public RagQVO docGetAnswer(RagQ ragQ) {
        String documentsId = ragQ.getDocumentsId();
        String userId = BaseContext.getCurrentId();
        String chatMemoryId = documentsId + "_" + userId;
        String filterExpression = String.format("docId == '%s'", documentsId);
        String answer = ragChatClient.prompt(ragQ.getPrompt())
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatMemoryId))
                .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, filterExpression))
                .call()
                .content();
        RagQVO ragQVO = new RagQVO();
        ragQVO.setAnswer(answer);
        ragQVO.setCreateAt(LocalDateTime.now());
        return ragQVO;
    }

    @Override
    public RagQVO kbGetAnswer(RagQK ragQK) {
        String knowledgebaseId = ragQK.getKnowledgebaseId();
        String userId = BaseContext.getCurrentId();
        String chatMemoryId = knowledgebaseId + "_" + userId;
        String filterExpression = String.format("kbId == '%s'", knowledgebaseId);
        String answer = ragChatClient.prompt(ragQK.getPrompt())
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatMemoryId))
                .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, filterExpression))
                .call()
                .content();
        RagQVO ragQVO = new RagQVO();
        ragQVO.setAnswer(answer);
        ragQVO.setCreateAt(LocalDateTime.now());
        return ragQVO;
    }

    @Override
    public RagQVO QADocGetAnswer(RagQ ragQ) {
        String documentsId = ragQ.getDocumentsId();
        ObjectId objectId = new ObjectId(ragQ.getDocumentsId());
        String userId = BaseContext.getCurrentId();
        String chatMemoryId = documentsId + "_" + userId;
        String answer = QAChatClient.prompt(documentsRepository.findById(objectId).orElse(null).getContent())
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatMemoryId))
                .call()
                .content();
        RagQVO ragQVO = new RagQVO();
        ragQVO.setAnswer(answer);
        ragQVO.setCreateAt(LocalDateTime.now());
        return ragQVO;
    }
}
