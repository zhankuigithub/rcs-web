package com.longmaster.admin.dto.application;

import com.longmaster.admin.dto.chatbot.ChatBotExpandDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApplicationQueryBaseDTO extends ApplicationBaseDTO {

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("机器人列表")
    private List<ChatBotExpandDTO> chatbotItems;

    @ApiModelProperty("是否提交固定菜单")
    private boolean menuAuditTag;

    @ApiModelProperty("运营商id")
    private String carrierIds;
}
