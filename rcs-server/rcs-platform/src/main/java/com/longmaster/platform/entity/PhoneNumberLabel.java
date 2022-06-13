package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_phone_number_label")
@ApiModel(value = "PhoneNumberLabel对象", description = "通讯录标签表")
public class PhoneNumberLabel extends CommonEntity {

    @ApiModelProperty(value = "标签id")
    @TableField("label_id")
    private Long labelId;

    @ApiModelProperty(value = "号码id")
    @TableField("phone_id")
    private Long phoneId;

}
