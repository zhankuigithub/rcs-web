package com.longmaster.platform.dto.menu;

import com.longmaster.platform.entity.MenuGroups;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MenuGroupsDTO extends MenuGroups {

    @ApiModelProperty("审核信息")
    private List<MenuAuditRecordDTO> auditRecords;

    @ApiModelProperty("客户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("应用名称")
    private String appName;
}
