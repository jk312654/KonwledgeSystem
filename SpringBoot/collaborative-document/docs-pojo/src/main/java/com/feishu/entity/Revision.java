package com.feishu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "revision")
public class Revision  implements Serializable {
    private static final long serialVersionUID = 1L;
    // 修订id
    @Id
    private ObjectId id;
    // 文档id
    private ObjectId documentId;
    // 修订的delta数据
    private Object delta;
    // 修订位置
    private Object position;
    // 修订状态
    private String status;
    // 修订类型
    private String type;
    // 用户id
    private ObjectId userId;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;
}
