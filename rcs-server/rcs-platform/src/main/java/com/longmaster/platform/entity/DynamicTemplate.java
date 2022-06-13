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
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_dynamic_template")
@ApiModel(value = "DynamicTemplate对象", description = "动态消息模板")
public class DynamicTemplate extends CommonEntity {

    @ApiModelProperty(value = "所属应用id")
    @TableField("app_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "请填写归宿应用ID(appId)", groups = {Create.class, CT.class, CM.class})
    private Long appId;

    @ApiModelProperty(value = "模板名称")
    @TableField("name")
    @NotBlank(message = "请填写模板名称（name）")
    private String name;

    @ApiModelProperty(value = "模板类型（1文本消息，2单卡片，3多卡片，4地理消息 5音频 6视频 7图片）")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "消息内容")
    @TableField("payload")
    @NotBlank(message = "请填写消息内容")
    private String payload;

    @ApiModelProperty(value = "短信内容")
    @TableField("sms_content")
    private String smsContent;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
