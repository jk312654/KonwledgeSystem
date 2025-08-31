package com.feishu.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.feishu.entity.Documents;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FolderByIdVO implements Serializable {

    private String id;

    private String title;

    private String type;


    private List<Documents> childen;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createAt;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateAt;
}
