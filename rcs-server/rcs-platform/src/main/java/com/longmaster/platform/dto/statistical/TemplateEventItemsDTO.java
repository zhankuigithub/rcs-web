package com.longmaster.platform.dto.statistical;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateEventItemsDTO {

    @ApiModelProperty(value = "消息模板名称")
    private String messageTemplateName;

    @ApiModelProperty(value = "消息模板id")
    private Long messageTemplateId;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "应用名")
    private String appName;

    @ApiModelProperty(value = "渠道商id")
    private Long carrierId;

    @ApiModelProperty(value = "渠道商名称")
    private String carrierName;

    @ApiModelProperty(value = "点击总次数")
    private Integer clickCnt;

    @ApiModelProperty(value = "菜单点击总次数")
    private Integer menuClickCnt;

    @ApiModelProperty(value = "按钮点击总次数")
    private Integer sugClickCnt;

    @ApiModelProperty(value = "最近点击时间")
    private String currentDt;

}
