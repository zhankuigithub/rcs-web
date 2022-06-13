package com.longmaster.admin.dto.menu;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MenuAuditRecordExpandDTO extends MenuAuditRecordBaseDTO {

    @ApiModelProperty("运营商名称ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long carrierId;

    @ApiModelProperty("运营商名称")
    private String carrierName;
}
