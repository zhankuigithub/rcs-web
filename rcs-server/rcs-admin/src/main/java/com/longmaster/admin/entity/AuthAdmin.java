package com.longmaster.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * 管理员账户信息
 * </p>
 *
 * @author dengshuihong
 * @since 2021-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_auth_admin")
@ApiModel(value="AuthAdmin对象", description="管理员账户信息")
public class AuthAdmin extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "递曾唯一ID、数据主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "登录账号")
    @TableField("account")
    private String account;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "管理员联系方式，手机号")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "管理员姓名")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "头像地址")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "数据状态，0.正常、1.异常 ")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField("insert_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime insertDt;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateDt;

    @ApiModelProperty(value = "逻辑删除： 1. 真、 0.假 ")
    @TableField("logic_del")
    private Integer logicDel;


}
