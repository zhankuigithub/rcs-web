package com.longmaster.admin.dto.chatbot;

import com.longmaster.admin.dto.application.ApplicationBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;

/**
 * 机器人
 */
@Data
public class ChatBotWrapperDTO {

    @Valid
    @ApiModelProperty("机器人信息")
    private ChatBotBaseDTO chatbot;

    @Valid
    @ApiModelProperty("应用信息")
    private ApplicationBaseDTO application;
}
