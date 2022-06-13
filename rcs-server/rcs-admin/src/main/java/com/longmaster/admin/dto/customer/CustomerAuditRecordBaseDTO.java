package com.longmaster.admin.dto.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerAuditRecordBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "运营商id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long carrierId;

    @ApiModelProperty(value = "NCSP客户识别码")
    private String cspEcNo;

    @ApiModelProperty(value = "客户状态（0待审核 1审核通过 2审核未通过）")
    private Integer status;

    @ApiModelProperty(value = "审核内容")
    private String reviewData;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;

}
