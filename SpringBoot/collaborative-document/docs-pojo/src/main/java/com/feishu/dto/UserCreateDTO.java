package com.feishu.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserCreateDTO implements Serializable {

    //用户名
    private String username;

    //邮箱
    private String email;

    //密码哈希
    private String passwordHash;
}
