package com.longmaster.platform.dto.card;

import com.longmaster.platform.entity.Card;
import com.longmaster.platform.entity.SuggestionItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * author zk
 * date 2021/3/2 10:21
 * description 卡片返回的实体
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDTO extends Card {

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("用户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("运营商素材审核")
    private JsonNode auditRecords;

    @ApiModelProperty("按钮")
    private List<SuggestionItem> suggestions;

    @ApiModelProperty("素材信息")
    private CardMaterial material;

    @Data
    public static class CardMaterial {
        private String thumbnailUrl;
        private String fileUrl;
        private Integer type;
    }

}
