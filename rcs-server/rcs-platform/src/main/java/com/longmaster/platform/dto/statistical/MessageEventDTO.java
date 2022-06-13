package com.longmaster.platform.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MessageEventDTO {

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "模板名称")
    private String messageTemplateName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "开始时间")
    private String beginDt;

    @ApiModelProperty(value = "结束时间")
    private String endDt;
}
