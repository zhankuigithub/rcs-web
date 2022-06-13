package com.longmaster.platform.dto.phoneBook;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ModifyBlacklistReqDTO {

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "应用ids")
    private String appBlacklistIds;

}
