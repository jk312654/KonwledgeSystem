package com.feishu.repository;

import com.feishu.entity.Folder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FolderRepository extends MongoRepository<Folder, ObjectId> {
    List<Folder> findByParentId(ObjectId parentId);

    void deleteByKnowledgebaseId(ObjectId knowledgebaseId);
}
