package com.feishu.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FolderMoveDTO implements Serializable {
    private String newParentId;
    private String curFolderId;

}
