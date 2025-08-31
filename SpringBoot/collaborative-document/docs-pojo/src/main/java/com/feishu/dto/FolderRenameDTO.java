package com.feishu.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FolderRenameDTO implements Serializable {
    String newTitle;
}
