package com.longmaster.platform.dto.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * author zk
 * date 2021/3/2 13:38
 * description 素材查询
 */
@Setter
@Getter
public class MaterialQueryDTO {

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_VIDEO = 3;

    @ApiModelProperty("应用id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty("客户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("图片名称")
    private String name;

    @ApiModelProperty("素材类型（1图片  2 音频 3视频）")
    private Integer type;

    @ApiModelProperty("素材类型（1是  0不是）")
    private Integer isThumb;
}
