package com.longmaster.admin.dto.menu;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MenuAuditRecordBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "机器人id")
    private Long chatBotId;

    @ApiModelProperty(value = "运营商id")
    private Long carrierId;

    @ApiModelProperty(value = "菜单组id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long menuGroupId;

    @ApiModelProperty(value = "审核状态（0待审核 ，1审核通过，2审核不通过）")
    private Integer status;

    @ApiModelProperty(value = "审核详情")
    private String reviewData;

    @ApiModelProperty(value = "菜单json")
    private String payload;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;

}
