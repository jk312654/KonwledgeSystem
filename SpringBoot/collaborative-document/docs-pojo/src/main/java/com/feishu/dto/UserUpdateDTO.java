package com.feishu.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserUpdateDTO implements Serializable {

    //用户名
    private String username;

    //头像
    private String picture;

    private String desc;
}
