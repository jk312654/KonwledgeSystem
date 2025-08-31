package com.feishu.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FolderVO implements Serializable {

    // 目录id
    private String id;
    // 目录作者id
    private String authorId;
    // 目录作者名
    private String author;
    // 目录标题
    private String title;
    private String type; // "folder"
    // 目录的父路劲id
    private String parentId;
    // 目录所属知识库id
    private String knowledgebaseId;

    // 当前目录下的文档或目录的id集合
    private List<String> childrenId;

    private List<Object> children;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

}
