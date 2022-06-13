package com.longmaster.platform.dto.messageTemplate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * author zk
 * date 2021/3/3 15:26
 * description 消息模板添加
 */
@Data
public class MessageTemplateAddDTO {

    @ApiModelProperty("应用appId")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    @ApiModelProperty("模板名称")
    private String name;

    @ApiModelProperty("模板类型（1文本消息，2单卡片，3多卡片，4地理消息 5图片 6音频 7视频）")
    private Integer type;

    @ApiModelProperty("回落类型 0（不回落） 1（回落h5） 2（回落普通短信） 3（UP1.0）")
    private Integer backType;

    @ApiModelProperty("内容（不同的类型保存格式不同）")
    private String payload;

    @ApiModelProperty("回落短信内容")
    private String smsContent;

    @ApiModelProperty("卡片宽度 1 小 2 大")
    private Integer width;

    @ApiModelProperty("排版位置 1 垂直 2 水平")
    private Integer orientation;

    @ApiModelProperty("图片位置 1 左 2 右")
    private Integer imageAlignment;

    @ApiModelProperty("按钮")
    private List<JsonNode> suggestions;


}

