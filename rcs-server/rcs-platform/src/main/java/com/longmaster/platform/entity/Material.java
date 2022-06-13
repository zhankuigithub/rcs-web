package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 素材信息表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_material")
@ApiModel(value = "Material对象", description = "素材信息表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Material extends CommonEntity {

    private static final long serialVersionUID = 1L;

    public static final int IS_THUMB = 1;
    public static final int IS_NOT_THUMB = 0;

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_ARTICLE = 4;
    public static final int TYPE_URL = 5;

    @ApiModelProperty(value = "客户id")
    @TableField("customer_id")
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "应用id")
    @TableField("app_id")
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "素材名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "素材类型")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "原始地址url")
    @TableField("original_url")
    private String originalUrl;

    @ApiModelProperty(value = "素材源地址url")
    @TableField("source_url")
    private String sourceUrl;

    @ApiModelProperty(value = "是否是缩略图 1 是 0 否")
    @TableField("is_thumb")
    private Integer isThumb;

    @ApiModelProperty(value = "缩略图id")
    @TableField("thumb_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long thumbId;

    @ApiModelProperty(value = "html详情")
    @TableField("content")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String content;

    @ApiModelProperty(value = "状态（0 未审核 1 审核通过 2 审核未通过）")
    @TableField("status")
    @Min(value = 1, message = "状态不正确", groups = {Update.class})
    @Max(value = 2, message = "状态不正确", groups = {Update.class})
    private Integer status;

    @ApiModelProperty(value = "审核备注")
    @TableField("remark")
    @NotBlank(message = "备注不允许为空~", groups = {Update.class})
    private String remark;

    @ApiModelProperty(value = "归属 (1 csp后台 2 chatbot云平台)")
    @TableField("attribution")
    private Integer attribution;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
