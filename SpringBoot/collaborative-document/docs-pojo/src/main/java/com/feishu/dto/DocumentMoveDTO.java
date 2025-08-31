package com.feishu.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class DocumentMoveDTO implements Serializable {
    private String newParentId;
    private String curDocumentId;

}
