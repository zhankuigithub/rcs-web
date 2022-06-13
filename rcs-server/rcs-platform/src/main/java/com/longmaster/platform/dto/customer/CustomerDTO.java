package com.longmaster.platform.dto.customer;

import com.longmaster.platform.entity.Application;
import com.longmaster.platform.entity.Customer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 客户扩展信息
 */
@Data
public class CustomerDTO extends Customer {

    @ApiModelProperty("运营商列表")
    private List<CustomerAuditRecordDTO> auditRecordList;

    @ApiModelProperty("应用列表")
    private List<Application> appList;

    @ApiModelProperty("客户CSPECNO")
    private String cspEcNo;
}
