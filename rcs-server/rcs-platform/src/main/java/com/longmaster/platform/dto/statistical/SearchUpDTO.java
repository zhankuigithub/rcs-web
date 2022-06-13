package com.longmaster.platform.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchUpDTO {

    @ApiModelProperty(value = "应用id")
    private List<Long> appIds;

    @ApiModelProperty(value = "渠道id")
    private Long carrierId;

    @ApiModelProperty(value = "开始时间")
    private String beginDt;

    @ApiModelProperty(value = "结束时间")
    private String endDt;

    @ApiModelProperty(value = "手机号")
    private String phone;
}
