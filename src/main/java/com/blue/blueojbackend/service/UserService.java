package com.blue.blueojbackend.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.blue.blueojbackend.domain.dto.UserLoginRequest;
import com.blue.blueojbackend.domain.dto.UserRegisterRequest;
import com.blue.blueojbackend.domain.entity.User;
import com.blue.blueojbackend.domain.vo.UserLoginVO;
import com.blue.blueojbackend.domain.vo.UserVO;

public interface UserService extends IService<User> {
    UserVO register(UserRegisterRequest userRegisterRequest);
    UserLoginVO login(UserLoginRequest userLoginRequest);
    UserVO getCurrentUserInfo(Long userId);
}
