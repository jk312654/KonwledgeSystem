package com.feishu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documents")
public class Documents implements Serializable {

    private static final long serialVersionUID = 1L;
    // 文档id
    @Id
    private ObjectId id;
    // 文档作者id
    private ObjectId authorId;
    // 文档作者名
    private String authorName;
    // 文档所属知识库id
    private ObjectId knowledgebaseId;
    // 文档标题
    private String title;
    // 文档内容
    private String content;
    // 文档类型 document
    private String type;
    private String slug;
    // 文档的父级id
    private ObjectId parentId;

    private Integer version; // 版本号
    private List<ObjectId> collaborators; // 协作者列表 单独维护协同者

    private ObjectId lastEditedBy; // 最后修改者的id
    private String lastEditedName; // 最后修改者的名字
    private LocalDateTime lastEditedAt; // 最后修改的时间
    private Object delta; // 用于记录最后一次Quill delta值
    // 存储分词结果的字段
    private Set<String> contentTokens;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;
}
