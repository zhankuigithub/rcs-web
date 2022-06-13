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
@TableName("t_log_send_message_status")
@ApiModel(value = "LogSendMessageStatus对象", description = "下行消息状态记录表")
public class LogSendMessageStatus extends SuperEntity {

    @ApiModelProperty(value = "数据主键， ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "消息id")
    @TableField(value = "message_id")
    private String messageId;

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

    @ApiModelProperty(value = "类型（1 已发送 2发送失败 3 已接收 4 已阅读 5 已转短 6已撤回）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "日志时间")
    @TableField("log_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logDt;
}
