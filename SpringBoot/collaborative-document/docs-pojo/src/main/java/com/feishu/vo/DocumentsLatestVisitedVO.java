package com.feishu.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsLatestVisitedVO implements Serializable {
    private String documentId;

    private String documentTitle;

    private String authorName;

    private String knowledgebaseId;

    private String knowledgebaseTitle;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModifiedTime; // <-- 更贴合语义
}
