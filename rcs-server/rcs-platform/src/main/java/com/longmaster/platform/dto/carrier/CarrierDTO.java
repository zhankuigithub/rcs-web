package com.longmaster.platform.dto.carrier;

import com.longmaster.platform.entity.Carrier;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CarrierDTO extends Carrier {

    @ApiModelProperty("机器人审核状态")
    private Integer auditStatus;
}
