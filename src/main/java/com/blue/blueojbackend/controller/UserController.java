package com.blue.blueojbackend.controller;

import com.blue.blueojbackend.auth.LoginUser;
import com.blue.blueojbackend.auth.TokenBlacklistService;
import com.blue.blueojbackend.auth.UserContext;
import com.blue.blueojbackend.common.BaseResponse;
import com.blue.blueojbackend.common.ResultUtils;
import com.blue.blueojbackend.domain.dto.UserLoginRequest;
import com.blue.blueojbackend.domain.dto.UserRegisterRequest;
import com.blue.blueojbackend.domain.vo.UserLoginVO;
import com.blue.blueojbackend.domain.vo.UserVO;
import com.blue.blueojbackend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping(value = "/register")
    public BaseResponse<UserVO> register(@RequestBody @Validated UserRegisterRequest userRegisterRequest) {
        UserVO userVO = userService.register(userRegisterRequest);
        return ResultUtils.success(userVO);
    }

    @PostMapping(value = "/login")
    public BaseResponse<UserLoginVO> login(@RequestBody @Validated UserLoginRequest userLoginRequest) {
        return ResultUtils.success(userService.login(userLoginRequest));
    }

    @GetMapping(value = "/get/login")
    @SecurityRequirement(name = "bearerAuth")
    public BaseResponse<UserVO> getUserInfo() {
        LoginUser loginUser = UserContext.getRequired();
        return ResultUtils.success(userService.getCurrentUserInfo(loginUser.userId()));
    }

    @PostMapping(value = "/logout")
    @SecurityRequirement(name = "bearerAuth")
    public BaseResponse<Boolean> logout() {
        LoginUser loginUser = UserContext.getRequired();
        tokenBlacklistService.add(loginUser.tokenId(), loginUser.expiresAt());
        return ResultUtils.success(true);
    }

}
