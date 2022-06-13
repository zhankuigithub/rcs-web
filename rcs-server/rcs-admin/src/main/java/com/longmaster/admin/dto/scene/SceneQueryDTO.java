package com.longmaster.admin.dto.scene;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * author zk
 * date 2021/5/11 14:32
 * description TODO
 */
@Data
public class SceneQueryDTO {

    @ApiModelProperty(value = "所属应用id")
    private Long appId;

    @ApiModelProperty(value = "场景名称")
    private String name;

}
