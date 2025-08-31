package com.feishu.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DocumentsSearchDTO implements Serializable {

    private String knowledgeId;

    private String keyword;
}
