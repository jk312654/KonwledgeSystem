package com.feishu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "textcomment")
public class TextComment implements Serializable {
    private static final long serialVersionUID = 1L;
    // 文本评论id
    @Id
    private ObjectId id;
    // 评论用户id
    private ObjectId userId;
    // 文档id
    private ObjectId documentId;
    // 评论的文档内容delata数据
    private Object delta;
    // 评论位置
    private Object position;
    // 评论内容
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;
}
