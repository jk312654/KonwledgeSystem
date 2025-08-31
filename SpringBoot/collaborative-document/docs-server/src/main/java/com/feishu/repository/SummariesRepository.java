package com.feishu.repository;

import com.feishu.entity.Summaries;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SummariesRepository extends MongoRepository<Summaries, String> {
    Summaries findByDocumentsId(ObjectId documentsId);
    Long deleteByDocumentsId(ObjectId documentsId);
    Boolean existsByDocumentsId(ObjectId documentsId);
}
