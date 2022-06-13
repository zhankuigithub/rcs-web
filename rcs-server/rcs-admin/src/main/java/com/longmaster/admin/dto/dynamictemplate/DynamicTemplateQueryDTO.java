package com.longmaster.admin.dto.dynamictemplate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DynamicTemplateQueryDTO {

    @ApiModelProperty(value = "所属应用id")
    private Long appId;

    @ApiModelProperty(value = "模板名称")
    private String name;

    @ApiModelProperty(value = "模板类型（1文本消息，2单卡片，3多卡片，4地理消息 5音频 6视频 7图片）")
    private Integer type;

    @ApiModelProperty("运营商id，前端不需要传")
    private String carrierIds;
}
