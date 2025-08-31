package com.feishu.config;

import com.feishu.entity.Documents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort;


@Configuration
public class MongoConfig {
    @Autowired
    void ensureIndexes(MongoTemplate mongoTemplate) {
        // 为分词字段创建索引
        Index tokenIndex = new Index().on("contentTokens", Sort.Direction.ASC);
        mongoTemplate.indexOps(Documents.class).ensureIndex(tokenIndex);
    }
}
