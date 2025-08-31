package com.feishu.dto;

import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;


@Data
public class KnowledgebasePutDTO implements Serializable {

    List<String> xietongAuthor;

    String title;

    String icon;

    String desc;
}
