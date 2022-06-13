package com.longmaster.platform.dto.phoneBook;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BatchUpdLabelDTO {

    @ApiModelProperty(value = "号码ids")
    private List<Long> phoneIds;

    @ApiModelProperty(value = "标签ids")
    private List<Long> labelIds;

}
