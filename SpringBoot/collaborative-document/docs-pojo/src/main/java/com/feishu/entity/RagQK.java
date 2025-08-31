package com.feishu.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagQK implements Serializable {
    private static final long serialVersionUID = 1L;

    private String knowledgebaseId;

    private String prompt;
}
