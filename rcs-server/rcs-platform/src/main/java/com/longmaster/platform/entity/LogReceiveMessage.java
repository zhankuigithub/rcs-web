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
@TableName("t_log_receive_message")
@ApiModel(value = "LogReceiveMessage对象", description = "上行消息记录表")
public class LogReceiveMessage extends SuperEntity {

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

    @ApiModelProperty(value = "消息内容")
    @TableField(value = "content")
    private String content;

    @ApiModelProperty(value = "文案或者文件")
    @TableField(value = "payload")
    private String payload;

    @ApiModelProperty(value = "菜单id，当type为5时才存在")
    @TableField(value = "menu_id")
    private Long menuId;

    @ApiModelProperty(value = "是否为关键词，当type为1时才存在")
    @TableField(value = "is_keyword")
    private Integer isKeyword;

    @ApiModelProperty(value = "消息类型（1 文本消息 2 建议回复 3 文件消息 4 终端共享配置数据 5 菜单 6 自定义按钮 ）")
    @TableField(value = "content_type")
    private Integer contentType;

    @ApiModelProperty(value = "日志时间")
    @TableField("log_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logDt;

}
