package com.feishu.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgebaseVO implements Serializable{
    // 知识库id
    private String id;

    private String authorId;

    private String authorName;

    private List<String> xietongAuthor;

    // 知识库名称
    private String title;

    private String icon;

    private String desc;

    private List<Object> children;
    // 当前知识库下目录和文档的子id集合
    private List<String> childrenId;

    // 创建日期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

}
