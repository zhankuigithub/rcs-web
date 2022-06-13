package com.longmaster.platform.dto.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * author zk
 * date 2021/3/2 11:15
 * description 卡片查询实体类
 */
@Setter
@Getter
public class CardQueryDTO {

    @ApiModelProperty("客户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("应用ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

}
