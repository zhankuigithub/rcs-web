package com.longmaster.admin.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendEventDTO {

    @ApiModelProperty(value = "消息id")
    private String messageId;

    @ApiModelProperty(value = "任务名")
    private String name;

    @ApiModelProperty(value = "机器人id")
    private String chatBotId;

    @ApiModelProperty(value = "手机号list")
    private String[] phoneNums;

    @ApiModelProperty(value = "渠道商")
    private Long carrierId;

    @ApiModelProperty(value = "消息模板id")
    private Long messageTemplateId;

}
