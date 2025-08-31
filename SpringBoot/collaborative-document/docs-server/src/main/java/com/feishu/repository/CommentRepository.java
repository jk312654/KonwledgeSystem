package com.feishu.repository;

import com.feishu.entity.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
    List<Comment> findByDocumentId(ObjectId authorId);
    Long deleteByDocumentId(ObjectId authorId);
}
