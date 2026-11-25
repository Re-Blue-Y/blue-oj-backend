package com.blue.blueojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.blue.blueojbackend.auth.AuthVersionService;
import com.blue.blueojbackend.auth.IssuedToken;
import com.blue.blueojbackend.auth.JwtService;
import com.blue.blueojbackend.common.ErrorCode;
import com.blue.blueojbackend.common.UserRole;
import com.blue.blueojbackend.domain.dto.UserLoginRequest;
import com.blue.blueojbackend.domain.dto.UserRegisterRequest;
import com.blue.blueojbackend.domain.entity.User;
import com.blue.blueojbackend.domain.vo.UserLoginVO;
import com.blue.blueojbackend.domain.vo.UserVO;
import com.blue.blueojbackend.exception.BusinessException;
import com.blue.blueojbackend.mapper.UserMapper;
import com.blue.blueojbackend.service.UserService;
import com.blue.blueojbackend.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordUtils passwordUtils;
    private final JwtService jwtService;
    private final AuthVersionService authVersionService;

    @Override
    public UserVO register(UserRegisterRequest userRegisterRequest) {
        //首先判断当前账号是否已经被注册
        String userAccount = userRegisterRequest.getUserAccount();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserAccount, userAccount));
        if (user != null) {
            throw new BusinessException(ErrorCode.USER_ACCOUNT_EXISTS, ErrorCode.USER_ACCOUNT_EXISTS.getMessage());
        }
        //接着判断两次输入的密码是否一致
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        if (!passwordUtils.isEncodingSupported(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码编码后不能超过72字节");
        }
        //如果没有被注册，并且密码一致，那么插入到数据库当中
        User registerUser = new User();
        registerUser.setUserAccount(userAccount);
        registerUser.setUserPassword(passwordUtils.encode(userPassword));
        registerUser.setUserRole(UserRole.USER.getUserRole());
        int insert = userMapper.insert(registerUser);
        if (insert != 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户注册失败");
        }
        return UserVO.toVO(registerUser);
    }

    @Override
    public UserLoginVO login(UserLoginRequest userLoginRequest) {
        String userAccount = userLoginRequest.getUserAccount();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserAccount, userAccount));
        String inputPassword = userLoginRequest.getUserPassword();
        // 账号不存在和密码错误使用相同提示，避免泄露账号是否存在
        if (user == null || !passwordUtils.matches(inputPassword, user.getUserPassword())) {
            throw new BusinessException(ErrorCode.USER_LOGIN_ERROR);
        }
        if (UserRole.BAN.getUserRole().equals(user.getUserRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "账号已被封禁");
        }
        long authVersion = authVersionService.getVersion(user.getId());
        IssuedToken issuedToken = jwtService.issue(user.getId(), user.getUserRole(), authVersion);
        return new UserLoginVO(
                issuedToken.value(),
                "Bearer",
                issuedToken.expiresAt(),
                UserVO.toVO(user)
        );
    }

    @Override
    public UserVO getCurrentUserInfo(Long userId) {
        //判断用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, ErrorCode.NOT_FOUND_ERROR.getMessage());
        }
        if (UserRole.BAN.getUserRole().equals(user.getUserRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "账号已被封禁");
        }
        //如果存在那么直接返回VO
        return UserVO.toVO(user);
    }
}
