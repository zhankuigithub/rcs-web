package com.longmaster.platform.dto.dynamicTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class InteractionDynamicTemplateBodyDTO {

    @ApiModelProperty(value = "动态模板id")
    @NotNull(message = "请填写动态模板id")
    private Long templateId;

    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "请填写手机号")
    private String phone;

    @ApiModelProperty(value = "消息主体的动态参数，特别说明：当需要复制多张卡片时，此参数类型为list，并将每一个卡片以json拼装起来")
    private JsonNode body;

    @ApiModelProperty(value = "悬浮菜单的动态参数，特别说明：当悬浮按钮类型确定但是个数不固定时，需要通过传递数组的形式来实现，且在配置时只能配置一个")
    private JsonNode suggestion;

    @ApiModelProperty(value = "是否在chatbotH5回落中对话")
    private boolean isMessageBack = false;

    @ApiModelProperty(value = "chatBotId")
    private String chatBotId;

    @ApiModelProperty(value = "conversationId")
    private String conversationId;

    @ApiModelProperty(value = "contributionId")
    private String contributionId;

}
