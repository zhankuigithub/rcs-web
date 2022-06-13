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
@TableName("t_log_send_message")
@ApiModel(value = "LogSendMessage对象", description = "下行消息记录表")
public class LogSendMessage extends SuperEntity {

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

    @ApiModelProperty(value = "原始消息内容（方便统计与回落消息使用）")
    @TableField(value = "original")
    private String original;

    @ApiModelProperty(value = "消息内容")
    @TableField(value = "content")
    private String content;

    @ApiModelProperty(value = "日志时间")
    @TableField("log_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logDt;

}
