package com.longmaster.platform.dto.customer;

import com.longmaster.platform.entity.CustomerAuditRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerAuditRecordDTO extends CustomerAuditRecord {

    @ApiModelProperty("运营商名称")
    private  String carrierName;

    @ApiModelProperty("客户名称")
    private  String customerName;
}
