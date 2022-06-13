package com.longmaster.admin.dto.customer;

import com.longmaster.admin.dto.application.ApplicationBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 客户扩展信息
 */
@Data
public class CustomerExpandDTO extends CustomerBaseDTO {

    @ApiModelProperty("运营商列表")
    private List<CustomerAuditRecordExpandDTO> auditRecordList;

    @ApiModelProperty("应用列表")
    private List<ApplicationBaseDTO> appList;

    @ApiModelProperty("客户CSPECNO")
    private String cspEcNo;

    @ApiModelProperty("运营商id")
    private String carrierIds;
}
