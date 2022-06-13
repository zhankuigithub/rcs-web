package com.longmaster.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_keyword_reply_config")
@ApiModel(value = "KeywordReplyConfig对象", description = "")
public class KeywordReplyConfig extends CommonEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属应用")
    @TableField("app_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "配置名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "匹配类型（1完全 2包含 3正则表达式 4兜底消息）")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "权重（越大越靠前）")
    @TableField("weight")
    private Integer weight;

    @ApiModelProperty(value = "匹配规则内容")
    @TableField("rule_content")
    private String ruleContent;

    @ApiModelProperty(value = "回复id")
    @TableField("reply_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long replyId;

    @ApiModelProperty(value = "状态（0正常，1停用）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除， 0. 正常、1. 删除")
    @TableField("logic_del")
    @TableLogic
    @JsonIgnore
    private Integer logicDel;


}
