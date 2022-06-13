package com.longmaster.guixjks.handler.guixjks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.bean.annotation.RcsAction;
import com.longmaster.core.bean.annotation.RcsScene;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.original.Card;
import com.longmaster.core.original.Suggestion;
import com.longmaster.core.original.file.MaterialFile;
import com.longmaster.guixjks.Constants;
import com.longmaster.guixjks.service.ICspService;
import com.longmaster.guixjks.service.IHealthVideoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@RcsScene
public class HealthVideoScene {

    @Resource
    private ICspService mSendToCspService;

    @Resource
    private IHealthVideoService mHealthVideoService;

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @Resource
    private ICspService cspService;

    @Value("${material.healthVideoId}")
    private Long healthVideoId;

    // 暂未使用
    @RcsAction(Constants.Scene.HEALTH_VIDEO)
    public void healthInformation(MaapMessage maapMessage) throws JsonProcessingException {
        JsonNode result = mHealthVideoService.getVideos(maapMessage.getUserPhone(), 3, 5); // format: {"code":200,"message":null, "data":[...]}
        if (result.get("code").asInt() == 200) {
            // 通用
            ObjectNode objectNode = buildCommonBody(maapMessage, 3);
            // 构建悬浮菜单
            List<Suggestion> suggestions = new ArrayList<>();

            // 返回
            Suggestion s1 = new Suggestion(Suggestion.REPLY, "返回", Constants.Scene.GUIXJK_RETURN_HOME); // 简单兼容

            //换一批
            Suggestion s2 = new Suggestion(Suggestion.REPLY, "换一批", Constants.Scene.HEALTH_VIDEO);

            suggestions.add(s1);
            suggestions.add(s2);
            objectNode.set("suggestions", sObjectMapper.readValue(sObjectMapper.writeValueAsString(suggestions), ArrayNode.class));

            // 构建卡片
            List<Card> cards = new ArrayList<>();

            JsonNode data = result.get("data");
            if (data.getNodeType() == JsonNodeType.ARRAY) {
                for (JsonNode node : data) {

                    JsonNode videoInfo = node.get("VideoInfo");
                    JsonNode fileUrl = videoInfo.get("FileUrls").get(0); // 是一个数组，且取第一条
                    String filePath = fileUrl.get("FilePath").asText();

                    String url = cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), healthVideoId, maapMessage.getMessageBack());
                    MaterialFile file = new MaterialFile(url);

                    // 按钮
                    List<Suggestion> suggestionList = new ArrayList<>();
                    Suggestion sug = new Suggestion(Suggestion.ACTION, "查看详情", filePath);
                    suggestionList.add(sug);
                    Card card = new Card(node.get("Title").asText(), node.get("Summary").asText(), file, suggestionList);
                    cards.add(card);
                }
            }
            objectNode.set("content", sObjectMapper.readValue(sObjectMapper.writeValueAsString(cards), ArrayNode.class));
            objectNode.set("layout", defaultStyle());
            mSendToCspService.sendToCsp(objectNode);
        }
    }

    private JsonNode defaultStyle() {
        ObjectNode layout = sObjectMapper.createObjectNode();
        layout.put("cardWidth", "MEDIUM_WIDTH");
        layout.put("cardHeight", "MEDIUM_HEIGHT");
        layout.put("cardOrientation", "VERTICAL");
        return layout;
    }

    private ObjectNode buildCommonBody(MaapMessage maapMessage, int messageType) {
        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("type", messageType);
        objectNode.put("chatBotId", maapMessage.getDestinationAddress());
        objectNode.put("userPhone", maapMessage.getUserPhone());
        objectNode.put("messageId", maapMessage.getMessageId());
        objectNode.put("conversationId", maapMessage.getConversationId());
        objectNode.put("contributionId", maapMessage.getContributionId());
        objectNode.put("isMessageBack", maapMessage.getMessageBack());
        return objectNode;
    }
}
