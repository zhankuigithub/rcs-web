package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("t_log_scene_message")
@ApiModel(value = "LogSceneMessage对象", description = "场景执行记录表")
public class LogSceneMessage extends SuperEntity {

    @ApiModelProperty(value = "应用id")
    @TableField(value = "app_id")
    private Long appId;

    @ApiModelProperty(value = "渠道id")
    @TableField(value = "carrier_id")
    private Long carrierId;

    @ApiModelProperty(value = "场景id")
    @TableField(value = "scene_id")
    private Long sceneId;

    @ApiModelProperty(value = "场景名")
    @TableField(value = "scene_name")
    private String sceneName;

    @ApiModelProperty(value = "手机号")
    @TableField(value = "phone_num")
    private String phoneNum;

    @ApiModelProperty(value = "日志时间")
    @TableField("log_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logDt;

}
