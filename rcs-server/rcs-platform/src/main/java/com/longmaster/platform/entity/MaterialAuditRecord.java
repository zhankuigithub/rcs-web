package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 素材审核记录表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_material_audit_record")
@ApiModel(value = "MaterialAuditRecord对象", description = "素材审核记录表")
public class MaterialAuditRecord extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "素材id")
    @TableField("material_id")
    private Long materialId;

    @ApiModelProperty(value = "运营商id")
    @TableField("carrier_id")
    private Long carrierId;

    @ApiModelProperty(value = "机器人id")
    @TableField("chatbot_id")
    private String chatBotId;

    @ApiModelProperty(value = "审核状态（0待审，1审核通过，2审核不通过）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "审核详情")
    @TableField("review_data")
    private String reviewData;

    @ApiModelProperty(value = "上传模式")
    @TableField("upload_mode")
    private Integer uploadMode;

    @ApiModelProperty(value = "素材地址")
    @TableField("material_url")
    private String materialUrl;

    @ApiModelProperty(value = "文件类型")
    @TableField("content_type")
    private String contentType;

    @ApiModelProperty(value = "文件名")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty(value = "文件大小")
    @TableField("file_size")
    private Integer fileSize;

    @ApiModelProperty(value = "到期时间")
    @TableField("until")
    private String until;

    @ApiModelProperty(value = "移动的tid标记")
    @TableField("tid")
    private String tid;


}
