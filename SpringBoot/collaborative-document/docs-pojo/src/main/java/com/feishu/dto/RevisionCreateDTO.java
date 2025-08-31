package com.feishu.dto;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class RevisionCreateDTO {
    private String documentId;
    private Object delta;
    private String type;
    private Object position;
}
