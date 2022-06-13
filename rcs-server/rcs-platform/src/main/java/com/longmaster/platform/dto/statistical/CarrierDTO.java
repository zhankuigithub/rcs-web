package com.longmaster.platform.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CarrierDTO {

    @ApiModelProperty(value = "渠道id")
    private Long carrierId;

    @ApiModelProperty(value = "渠道名")
    private String carrierName;

    @ApiModelProperty(value = "点击次数")
    private Integer userTimes;
}
