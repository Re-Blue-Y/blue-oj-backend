package com.blue.blueojbackend.auth;

import com.blue.blueojbackend.common.ErrorCode;
import com.blue.blueojbackend.exception.BusinessException;

/**
 * 当前请求的用户上下文。只能在完成 JWT 校验后写入。
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser loginUser) {
        CURRENT_USER.set(loginUser);
    }

    public static LoginUser get() {
        return CURRENT_USER.get();
    }

    public static LoginUser getRequired() {
        LoginUser loginUser = get();
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return loginUser;
    }

    public static void remove() {
        CURRENT_USER.remove();
    }
}
