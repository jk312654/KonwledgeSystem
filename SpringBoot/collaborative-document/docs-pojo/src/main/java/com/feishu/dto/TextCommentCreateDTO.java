package com.feishu.dto;

import lombok.Data;

@Data
public class TextCommentCreateDTO {
    private Object position;
    private String documentId;
    private Object delta;
    private String content;
}
