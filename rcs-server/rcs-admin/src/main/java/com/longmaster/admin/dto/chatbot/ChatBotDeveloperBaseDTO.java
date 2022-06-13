package com.longmaster.admin.dto.chatbot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChatBotDeveloperBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属机器人")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long chatBotId;

    @ApiModelProperty(value = "开发者token")
    private String token;

    @ApiModelProperty(value = "协议类型 1:http，2:https")
    private String protocol;

    @ApiModelProperty(value = "回调 URL 地址根目录 以 http:// 开头，IP+端口的形式，用来接收 下行消息状态报告以及消息通知")
    private String notifyUrl;

    @ApiModelProperty(value = "Chatbot 接口是否启用，1:启用， 0:不启用")
    private Integer enable;

    @ApiModelProperty(value = "开发者id")
    private String appId;

    @ApiModelProperty(value = "开发者密码")
    private String appKey;

    @ApiModelProperty(value = "状态（0正常 1限制）")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;

}
