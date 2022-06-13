package com.longmaster.platform.dto.label;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LabelQueryDTO {

    @ApiModelProperty(value = "客户id")
    private Long customerId;

    @ApiModelProperty(value = "标签名称")
    private String name;

}
