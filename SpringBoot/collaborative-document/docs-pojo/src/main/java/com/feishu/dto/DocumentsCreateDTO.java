package com.feishu.dto;

import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
public class DocumentsCreateDTO implements Serializable {

    private String parentId;

    private String title;

    private Object delta;

    private String content;


}
