package com.longmaster.platform.dto.scene;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * author zk
 * date 2021/5/10 17:48
 * description 变更场景节点
 */
@Data
public class SceneNodeModifyDTO {

    @ApiModelProperty(value = "场景id，有则传")
    private String id;

    @ApiModelProperty(value = "场景名")
    private String name;

    @ApiModelProperty(value = "应用id")
    private String appId;

    @ApiModelProperty(value = "所有节点")
    private List<JsonNode> nodes;

    @ApiModelProperty(value = "节点的关系")
    private List<JsonNode> edges;

}
