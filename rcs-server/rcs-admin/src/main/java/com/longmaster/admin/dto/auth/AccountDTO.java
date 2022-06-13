package com.longmaster.admin.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AccountDTO {

    @ApiModelProperty(value = "账号")
    @NotNull(message = "请输入手机号或者邮箱")
    private String account;

}
