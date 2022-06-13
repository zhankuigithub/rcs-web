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

/**
 * <p>
 * 固定菜单审核记录表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_menu_audit_record")
@ApiModel(value = "MenuAuditRecord对象", description = "固定菜单审核记录表")
public class MenuAuditRecord extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "机器人id")
    @TableField("chatbot_id")
    private Long chatBotId;

    @ApiModelProperty(value = "运营商id")
    @TableField("carrier_id")
    private Long carrierId;

    @ApiModelProperty(value = "菜单组id")
    @TableField("menu_group_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long menuGroupId;

    @ApiModelProperty(value = "审核状态（0待审核 ，1审核通过，2审核不通过）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "审核详情")
    @TableField("review_data")
    private String reviewData;

    @ApiModelProperty(value = "菜单json")
    @TableField("payload")
    private String payload;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
