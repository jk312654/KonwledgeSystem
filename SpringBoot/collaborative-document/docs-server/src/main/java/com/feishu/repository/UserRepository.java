package com.feishu.repository;
import com.feishu.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String>{
    boolean existsByUsernameAndIdNot(String username, ObjectId excludeId);

    User findByUsername(String username);

    User findById(ObjectId id);

    void deleteById(ObjectId id);
}