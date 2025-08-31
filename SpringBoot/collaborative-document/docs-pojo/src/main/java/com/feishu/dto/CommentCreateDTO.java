package com.feishu.dto;

import lombok.Data;

@Data
public class CommentCreateDTO {
    private String documentId;
    private String content;
}
