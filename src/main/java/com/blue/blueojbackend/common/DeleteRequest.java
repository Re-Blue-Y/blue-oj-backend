package com.blue.blueojbackend.common;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求。
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "id 不能为空")
    @Positive(message = "id 必须大于 0")
    private Long id;
}
