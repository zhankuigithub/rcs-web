package com.longmaster.platform.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StatisticalDTO {

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "消息类型")
    private Integer contentType;

}
