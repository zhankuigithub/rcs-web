package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * author zk
 * date 2021/3/2 16:32
 * description PushEvent对象
 */
@TableName("t_push_event")
@ApiModel(value = "PushEvent对象", description = "任务")
@Data
public class PushEvent implements Serializable {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(value = "任务名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "客户id")
    @TableField("customer_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty(value = "应用id")
    @TableField("app_id")
    private Long appId;

    @ApiModelProperty(value = "消息模板id")
    @TableField("message_template_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long messageTemplateId;

    @ApiModelProperty(value = "消息模板名称")
    @TableField("message_template_name")
    private String messageTemplateName;

    @ApiModelProperty(value = "消息模板类型")
    @TableField("message_template_type")
    private Integer messageTemplateType;

    @ApiModelProperty(value = "消息模板回落类型")
    @TableField("message_template_back_type")
    private Integer messageTemplateBackType;

    @ApiModelProperty(value = "手机号（逗号隔开）")
    @TableField("phone_nums")
    private String phoneNums;

    @ApiModelProperty(value = "运营商id（逗号隔开）")
    @TableField("carrier_ids")
    private String carrierIds;

    public static final int SEND_STATUS_INIT = 1; // 待发送
    public static final int SEND_STATUS_CANCEL = 2; // 已取消
    public static final int SEND_STATUS_STOP = 3; // 暂停
    public static final int SEND_STATUS_COMPLETE = 4; // 已完成

    @ApiModelProperty(value = "发送状态")
    @TableField("send_status")
    private Integer sendStatus;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "发送时间")
    @TableField("send_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendDt;

    @ApiModelProperty(value = "发送时间")
    @TableField("update_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;

    @ApiModelProperty(value = "创建时间")
    @TableField("insert_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDt;


}
