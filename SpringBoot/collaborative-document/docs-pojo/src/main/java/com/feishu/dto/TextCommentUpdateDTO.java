package com.feishu.dto;

import lombok.Data;

@Data
public class TextCommentUpdateDTO {
    private String id;
    private Object position;
    private Object delta;
    private String content;
}
