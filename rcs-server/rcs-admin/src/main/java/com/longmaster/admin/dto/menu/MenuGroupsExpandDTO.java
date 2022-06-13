package com.longmaster.admin.dto.menu;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MenuGroupsExpandDTO extends MenuGroupsBaseDTO {

    @ApiModelProperty("审核信息")
    private List<MenuAuditRecordExpandDTO> auditRecords;

    @ApiModelProperty("客户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("运营商id")
    private String carrierIds;

}
