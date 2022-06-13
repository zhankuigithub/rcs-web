package com.longmaster.platform.dto.customer;

import com.longmaster.platform.entity.CustomerAuditRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CertificationDTO {

    @ApiModelProperty(value = "客户id")
    public Long customerId;

    @ApiModelProperty(value = "客户认证状态")
    private Integer status;

    @ApiModelProperty(value = "审核内容")
    private String auditContent;

    @ApiModelProperty(value = "认证记录")
    private List<CustomerAuditRecord> customerAuditRecords;

}
