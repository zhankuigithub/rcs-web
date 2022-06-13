package com.longmaster.admin.dto.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerAuditRecordExpandDTO extends CustomerAuditRecordBaseDTO {

    @ApiModelProperty("运营商名称")
    private  String carrierName;

    @ApiModelProperty("客户名称")
    private  String customerName;

    @ApiModelProperty("运营商id")
    private String carrierIds;

}
