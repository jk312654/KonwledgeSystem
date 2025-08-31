package com.feishu.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.ObjectError;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "folders")
@CompoundIndex(def = "{'parentId': 1, 'title': 1}", name = "uniq_parent_title", unique = true)
public class Folder implements Serializable {
    // 目录id
    @Id
    private ObjectId id;
    // 目录作者id
    private ObjectId authorId;
    // 目录作者名
    private String author;
    // 目录标题
    private String title;
    // 目录所属知识库id
    private ObjectId knowledgebaseId;

    // 当前目录层的文档和目录id集合
    private List<ObjectId> childrenId;

    private String type; // 类型 目录就是 folder

    private ObjectId parentId; // 上级目录


    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;
}
