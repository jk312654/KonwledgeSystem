package com.feishu.config;

import com.feishu.constant.ChatConstant;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;

import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfiguration {
    @Bean
    public ChatClient summaryChatClient(OpenAiChatModel model) {
        return ChatClient
                .builder(model)
                .defaultSystem(ChatConstant.SUMMARY_DEFAULT_SYSTEM)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore
                .builder(embeddingModel)
                .build();
    }

    @Bean
    public ChatClient ragChatClient(OpenAiChatModel model, VectorStore vectorStore, ChatMemory chatMemory) {
        return ChatClient
                .builder(model)
                .defaultSystem(ChatConstant.RAG_DEFAULT_SYSTEM)
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory),
                        new QuestionAnswerAdvisor(vectorStore,
                                SearchRequest.builder()
                                .similarityThreshold(0.5d)
                                .topK(3)
                                .build()
                        )
                )
                .build();
    }

    @Bean
    public ChatClient QAChatClient(OpenAiChatModel model, VectorStore vectorStore, ChatMemory chatMemory) {
        return ChatClient
                .builder(model)
                .defaultSystem(ChatConstant.QA_DEFAULT_SYSTEM)
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }
}


