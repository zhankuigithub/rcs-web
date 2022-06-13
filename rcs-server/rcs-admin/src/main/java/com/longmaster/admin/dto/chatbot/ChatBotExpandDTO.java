package com.longmaster.admin.dto.chatbot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChatBotExpandDTO extends ChatBotBaseDTO {

    @ApiModelProperty("运营商ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long carrierId;

    @ApiModelProperty("运营商名称")
    private String carrierName;

    @ApiModelProperty("客户Id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("应用名称/机器人名称")
    private String appName;

    @ApiModelProperty("行业类型")
    private String categoryIds;

    @ApiModelProperty("运营商id")
    private String carrierIds;

}

