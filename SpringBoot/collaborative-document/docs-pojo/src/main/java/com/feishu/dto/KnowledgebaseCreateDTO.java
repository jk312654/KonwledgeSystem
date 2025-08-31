package com.feishu.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class KnowledgebaseCreateDTO implements Serializable {
    // 知识库名称
    private String title;

    // 知识库描述
    private String desc;

    // 知识库标识
    private String icon;
}
