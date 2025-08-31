package com.feishu.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FolderCreateVO implements Serializable {

    // 目录id
    private String id;
    // 目录作者id
    private String authorId;
    // 目录作者名
    private String author;
    // 目录标题
    private String title;
    // "folder"
    private String type;
    // 目录的父路劲id
    private String parentId;
    // 目录所属知识库id
    private String knowledgebaseId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createAt;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateAt;
}
