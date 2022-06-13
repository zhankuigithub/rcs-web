package com.longmaster.admin.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthSmsDto {
    @ApiModelProperty(value = "账户")
    @NotBlank(message = "账号不允许为空")
    private String account;

    @ApiModelProperty(value = "图型验证码")
    private String code;

    @ApiModelProperty(value = "短信验证码")
    private String smsCode;
}
