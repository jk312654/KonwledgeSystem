package com.feishu.dto;

import lombok.Data;

@Data
public class RevisionUpdateDTO {
    private String id;
    private Object delta;
    private String status;
    private Object position;
}
