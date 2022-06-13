package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@TableName("t_news_info")
@ApiModel(value = "NewsInfo对象", description = "新闻")
public class NewsInfo {

    @ApiModelProperty(value = "数据主键， ID")
    @TableId(value = "id", type = IdType.AUTO)
    @NotNull(message = "主键不允许为空~")
    private String id;

    @ApiModelProperty(value = "类型")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "来源")
    @TableField("source")
    private String source;

    @ApiModelProperty(value = "标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "封面")
    @TableField("source_url")
    private String sourceUrl;

    @ApiModelProperty(value = "内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "权重")
    @TableField("weight")
    private Integer weight;


    @ApiModelProperty(value = "创建时间")
    @TableField("insert_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDt;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDt;

}
