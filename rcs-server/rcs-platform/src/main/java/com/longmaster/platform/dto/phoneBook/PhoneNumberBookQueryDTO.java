package com.longmaster.platform.dto.phoneBook;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PhoneNumberBookQueryDTO {

    @ApiModelProperty(value = "客户id")
    private Long customerId;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "手机号")
    private String phoneNum;

    @ApiModelProperty(value = "标签id")
    private Long labelId;

}
