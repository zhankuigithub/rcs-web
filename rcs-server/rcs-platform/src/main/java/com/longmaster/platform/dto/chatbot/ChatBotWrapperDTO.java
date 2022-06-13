package com.longmaster.platform.dto.chatbot;

import com.longmaster.platform.entity.Application;
import com.longmaster.platform.entity.Chatbot;
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
    private Chatbot chatbot;

    @Valid
    @ApiModelProperty("应用信息")
    private Application application;
}
