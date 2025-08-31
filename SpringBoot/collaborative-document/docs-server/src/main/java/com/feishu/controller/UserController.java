package com.feishu.controller;

import com.feishu.constant.JwtClaimsConstant;
import com.feishu.dto.UserCreateDTO;
import com.feishu.dto.UserLoginDTO;
import com.feishu.dto.UserUpdateDTO;
import com.feishu.dto.UserUpdatePasswordDTO;
import com.feishu.entity.User;
import com.feishu.interceptor.JwtTokenUserInterceptor;
import com.feishu.properties.JwtProperties;
import com.feishu.result.Result;
import com.feishu.service.impl.UserServiceImpl;
import com.feishu.utils.JwtUtil;
import com.feishu.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/register")
    public Result createUser(@RequestBody UserCreateDTO userDTO) {
        Result result = userService.createUser(userDTO);
        return result;
    }

    @DeleteMapping("/del")
    public Result deleteUser(){
        userService.deleteUser();
        return Result.success();
    }

    @GetMapping("/info")
    public Result<UserLoginVO> getUser(HttpServletRequest request) {
        User user = userService.getUser();
        String token = (String) request.getAttribute(JwtTokenUserInterceptor.TOKEN_ATTRIBUTE_KEY);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .picture(user.getPicture())
                .desc(user.getDesc())
                .email(user.getEmail())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }

    @GetMapping("/infoById/{userId}")
    public Result<UserLoginVO> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .picture(user.getPicture())
                .build();
        return Result.success(userLoginVO);
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId().toString());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .picture(user.getPicture())
                .desc(user.getDesc())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    @PutMapping("/update")
    public Result updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        Result result = userService.updateUser(userUpdateDTO);
        return result;
    }

    @PutMapping("/password")
    public Result updatePassword(@RequestBody UserUpdatePasswordDTO userUpdatePasswordDTO) {
        Result result = userService.userUpdatePassword(userUpdatePasswordDTO);
        return result;
    }
}
