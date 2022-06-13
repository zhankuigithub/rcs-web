package com.longmaster.platform.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EventStatDTO {

    @ApiModelProperty(value = "应用id")
    private List<Long> appIds;

    @ApiModelProperty(value = "查询类型， 1日 2周 3月 4年")
    private Integer type;

    @ApiModelProperty(value = "开始时间")
    private String beginDt;

    @ApiModelProperty(value = "结束时间")
    private String endDt;

}
