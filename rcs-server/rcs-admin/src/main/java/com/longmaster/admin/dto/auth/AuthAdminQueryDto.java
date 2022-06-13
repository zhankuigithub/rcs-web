package com.longmaster.admin.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * author zk
 * date 2021/4/12 16:51
 * description TODO
 */
@Setter
@Getter
public class AuthAdminQueryDto {

    @ApiModelProperty("登录账号")
    private String account;

    @ApiModelProperty(value = "管理员姓名")
    private String name;

    @ApiModelProperty(value = "数据状态，0.正常、1.异常 ")
    private Integer status;

    @ApiModelProperty(value = "管理员联系方式，手机号")
    private String phone;
}
