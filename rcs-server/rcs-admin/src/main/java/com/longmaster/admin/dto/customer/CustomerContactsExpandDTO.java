package com.longmaster.admin.dto.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 联系人扩展信息
 */
@Data
public class CustomerContactsExpandDTO extends CustomerContactsBaseDTO {

    @ApiModelProperty("身份证证件照地址")
    private String[] cardUrl;
}
