package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_phone_number_app_blacklist")
@ApiModel(value = "PhoneNumberAppBlacklist对象", description = "通讯录应用黑名单表")
public class PhoneNumberAppBlacklist extends CommonEntity {

    @ApiModelProperty(value = "应用id")
    @TableField("application_id")
    private Long applicationId;

    @ApiModelProperty(value = "号码id")
    @TableField("phone_id")
    private Long phoneId;

}
