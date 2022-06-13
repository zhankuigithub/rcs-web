package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_contact_information")
@ApiModel(value = "ContactInformation对象", description = "")
public class ContactInformation extends CommonEntity {

    @ApiModelProperty(value = "姓名")
    @TableField("name")
    @NotBlank(message = "请填写姓名")
    private String name;

    @ApiModelProperty(value = "手机号")
    @TableField("phone_num")
    @NotBlank(message = "请填写手机号")
    private String phoneNum;

    @ApiModelProperty(value = "公司")
    @TableField("company")
    @NotBlank(message = "请填写公司")
    private String company;

    @ApiModelProperty(value = "更多")
    @TableField("remark")
    private String remark;

}
