package com.feishu.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;

public class ObjectIdUtils {
    public static List<String> toStringList(List<ObjectId> ids) {
        return ids == null ? new ArrayList<>() :
               ids.stream().map(ObjectId::toHexString).collect(Collectors.toList());
    }

    public static List<ObjectId> toObjectIdList(List<String> ids) {
        return ids == null ? new ArrayList<>() :
               ids.stream().map(ObjectId::new).collect(Collectors.toList());
    }
}
