package com.feishu.repository;

import com.feishu.entity.Documents;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DocumentsRepository extends MongoRepository<Documents, ObjectId> {
    List<Documents> findByParentId(ObjectId id);
    List<Documents> findByKnowledgebaseId(ObjectId id);
    void deleteByKnowledgebaseId(ObjectId knowledgebaseId);
    @Query(value = """
    {
        '$and': [
            {'knowledgebaseId': ?1},
            {'$text': {'$search': ?0}}
        ]
    }
    """)
    List<Documents> findByContentAndKnowledgebaseId(String search, ObjectId knowledgebaseId);
}
