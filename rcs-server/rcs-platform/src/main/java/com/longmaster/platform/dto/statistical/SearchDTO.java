package com.longmaster.platform.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchDTO {

    @ApiModelProperty(value = "应用id")
    private List<Long> appIds;

    @ApiModelProperty(value = "渠道id")
    private Long carrierId;

    @ApiModelProperty(value = "开始时间")
    private String beginDt;

    @ApiModelProperty(value = "结束时间")
    private String endDt;

    @ApiModelProperty(value = "任务名称")
    private String name;
}
