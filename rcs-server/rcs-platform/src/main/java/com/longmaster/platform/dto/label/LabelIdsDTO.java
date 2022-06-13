package com.longmaster.platform.dto.label;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class LabelIdsDTO {

    @ApiModelProperty(value = "标签ids")
    private List<String> ids;

}
