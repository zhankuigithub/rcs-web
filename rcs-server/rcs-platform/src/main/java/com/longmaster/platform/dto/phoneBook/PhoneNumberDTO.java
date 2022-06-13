package com.longmaster.platform.dto.phoneBook;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PhoneNumberDTO {

    @ApiModelProperty(value = "客户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "客户")
    private String customerName;

    @ApiModelProperty(value = "通讯录号码总数")
    private int cnt;

    @ApiModelProperty(value = "移动号码数")
    private int cntCm;

    @ApiModelProperty(value = "联通号码数")
    private int cntCu;

    @ApiModelProperty(value = "电信号码数")
    private int cntCt;
}
