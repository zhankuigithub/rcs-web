package com.longmaster.platform.dto.keyword;

import com.longmaster.platform.entity.KeywordReplyConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author zk
 * date 2021/3/1 18:33
 * description 关键词配置返回的实体
 */
@Setter
@Getter
@ToString
public class KeywordReplyConfigDTO extends KeywordReplyConfig {

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("用户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("消息模板名称")
    private String messageName;

    @ApiModelProperty("消息模板类型")
    private Integer messageType;

}
