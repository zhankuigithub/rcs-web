package com.longmaster.platform.dto.menu;

import com.longmaster.platform.entity.MenuAuditRecord;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MenuAuditRecordDTO extends MenuAuditRecord {

    @ApiModelProperty("运营商名称ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long carrierId;

    @ApiModelProperty("运营商名称")
    private String carrierName;
}
