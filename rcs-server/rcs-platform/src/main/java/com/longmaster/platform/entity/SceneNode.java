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
 */
@TableName("t_scene_node")
@ApiModel(value = "SceneNode对象", description = "场景节点")
@Data
public class SceneNode implements Serializable {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(value = "节点名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "父级id")
    @TableField(value = "pid")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long pId;


    @ApiModelProperty(value = "类型 1 消息模板 2 建议回复 3场景 4关键词 5逻辑判断")
    @TableField(value = "type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer type;

    @ApiModelProperty(value = "执行内容")
    @TableField("payload")
    private String payload;

    @ApiModelProperty(value = "类型 1 消息模板 2 建议回复 3场景 4关键词 5逻辑判断")
    @TableField(value = "scene_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long sceneId;

    @ApiModelProperty(value = "创建时间")
    @TableField("insert_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDt;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;

}
