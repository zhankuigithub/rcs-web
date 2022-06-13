package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 机器人信息表
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_chatbot")
@ApiModel(value = "Chatbot对象", description = "机器人信息表")
public class Chatbot extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属应用id")
    @TableField("app_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "请填写归宿应用ID(appId)", groups = {Create.class, CT.class, CM.class})
    private Long appId;

    @ApiModelProperty(value = "运营商id")
    @TableField("carrier_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "请填写归宿运营商ID(carrierId)", groups = {Create.class, CT.class, CM.class})
    private Long carrierId;

    @ApiModelProperty(value = "CSP编码")
    @TableField("csp_id")
    private String cspId;

    @ApiModelProperty(value = "机器人编码")
    @TableField("access_tag_no")
    @NotBlank(message = "参数机器人编码不允许为空(accessTagNo)", groups = {Update.class})
    private String accessTagNo;

    @ApiModelProperty(value = "NCSP客户识别码")
    @TableField("csp_ec_no")
    private String cspEcNo;

    @ApiModelProperty(value = "接入号码+自定义号码")
    @TableField("chatbot_id")
    @NotBlank(message = "请填写机器人编码（chatBotId）",  groups = {Create.class, CT.class, CM.class})
    private String chatBotId;


    @ApiModelProperty(value = "接入号")
    @TableField("access_number")
    private String accessNumber;

    @ApiModelProperty(value = "Chatbot 短信端口号=chatBotId 中(接 入号码+自定义号码)")
    @TableField("sms_code")
    @NotBlank(message = "请填写短信端口号（smsCode）", groups = CT.class)
    private String smsCode;

    @ApiModelProperty(value = "状态 0 停用， 1.启用")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "状态（-2未通过 -1审核中 0下线 1上线）")
    @TableField("audit_status")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核内容")
    @TableField("review_data")
    private String reviewData;

    @ApiModelProperty(value = "审核时间")
    @TableField("audit_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditDt;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;

}
