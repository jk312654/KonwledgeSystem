package com.feishu.dto;

import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
public class DocumentsUpdateDTO implements Serializable {
    private String id;

    private String title;

    private String content;

    private Object delta;
}
