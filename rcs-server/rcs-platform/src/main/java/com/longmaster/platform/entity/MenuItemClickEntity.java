package com.longmaster.platform.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MenuItemClickEntity {

    @ApiModelProperty(value = "菜单id")
    private Long menuId;

    @ApiModelProperty(value = "点击次数")
    private Integer clickCnt;

    @ApiModelProperty(value = "点击人数")
    private Integer clickUsers;

    @ApiModelProperty(value = "平均点击数")
    private Double clickAverage;
}
