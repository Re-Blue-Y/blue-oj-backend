package com.blue.blueojbackend.common;

/**
 * 全局业务错误码。
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),

    PARAMS_ERROR(40000, "请求参数错误"),

    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),

    FORBIDDEN_ERROR(40300, "禁止访问"),

    NOT_FOUND_ERROR(40400, "请求数据不存在"),

    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    USER_ACCOUNT_EXISTS(40000, "登录账号已经存在"),
    USER_LOGIN_ERROR(40001, "账号或密码错误"),
    API_REQUEST_ERROR(50010, "接口调用失败");

    /**
     * 业务状态码。
     */
    private final int code;

    /**
     * 默认提示信息。
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
