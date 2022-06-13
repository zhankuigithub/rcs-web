package com.longmaster.admin.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthDto {

    @ApiModelProperty(value = "账户")
    @NotBlank(message = "账号不允许为空")
    private String  account;

    @ApiModelProperty(value = "密码，aes加密")
    @NotBlank(message = "密码不允许为空")
    private String password;

    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "验证码不允许为空")
    private String code;
}
