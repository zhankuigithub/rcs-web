package com.longmaster.platform.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CruxDTO {
    @ApiModelProperty(value = "平均点击数")
    private Double userAverage;

    @ApiModelProperty(value = "平均点击数-变动率")
    private String userAveragePercentage;

    @ApiModelProperty(value = "人数")
    private Integer userCnt;

    @ApiModelProperty(value = "人数-变动率")
    private String userCntPercentage;

    @ApiModelProperty(value = "次数")
    private Integer userTimes;

    @ApiModelProperty(value = "次数-变动率")
    private String userTimesPercentage;
}
