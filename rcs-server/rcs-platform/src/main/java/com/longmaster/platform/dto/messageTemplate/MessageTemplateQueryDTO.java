package com.longmaster.platform.dto.messageTemplate;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * author zk
 * date 2021/3/2 11:41
 * description 消息模板查询 实体
 */
@Setter
@Getter
public class MessageTemplateQueryDTO {

    @ApiModelProperty(value = "模板id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(value = "模板名称")
    private String name;

    @ApiModelProperty("应用ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty("客户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "模板类型")
    private Integer type;
}
