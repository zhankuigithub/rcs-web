package com.longmaster.platform.dto.csp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CallbackCustomerDTO extends EnterpriseAuthWithCtDTO {

    //通知类型，1：新增，2：变更 3：删除
    @ApiModelProperty("通知类型，1：新增，2：变更，3：删除")
    private Integer type;
}
