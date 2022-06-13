package com.longmaster.platform.dto.customer;

import com.longmaster.platform.entity.CustomerContacts;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 联系人扩展信息
 */
@Data
public class CustomerContactsDTO extends CustomerContacts {

    @ApiModelProperty("身份证证件照地址")
    private String[] cardUrl;
}
