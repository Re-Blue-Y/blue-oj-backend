package com.blue.blueojbackend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
public class UserLoginRequest {
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 4, max = 32, message = "用户账号长度只能在4~32位")
    private String userAccount;

    @NotBlank(message = "用户密码不能为空")
    @Size(min = 8, max = 64, message = "用户密码长度只能在8~64位")
    @ToString.Exclude
    private String userPassword;
}
