package com.longmaster.guixjks.handler.guixjks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.bean.annotation.RcsAction;
import com.longmaster.core.bean.annotation.RcsScene;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.util.RSAUtil;
import com.longmaster.guixjks.Constants;
import com.longmaster.guixjks.service.ICspService;
import com.longmaster.guixjks.service.IWebApiService;
import com.longmaster.core.original.Card;
import com.longmaster.core.original.Suggestion;
import com.longmaster.core.original.file.MaterialFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@RcsScene
public class PatientScene {

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Value("${material.patientId}")
    private Long patientId;

    @Value("${guijk.url}")
    private String guijkUrl;

    @Resource
    private ICspService cspService;

    @Resource
    private IWebApiService webApiService;

    @Resource
    private ICspService mSendToCspService;

    @RcsAction(Constants.Scene.PATIENT_LIST)
    public void seeDoctor(MaapMessage maapMessage) throws JsonProcessingException {
        JsonNode jsonNode = webApiService.patientList(maapMessage.getUserPhone());
        sendCard(maapMessage, jsonNode);
    }

    private void sendCard(MaapMessage maapMessage, JsonNode result) throws JsonProcessingException {
        List<Card> cards = new ArrayList<>();

        if (result.get("code").asInt() == 0) {
            JsonNode list = result.get("list");
            if (list.size() > 0) {

                ObjectNode objectNode = buildCommonBody(maapMessage, 3); // 多卡片
                if (list.size() == 1) {
                    objectNode.put("type", 2); // 单卡片
                }

                // 构建悬浮菜单
                list.forEach(object -> {
                    String url = cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), patientId, maapMessage.getMessageBack());
                    MaterialFile file = new MaterialFile(url);
                    // 按钮
                    List<Suggestion> suggestions = new ArrayList<>();
                    Suggestion suggestion1 = new Suggestion(
                            Suggestion.ACTION,
                            "编辑",
                            guijkUrl + "/uni/#/pages/askDoctor/profilesEdit?patientId=" + object.get("id").asLong() +
                                    "&source=10024&info=" + RSAUtil.getLoginInfo(maapMessage.getUserPhone(), maapMessage.getDestinationAddress()));
                    suggestions.add(suggestion1);

                    // https://m.guijk.com/uni/#/pages/askDoctor/profilesAdd?source=10024&info=%s
                    Suggestion suggestion2 = new Suggestion(
                            Suggestion.ACTION,
                            "添加",
                            guijkUrl + "/uni/#/pages/askDoctor/profilesAdd?source=10024&info=" + RSAUtil.getLoginInfo(maapMessage.getUserPhone(), maapMessage.getDestinationAddress()));
                    suggestions.add(suggestion2);

                    Card card = new Card();
                    card.setTitle(object.get("patient_name").asText() + " " + object.get("age") + "岁");
                    card.setFile(file);
                    card.setSuggestions(suggestions);

                    cards.add(card);
                });
                objectNode.set("content", sObjectMapper.readValue(sObjectMapper.writeValueAsString(cards), ArrayNode.class));
                objectNode.set("layout", defaultStyle());
                mSendToCspService.sendToCsp(objectNode);
            } else {
                ObjectNode node = buildCommonBody(maapMessage, 1);
                node.put("content", "没有更多家庭成员了");
                mSendToCspService.sendToCsp(node);
            }
        }
    }

    private JsonNode defaultStyle() {
        ObjectNode layout = sObjectMapper.createObjectNode();
        layout.put("width", "MEDIUM_WIDTH");
        layout.put("orientation", "VERTICAL");
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
