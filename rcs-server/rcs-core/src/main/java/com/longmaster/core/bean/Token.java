package com.longmaster.core.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class Token {
    //创建时间
    @ApiModelProperty(value = "创建时间")
    private Long timestamp;
    //token 令牌
    @ApiModelProperty("访问令牌")
    private String accessToken;
    //刷新token
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "刷新令牌")
    private String refreshToken;
    //有效时长
    @ApiModelProperty(value = "有限时长")
    private Long expire = 60L;
}
