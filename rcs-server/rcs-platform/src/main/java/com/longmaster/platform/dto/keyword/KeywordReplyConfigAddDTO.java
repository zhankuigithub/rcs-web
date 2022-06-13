package com.longmaster.platform.dto.keyword;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * author zk
 * date 2021/3/3 9:59
 * description 关键词添加
 */
@Data
public class KeywordReplyConfigAddDTO {

    @ApiModelProperty(value = "规则名称")
    private String name;

    @ApiModelProperty(value = "应用id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "匹配模式（1完全 2包含 3正则表达式）")
    private Integer type;

    @ApiModelProperty(value = "上行关键词")
    private String ruleContent;

    @ApiModelProperty(value = "回复内容（消息模板id）")
    @TableField("reply_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long replyId;

    @ApiModelProperty(value = "权重（越大越靠前）")
    @TableField("weight")
    private Integer weight;

    @ApiModelProperty(value = "状态（0 正常 1 停用）")
    @TableField("status")
    private Integer status;

}
