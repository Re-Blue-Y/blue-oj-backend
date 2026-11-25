package com.blue.blueojbackend.common;

/**
 * 统一响应构造工具。
 */
public final class ResultUtils {

    private ResultUtils() {
    }

    /**
     * 返回成功结果。
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(
                ErrorCode.SUCCESS.getCode(),
                data,
                ErrorCode.SUCCESS.getMessage()
        );
    }

    /**
     * 根据错误码返回失败结果。
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(
                errorCode.getCode(),
                null,
                errorCode.getMessage()
        );
    }

    /**
     * 使用指定业务状态码和提示返回失败结果。
     */
    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 保留错误码，但覆盖默认提示。
     */
    public static <T> BaseResponse<T> error(
            ErrorCode errorCode,
            String message
    ) {
        return new BaseResponse<>(
                errorCode.getCode(),
                null,
                message
        );
    }
}