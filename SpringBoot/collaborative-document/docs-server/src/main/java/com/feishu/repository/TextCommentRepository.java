package com.feishu.repository;

import com.feishu.entity.TextComment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface TextCommentRepository extends MongoRepository<TextComment, ObjectId> {
    void deleteByDocumentId(ObjectId id);
    List<TextComment> findByDocumentId(ObjectId id);
}
