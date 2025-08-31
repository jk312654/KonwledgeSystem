package com.feishu.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserUpdatePasswordDTO implements Serializable {

    //用户名
    private String password;

    //头像
    private String newPassword;
}
