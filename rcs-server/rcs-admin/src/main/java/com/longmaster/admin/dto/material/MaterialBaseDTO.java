package com.longmaster.admin.dto.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.admin.dto.BaseDTO;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MaterialBaseDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    public static final int IS_THUMB = 1;
    public static final int IS_NOT_THUMB = 0;

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_VIDEO = 3;

    @ApiModelProperty(value = "客户id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "应用id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "素材名称")
    private String name;

    @ApiModelProperty(value = "素材类型")
    private Integer type;

    @ApiModelProperty(value = "原始地址url")
    private String originalUrl;

    @ApiModelProperty(value = "素材源地址url")
    private String sourceUrl;

    @ApiModelProperty(value = "是否是缩略图 1 是 0 否")
    private Integer isThumb;

    @ApiModelProperty(value = "缩略图id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long thumbId;

    @ApiModelProperty(value = "状态（0正常 1失效）")
    private Integer status;

    @ApiModelProperty(value = "审核备注")
    @NotBlank(message = "备注不允许为空~", groups = {SuperEntity.Update.class})
    private String remark;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @JsonIgnore
    private Integer logicDel;
}
