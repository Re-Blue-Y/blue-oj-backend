package com.blue.blueojbackend.exception;

import com.blue.blueojbackend.common.BaseResponse;
import com.blue.blueojbackend.common.ErrorCode;
import com.blue.blueojbackend.common.ResultUtils;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理主动抛出的业务异常。
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<?>> handleBusinessException(
            BusinessException exception
    ) {
        log.warn(
                "BusinessException: code={}, message={}",
                exception.getCode(),
                exception.getMessage()
        );

        return response(
                httpStatus(exception.getCode()),
                ResultUtils.error(exception.getCode(), exception.getMessage())
        );
    }

    /**
     * 处理使用 @Valid 校验请求体失败的异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError ->
                        fieldError.getField()
                                + ": "
                                + fieldError.getDefaultMessage()
                )
                .orElse(ErrorCode.PARAMS_ERROR.getMessage());

        return response(
                HttpStatus.BAD_REQUEST,
                ResultUtils.error(ErrorCode.PARAMS_ERROR, message)
        );
    }

    /**
     * 处理表单或查询对象绑定校验失败。
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<BaseResponse<?>> handleBindException(BindException exception) {
        String message = exception
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .orElse(ErrorCode.PARAMS_ERROR.getMessage());
        return response(HttpStatus.BAD_REQUEST, ResultUtils.error(ErrorCode.PARAMS_ERROR, message));
    }

    /**
     * 处理请求参数约束校验异常。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<?>> handleConstraintViolationException(
            ConstraintViolationException exception
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                ResultUtils.error(ErrorCode.PARAMS_ERROR, exception.getMessage())
        );
    }

    /**
     * 处理 Spring MVC 方法参数校验失败。
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BaseResponse<?>> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception
    ) {
        return response(HttpStatus.BAD_REQUEST, ResultUtils.error(ErrorCode.PARAMS_ERROR));
    }

    /**
     * 处理请求体 JSON 格式错误。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<?>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                ResultUtils.error(ErrorCode.PARAMS_ERROR, "请求体格式错误")
        );
    }

    /**
     * 统一处理不存在的接口或静态资源。
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<?>> handleNoResourceFoundException(
            NoResourceFoundException exception
    ) {
        return response(HttpStatus.NOT_FOUND, ResultUtils.error(ErrorCode.NOT_FOUND_ERROR));
    }

    /**
     * 兜底处理未预期的运行时异常。
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<?>> handleRuntimeException(
            RuntimeException exception
    ) {
        log.error("RuntimeException", exception);

        return response(HttpStatus.INTERNAL_SERVER_ERROR, ResultUtils.error(ErrorCode.SYSTEM_ERROR));
    }

    private HttpStatus httpStatus(int code) {
        if (code == ErrorCode.NOT_LOGIN_ERROR.getCode()) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (code == ErrorCode.NO_AUTH_ERROR.getCode() || code == ErrorCode.FORBIDDEN_ERROR.getCode()) {
            return HttpStatus.FORBIDDEN;
        }
        if (code == ErrorCode.NOT_FOUND_ERROR.getCode()) {
            return HttpStatus.NOT_FOUND;
        }
        if (code >= 50000) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private ResponseEntity<BaseResponse<?>> response(HttpStatus status, BaseResponse<?> body) {
        return ResponseEntity.status(status).body(body);
    }
}
