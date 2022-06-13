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
@TableName("t_log_message_template_event")
@ApiModel(value = "LogMessageTemplateEvent对象", description = "消息模板点击记录表")
public class LogMessageTemplateEvent extends SuperEntity {

    @ApiModelProperty(value = "数据主键， ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "消息id")
    @TableField(value = "message_id")
    private String messageId;

    @ApiModelProperty(value = "消息模板id")
    @TableField(value = "message_template_id")
    private Long messageTemplateId;

    @ApiModelProperty(value = "消息模板名称")
    @TableField(value = "message_template_name")
    private String messageTemplateName;

    @ApiModelProperty(value = "按钮id")
    @TableField(value = "sug_id")
    private Long sugId;

    @ApiModelProperty(value = "机器人id")
    @TableField(value = "chatbot_id")
    private String chatBotId;

    @ApiModelProperty(value = "应用id")
    @TableField(value = "app_id")
    private Long appId;

    @ApiModelProperty(value = "渠道id")
    @TableField(value = "carrier_id")
    private Long carrierId;

    @ApiModelProperty(value = "手机号")
    @TableField(value = "phone_num")
    private String phoneNum;

    @ApiModelProperty(value = "1 菜单 2建议回复")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "日志时间")
    @TableField("log_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logDt;
}
