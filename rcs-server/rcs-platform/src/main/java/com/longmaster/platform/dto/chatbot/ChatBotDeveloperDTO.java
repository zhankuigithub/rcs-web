package com.longmaster.platform.dto.chatbot;

import com.longmaster.platform.entity.ChatbotDeveloper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChatBotDeveloperDTO extends ChatbotDeveloper {

    @ApiModelProperty("机器人编码")
    private String accessTagNo;
}
