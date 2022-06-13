package com.longmaster.platform.dto.dynamicTemplate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class KeysBodyDTO {

    @ApiModelProperty("消息主体的参数列表")
    private List<String> bodyKeys;

    @ApiModelProperty("悬浮菜单的参数列表")
    private List<String> suggestionKeys;
}
