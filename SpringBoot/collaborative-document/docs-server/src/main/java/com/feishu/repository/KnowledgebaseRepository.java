package com.feishu.repository;

import com.feishu.entity.Knowledgebase;
import com.feishu.vo.KnowledgebaseVO;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface KnowledgebaseRepository extends MongoRepository<Knowledgebase, ObjectId> {
    Knowledgebase findByTitle(String name);

    List<Knowledgebase> findByAuthorId(Object authorId);

    List<Knowledgebase> findByXietongAuthorContains(ObjectId userId);
}
