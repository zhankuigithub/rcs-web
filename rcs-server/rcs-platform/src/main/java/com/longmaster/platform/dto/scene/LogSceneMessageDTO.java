package com.longmaster.platform.dto.scene;

import com.longmaster.platform.entity.LogSceneMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogSceneMessageDTO extends LogSceneMessage {

    @ApiModelProperty(value = "渠道名称")
    private String carrierName;

    @ApiModelProperty(value = "应用名称")
    private String appName;
}
