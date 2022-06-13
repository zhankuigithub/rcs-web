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
 * date 2021/4/27 18:19
 * description SceneInfo对象
 */
@TableName("t_scene_info")
@ApiModel(value = "SceneInfo对象", description = "场景")
@Data
public class SceneInfo implements Serializable {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(value = "父级id")
    @TableField(value = "app_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "场景名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "场景状态")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "场景json内容")
    @TableField("payload")
    private String payload;

    @ApiModelProperty(value = "创建时间")
    @TableField("insert_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDt;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;

}
