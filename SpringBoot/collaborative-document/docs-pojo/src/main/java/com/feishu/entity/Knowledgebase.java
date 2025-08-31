package com.feishu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "knowledgebases")
public class Knowledgebase implements Serializable {

    private static final long serialVersionUID = 1L;

    // 知识库id
    @Id
    private ObjectId id;

    // 知识库所属用户id
    private ObjectId authorId;
    // 知识库所属用户名
    private String authorName;

    // 知识库协调作者id集合
    private List<ObjectId> xietongAuthor;

    private String type; // 类型 知识库就是 knowledgebase

    // 当前知识库层的文档或目录的id集合
    private List<ObjectId> childrenId;

    // 知识库名字
    private String title;

    // 知识库标识
    private String icon;

    // 知识库介绍
    private String desc;


    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    // Getters and Setters
}
