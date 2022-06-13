package com.longmaster.admin.dto.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CardBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属客户")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "所属应用")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "卡片名称")
    private String name;

    @ApiModelProperty(value = "卡片标题")
    private String title;

    @ApiModelProperty(value = "文本内容")
    private String description;

    @ApiModelProperty(value = "按钮列表（多个id使用逗号隔开）")
    private String sugIds;

    @ApiModelProperty(value = "素材id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long materialId;

    @ApiModelProperty(value = "封面id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long thumbId;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    private Integer status;

    @ApiModelProperty(value = "状态（SHORT_HEIGHT（1 小）   MEDIUM_HEIGHT （2 中）   TALL_HEIGHT（3 大））")
    private Integer height;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;

}
