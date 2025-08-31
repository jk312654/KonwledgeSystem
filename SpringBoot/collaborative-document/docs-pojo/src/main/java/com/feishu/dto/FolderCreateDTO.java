package com.feishu.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class FolderCreateDTO implements Serializable {

    String parentId;

    String title;
}
