package com.feishu.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class LocalVectorStoreRepository {

    private final VectorStore vectorStore;

    @PostConstruct
    public void init() {
        FileSystemResource vectorResoure = new FileSystemResource("vectors.json");
        if (vectorResoure.exists()) {
            SimpleVectorStore simpleVectorStore = (SimpleVectorStore) vectorStore;
            simpleVectorStore.load(vectorResoure);
        }
    }

    @PreDestroy
    public void presistent() {
        SimpleVectorStore simpleVectorStore = (SimpleVectorStore) vectorStore;
        simpleVectorStore.save(new File("vectors.json"));
    }
}
