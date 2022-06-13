package com.longmaster.platform.dto.customer;

import com.longmaster.platform.entity.Customer;
import com.longmaster.platform.entity.CustomerContract;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 电信认证提交材料
 */
@Data
@ToString
public class CustomerCtAuthDTO {

    @ApiModelProperty("企业信息")
    private Customer customer;

    @ApiModelProperty("联系人信息")
    private CustomerContactsDTO contacts;

    @ApiModelProperty("合同信息")
    private CustomerContract contract;
}
