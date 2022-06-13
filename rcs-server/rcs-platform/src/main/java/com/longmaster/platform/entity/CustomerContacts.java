package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 客户联系人表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_customer_contacts")
@ApiModel(value="CustomerContacts对象", description="客户联系人表")
public class CustomerContacts extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户id")
    @TableField("customer_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "联系人名称")
    @TableField("name")
    @NotBlank(message = "请填写联系人名称", groups = {Create.class, CT.class})
    private String name;

    @ApiModelProperty(value = "身份证号码")
    @TableField("id_card_no")
    private String idCardNo;

    @ApiModelProperty(value = "联系人电话")
    @TableField("phone_num")
    @NotBlank(message = "请填写联系人电话", groups = {CT.class})
    private String phoneNum;

    @ApiModelProperty(value = "联系人邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty(value = "身份证文件地址")
    @TableField("id_card_url")
    private String idCardUrl;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
