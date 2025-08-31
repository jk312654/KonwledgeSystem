package com.feishu.service;

import com.feishu.dto.UserCreateDTO;
import com.feishu.dto.UserLoginDTO;
import com.feishu.dto.UserUpdateDTO;
import com.feishu.dto.UserUpdatePasswordDTO;
import com.feishu.entity.User;
import com.feishu.result.Result;

import java.util.List;

public interface UserService {
    Result createUser(UserCreateDTO userDTO);

    User login(UserLoginDTO userLoginDTO);

    User getUser();

    void deleteUser();

    Result updateUser(UserUpdateDTO userUpdateDTO);

    Result userUpdatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO);

    User getUserById(String userId);
}
