package com.longmaster.platform.dto.group;

import com.longmaster.platform.entity.LogTaskSendEvent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogTaskSendEventDTO  extends LogTaskSendEvent {

    @ApiModelProperty(value = "应用名")
    private String appName;

    @ApiModelProperty(value = "渠道名")
    private String carrierName;

}
