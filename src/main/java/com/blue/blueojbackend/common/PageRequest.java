package com.blue.blueojbackend.common;

import com.blue.blueojbackend.constant.CommonConstant;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求。
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Min(value = 1, message = "当前页必须大于等于 1")
    private long current = 1;

    @Min(value = 1, message = "每页数量必须大于等于 1")
    @Max(value = 100, message = "每页数量不能超过 100")
    private long pageSize = 10;

    private String sortField;

    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
