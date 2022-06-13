package com.longmaster.platform.dto.messageTemplate;

import com.longmaster.platform.entity.SuggestionItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * author zk
 * date 2021/3/4 11:02
 * description 消息模板修改
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageTemplateUpdDTO {

    @ApiModelProperty(value = "主键ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(value = "所属应用")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty(value = "模板名称")
    private String name;

    @ApiModelProperty(value = "模板类型（1文本消息，2单卡片，3多卡片，4地理消息 5音频 6视频 7图片）")
    private Integer type;

    @ApiModelProperty("回落类型 0（不回落） 1（回落h5） 2（回落普通短信） 3（UP1.0）")
    private Integer backType;

    @ApiModelProperty(value = "消息内容")
    private String payload;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    private Integer status;

    @ApiModelProperty(value = "建议回复菜单id （多个id使用逗号隔开）")
    private String sugIds;

    @ApiModelProperty("卡片宽度 1 小 2 大")
    private Integer width;

    @ApiModelProperty("排版位置 1 垂直 2 水平")
    private Integer orientation;

    @ApiModelProperty("图片位置 1 左 2 右")
    private Integer imageAlignment;

    @ApiModelProperty("回落短信内容")
    private String smsContent;

    @ApiModelProperty("悬浮菜单")
    private List<SuggestionItem> suggestions;

}
