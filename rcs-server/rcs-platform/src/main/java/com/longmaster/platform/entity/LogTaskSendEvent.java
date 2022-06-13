package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_log_task_send_event")
@ApiModel(value = "LogTaskSendEvent对象", description = "群消息发送记录表")
public class LogTaskSendEvent extends SuperEntity {

    @ApiModelProperty(value = "数据主键， ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务id")
    @TableField(value = "task_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long taskId;

    @ApiModelProperty(value = "消息id")
    @TableField(value = "message_id")
    private String messageId;

    @ApiModelProperty(value = "任务名")
    @TableField(value = "name")
    private String name;

    @ApiModelProperty(value = "机器人id")
    @TableField(value = "chatbot_id")
    private String chatBotId;

    @ApiModelProperty(value = "应用id")
    @TableField(value = "app_id")
    private Long appId;

    @ApiModelProperty(value = "手机号")
    @TableField(value = "phone_num")
    private String phoneNum;

    @ApiModelProperty(value = "渠道商id")
    @TableField("carrier_id")
    private Long carrierId;

    @ApiModelProperty(value = "消息模板id")
    @TableField(value = "message_template_id")
    private Long messageTemplateId;

    @ApiModelProperty(value = "消息模板名称")
    @TableField(value = "message_template_name")
    private String messageTemplateName;

    @ApiModelProperty(value = "消息模板类型")
    @TableField(value = "message_template_type")
    private Integer messageTemplateType;

    @ApiModelProperty(value = "消息模板回落类型")
    @TableField(value = "message_template_back_type")
    private Integer messageTemplateBackType;

    @ApiModelProperty(value = "发送状态 1已发送 2发送失败 3消息已送达 4已阅读 5已转短 6已撤回 7机器人未开通 8机器人未审核 9素材未审核 10用户未登录 404 未知错误通用码")
    @TableField(value = "status")
    private Integer status;

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_CHATBOT_IS_NOT_ENABLE = 7;
    public static final int STATUS_CHATBOT_NEVER_AUDIT = 8;
    public static final int STATUS_MATERIAL_NEVER_AUDIT = 9;
    public static final int STATUS_USER_IS_NOT_ONLINE = 10;
    public static final int STATUS_404 = 404;

    @ApiModelProperty(value = "日志时间")
    @TableField("log_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logDt;

    @ApiModelProperty(value = "更新时间，回调通知修改本行记录，此字段可理解为发送时间")
    @TableField("update_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;
}
