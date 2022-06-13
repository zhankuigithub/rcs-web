package com.longmaster.platform.dto.keyword;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * author zk
 * date 2021/3/2 11:36
 * description 关键词查询 实体
 */
@Setter
@Getter
public class KeywordReplyConfigQueryDTO {

    @ApiModelProperty(value = "规则名称")
    private String name;

    @ApiModelProperty("应用ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty("客户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

}
