package com.longmaster.admin.dto.auth;

import com.longmaster.admin.entity.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ResetModifyDTO {

    @ApiModelProperty(value = "账号")
    @Pattern(regexp = CommonEntity.PHONE_REG, message = "请输入正确的手机号")
    private String account;

    @ApiModelProperty(value = "验证码")
    @NotNull(message = "请输入短信验证码")
    private String code;

    @ApiModelProperty(value = "密码，aes加密")
    @NotNull(message = "请输入新管理员密码")
    private String password;

}
