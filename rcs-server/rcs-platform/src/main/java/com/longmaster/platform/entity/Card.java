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
 * 卡片信息表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_card")
@ApiModel(value = "Card对象", description = "卡片信息表")
public class Card extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属客户")
    @TableField("customer_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "所属应用")
    @TableField("app_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "卡片名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "卡片标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "文本内容")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "按钮列表（多个id使用逗号隔开）")
    @TableField("sug_ids")
    private String sugIds;

    @ApiModelProperty(value = "素材id")
    @TableField("material_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long materialId;

    @ApiModelProperty(value = "封面id")
    @TableField("thumb_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long thumbId;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "状态（SHORT_HEIGHT（1 小）   MEDIUM_HEIGHT （2 中）   TALL_HEIGHT（3 大））")
    @TableField("height")
    private Integer height;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;
}
