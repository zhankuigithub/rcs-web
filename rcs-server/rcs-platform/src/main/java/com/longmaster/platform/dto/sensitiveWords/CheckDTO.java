package com.longmaster.platform.dto.sensitiveWords;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckDTO {

    @ApiModelProperty(value = "需要检查的文本")
    private String content;

}
