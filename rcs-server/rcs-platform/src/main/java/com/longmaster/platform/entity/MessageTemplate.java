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
 * 消息模板
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_message_template")
@ApiModel(value = "MessageTemplate对象", description = "消息模板")
public class MessageTemplate extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属应用")
    @TableField("app_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "模板名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "模板类型（1文本消息，2单卡片，3多卡片，4地理消息 5音频 6视频 7图片）")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("回落类型 0（不回落） 1（回落h5） 2（回落普通短信） 3（UP1.0）")
    @TableField("back_type")
    private Integer backType;

    // //1文本消息，2单卡片，3多卡片，4地理消息 5图片 6音频 7视频
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_SINGLE_CARD = 2;
    public static final int TYPE_MANY_CARD = 3;
    public static final int TYPE_MANY_LOCATION = 4;
    public static final int TYPE_IMAGE = 5;
    public static final int TYPE_AUDIO = 6;
    public static final int TYPE_VIDEO = 7;

    @ApiModelProperty(value = "消息内容")
    @TableField("payload")
    private String payload;

    @ApiModelProperty(value = "建议回复菜单id （多个id使用逗号隔开）")
    @TableField("sug_ids")
    private String sugIds;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "卡片宽度")
    @TableField("width")
    private Integer width;

    @ApiModelProperty(value = "卡片方向")
    @TableField("orientation")
    private Integer orientation;

    @ApiModelProperty(value = "短信内容")
    @TableField("sms_content")
    private String smsContent;

    @ApiModelProperty(value = "图片位置 1左  2右")
    @TableField("image_alignment")
    private Integer imageAlignment;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
