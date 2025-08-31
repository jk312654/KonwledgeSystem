package com.feishu.repository;

import com.feishu.entity.Revision;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface RevisionRepository extends MongoRepository<Revision, ObjectId> {
    void deleteByDocumentId(ObjectId id);
    List<Revision> findByDocumentId(ObjectId id);
}
