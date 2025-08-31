package com.feishu.service.impl;

import com.feishu.constant.MessageConstant;
import com.feishu.constant.PasswordConstant;
import com.feishu.context.BaseContext;
import com.feishu.dto.UserCreateDTO;
import com.feishu.dto.UserLoginDTO;
import com.feishu.dto.UserUpdateDTO;
import com.feishu.dto.UserUpdatePasswordDTO;
import com.feishu.entity.User;
import com.feishu.exception.AccountNotFoundException;
import com.feishu.exception.PasswordErrorException;
import com.feishu.repository.UserRepository;
import com.feishu.result.Result;
import com.feishu.service.UserService;
import com.feishu.vo.UserLoginVO;
import com.feishu.vo.UserUpdateVO;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public Result createUser(UserCreateDTO userDTO) {
        String username = userDTO.getUsername();
        if (userRepository.findByUsername(username) != null){
            return Result.error(MessageConstant.USERNAME_EXISTS);
        }
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPasswordHash(DigestUtils.md5DigestAsHex(userDTO.getPasswordHash().getBytes()));
        user.setCreateAt(LocalDateTime.now());
        user.setUpdateAt(LocalDateTime.now());
        userRepository.save(user);
        return Result.success();
    }


    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPasswordHash();

        //1、根据用户名查询数据库中的数据
        User user = userRepository.findByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端传过来的明文密码进行md5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPasswordHash())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //3、返回实体对象
        return user;
    }

    @Override
    public User getUser() {
        String userid = BaseContext.getCurrentId();
        ObjectId objectId = new ObjectId(userid);
        return userRepository.findById(objectId);
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(new ObjectId(userId));
    }

    @Override
    public void deleteUser() {
        String userid = BaseContext.getCurrentId();
        ObjectId objectId = new ObjectId(userid);
        userRepository.deleteById(objectId);
    }

    @Override
    public Result updateUser(UserUpdateDTO userUpdateDTO) {
        String userid = BaseContext.getCurrentId();
        ObjectId objectId = new ObjectId(userid);
        String newUsername = userUpdateDTO.getUsername();
        if (userRepository.existsByUsernameAndIdNot(newUsername, objectId)){
            return Result.error(MessageConstant.USERNAME_EXISTS);
        }
        User user = userRepository.findById(objectId);
        user.setUsername(newUsername);
        user.setUpdateAt(LocalDateTime.now());
        user.setPicture(userUpdateDTO.getPicture());
        user.setDesc(userUpdateDTO.getDesc());
        userRepository.save(user);
        UserUpdateVO userUpdateVO = UserUpdateVO.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .picture(user.getPicture())
                .desc(user.getDesc())
                .build();
        return Result.success(userUpdateVO);
    }

    @Override
    public Result userUpdatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO) {
        String userid = BaseContext.getCurrentId();
        ObjectId objectId = new ObjectId(userid);
        User user = userRepository.findById(objectId);
        if (user == null) {
            return Result.error(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        String password = DigestUtils.md5DigestAsHex(userUpdatePasswordDTO.getPassword().getBytes());
        if (!password.equals(user.getPasswordHash())) {
            //密码错误
            return Result.error(MessageConstant.PASSWORD_ERROR);
        }
        user.setPasswordHash(DigestUtils.md5DigestAsHex(userUpdatePasswordDTO.getNewPassword().getBytes()));
        user.setUpdateAt(LocalDateTime.now());
        userRepository.save(user);
        UserUpdateVO userUpdateVO = UserUpdateVO.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .picture(user.getPicture())
                .build();
        return Result.success(userUpdateVO);
    }


}
