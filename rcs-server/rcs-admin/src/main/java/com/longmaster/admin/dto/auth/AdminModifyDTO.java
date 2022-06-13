package com.longmaster.admin.dto.auth;

import com.longmaster.core.util.AESUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminModifyDTO {

    @ApiModelProperty(value = "管理员id")
    @NotNull(message = "请输入管理员id")
    private Long id;

    @ApiModelProperty(value = "原管理员密码，aes加密")
    @NotNull(message = "请输入原管理员密码")
    private String oldPassword;

    @ApiModelProperty(value = "新管理员密码，aes加密")
    @NotNull(message = "请输入新管理员密码")
    private String password;

    @ApiModelProperty(value = "新管理员密码确认，aes加密")
    @NotNull(message = "请再次输入新管理员密码")
    private String passwordAgain;

    public String getOldPassword() {
        return AESUtil.desEncrypt(oldPassword);
    }

    public String getPassword() {
        return AESUtil.desEncrypt(password);
    }

    public String getPasswordAgain() {
        return AESUtil.desEncrypt(passwordAgain);
    }
}
