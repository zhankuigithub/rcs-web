package com.longmaster.platform.dto.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.longmaster.platform.dto.chatbot.ChatBotDTO;
import com.longmaster.platform.entity.Application;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApplicationDTO extends Application {

    @ApiModelProperty("客户名称")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String customerName;

    @ApiModelProperty("机器人列表")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ChatBotDTO> chatbotItems;

    @ApiModelProperty("是否提交固定菜单")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean menuAuditTag;

}
