package com.longmaster.platform.dto.receive;

import com.longmaster.platform.entity.LogReceiveMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogReceiveMessageDTO extends LogReceiveMessage {

    @ApiModelProperty(value = "应用名")
    private String appName;

    @ApiModelProperty(value = "渠道名")
    private String carrierName;

}
