package com.longmaster.admin.dto.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 电信认证提交材料
 */
@Data
@ToString
public class CustomerCtAuthWrapperDTO {

    @ApiModelProperty("企业信息")
    private CustomerBaseDTO customer;

    @ApiModelProperty("联系人信息")
    private CustomerContactsExpandDTO contacts;

    @ApiModelProperty("合同信息")
    private CustomerContractBaseDTO contract;
}
