package com.longmaster.platform.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SataDTO {

    @ApiModelProperty(value = "关键数据")
    private CruxDTO crux;

    @ApiModelProperty(value = "数据趋势")
    private List<TrendDTO> trends;

    @ApiModelProperty(value = "渠道构成")
    private List<CarrierDTO> carriers;

}


