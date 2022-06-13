package com.longmaster.admin.dto.menu;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MenuItemBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属应用")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "父级菜单id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long pid;

    @ApiModelProperty(value = "菜单项名称")
    private String name;

    @ApiModelProperty(value = "菜单项类型（0目录菜单，1建议回复，2建议操作）")
    private Integer type;

    @ApiModelProperty(value = "排序权重")
    private Integer weight;

    @ApiModelProperty(value = "执行荷载")
    private String payload;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;

}
