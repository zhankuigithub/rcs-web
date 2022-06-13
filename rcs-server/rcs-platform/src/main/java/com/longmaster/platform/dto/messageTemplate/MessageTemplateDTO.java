package com.longmaster.platform.dto.messageTemplate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.longmaster.platform.dto.card.CardDTO;
import com.longmaster.platform.entity.Material;
import com.longmaster.platform.entity.MessageTemplate;
import com.longmaster.platform.entity.SuggestionItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * author zk
 * date 2021/3/1 16:21
 * description 消息模板返回的实体
 */
@Setter
@Getter
@ToString
public class MessageTemplateDTO extends MessageTemplate {

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("用户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("运营商素材审核")
    private ArrayNode auditRecords;

    @ApiModelProperty("悬浮菜单")
    private List<SuggestionItem> suggestions;

    // 1，4 直接取payload

    // 2,3
    @ApiModelProperty("卡片（type为2，3时存在此项）")
    private List<CardDTO> cards;

    // 5,6,7
    @ApiModelProperty("素材（type为5，6，7时存在此项）")
    private List<Material> materials;

}
