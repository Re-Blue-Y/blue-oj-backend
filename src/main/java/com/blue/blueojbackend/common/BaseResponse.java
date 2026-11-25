package com.blue.blueojbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 全局统一响应对象。
 *
 * @param <T> 响应数据类型
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码。
     */
    private int code;

    /**
     * 响应数据。
     */
    private T data;

    /**
     * 响应信息。
     */
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}