package com.longmaster.platform.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TrendDTO {

    @ApiModelProperty(value = "日期")
    private String statDt;
    @ApiModelProperty(value = "平均点击数")
    private Double userAverage;
    @ApiModelProperty(value = "人数")
    private Integer userCnt;
    @ApiModelProperty(value = "次数")
    private Integer userTimes;

}
