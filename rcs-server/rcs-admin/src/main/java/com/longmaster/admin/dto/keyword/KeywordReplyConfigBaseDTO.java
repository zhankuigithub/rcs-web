package com.longmaster.admin.dto.keyword;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class KeywordReplyConfigBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属应用")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "配置名称")
    private String name;

    @ApiModelProperty(value = "匹配类型（1完全 2包含 3正则表达式）")
    private Integer type;

    @ApiModelProperty(value = "权重（越大越靠前）")
    private Integer weight;

    @ApiModelProperty(value = "匹配规则内容")
    private String ruleContent;

    @ApiModelProperty(value = "回复id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long replyId;

    @ApiModelProperty(value = "状态（0正常，1停用）")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;
}
