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
 * 机器人开发者信息
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_chatbot_developer")
@ApiModel(value="ChatbotDeveloper对象", description="机器人开发者信息")
public class ChatbotDeveloper extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属机器人")
    @TableField("chatbot_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long chatBotId;

    @ApiModelProperty(value = "开发者token")
    @TableField("token")
    private String token;

    @ApiModelProperty(value = "协议类型 1:http，2:https")
    @TableField("protocol")
    private String protocol;

    @ApiModelProperty(value = "回调 URL 地址根目录 以 http:// 开头，IP+端口的形式，用来接收 下行消息状态报告以及消息通知")
    @TableField("notify_url")
    private String notifyUrl;

    @ApiModelProperty(value = "Chatbot 接口是否启用，1:启用， 0:不启用")
    @TableField("enable")
    private Integer enable;

    @ApiModelProperty(value = "开发者id")
    @TableField("app_id")
    private String appId;

    @ApiModelProperty(value = "开发者密码")
    @TableField("app_key")
    private String appKey;

    @ApiModelProperty(value = "状态（0正常 1限制）")
    @TableField("status")
    private Integer status;


    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
