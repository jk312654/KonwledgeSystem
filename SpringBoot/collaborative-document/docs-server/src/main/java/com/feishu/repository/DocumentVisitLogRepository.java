package com.feishu.repository;

import com.feishu.entity.DocumentVisitLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DocumentVisitLogRepository extends MongoRepository<DocumentVisitLog, ObjectId> {

    List<DocumentVisitLog> findTop20ByUserIdOrderByVisitTimeDesc(ObjectId userId);

    // 如果一个用户可能访问同一文档多次，也可以考虑加去重逻辑
}