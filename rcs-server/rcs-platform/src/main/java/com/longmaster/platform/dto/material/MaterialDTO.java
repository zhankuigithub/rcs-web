package com.longmaster.platform.dto.material;

import com.longmaster.platform.entity.Material;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 素材信息
 */
@Data
public class MaterialDTO extends Material {

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("截图地址")
    private String thumbnailUrl;

    @ApiModelProperty("运营商素材审核")
    private ArrayNode auditRecords;
}
