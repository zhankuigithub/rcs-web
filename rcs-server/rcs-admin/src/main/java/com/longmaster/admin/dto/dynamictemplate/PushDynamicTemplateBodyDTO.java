package com.longmaster.admin.dto.dynamictemplate;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PushDynamicTemplateBodyDTO {

    @ApiModelProperty(value = "动态模板id")
    @NotNull(message = "请填写动态模板id")
    private Long templateId;

    @ApiModelProperty(value = "手机号")
    @NotNull(message = "请填写手机号")
    private List<String> phones;

    @ApiModelProperty(value = "消息主体的动态参数，特别说明：当需要复制多张卡片时，此参数类型为list，并将每一个卡片以json拼装起来")
    private JsonNode body;

    @ApiModelProperty(value = "悬浮菜单的动态参数")
    private JsonNode suggestion;

}
