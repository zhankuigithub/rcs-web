package com.longmaster.platform.dto.csp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CallbackAuditDTO {

    @ApiModelProperty(value = "审核识别码,type 为 1 时：客户识别码识别码为客户新增时返回值 data 内的值），type 为 2 时：chatbot 识别码chatbot 新增时返回值 data 内的值）")
    private String  auditNo;

    @ApiModelProperty(value = "客户审核状态（1.审核通过，2.审核不通过")
    private Integer  auditStatus;

    @ApiModelProperty(value = "审核类型 1：客户，2：chatbot")
    private Integer  type;

    @ApiModelProperty(value = "操作类型，1：新增，2：变更，3：删除，4：上线（机器人cucc）")
    private Integer  opType;

    @ApiModelProperty(value = "审核不通过描述，当审核不通过时，字段有效")
    private String  description;

}
