package com.longmaster.platform.dto.dynamicTemplate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.longmaster.platform.entity.DynamicTemplate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DynamicTemplateDTO extends DynamicTemplate {

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("运营商素材审核")
    private List<Map<String, Object>> auditRecords;

    @ApiModelProperty("客户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;
}
