package com.longmaster.guixjks.handler.guixjks;

import cn.hutool.core.util.StrUtil;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RcsScene
public class SearchDoctorScene {

    @Resource
    private ICspService mSendToCspService;

    @Resource
    private IWebApiService mWebApiService;

    @Value("${guijk.url}")
    private String guijkUrl;

    @Value("${material.search.defaultAvatarId}")
    private long defaultAvatarId;

    @Value("${material.search.manAvatarId}")
    private long manAvatarId;

    @Value("${material.search.womanAvatarId}")
    private long womanAvatarId;

    @Resource
    private ICspService cspService;

    private Map<String, AtomicInteger> clickMap = new ConcurrentHashMap();

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @RcsAction(Constants.Scene.SBC_SEARCH_DOCTOR)
    public void searchDoctor(MaapMessage maapMessage) throws IOException {
        clickMap.computeIfAbsent(maapMessage.getUserPhone(), k -> new AtomicInteger(1)); // 首次/重新进入时强制覆盖
        JsonNode result = mWebApiService.doctorList(maapMessage.getBodyText(), 1);
        sendCard(maapMessage, result);
    }

    private void sendCard(MaapMessage maapMessage, JsonNode result) throws JsonProcessingException {
        String chatBotId = maapMessage.getDestinationAddress();

        // 通用
        ObjectNode objectNode = buildCommonBody(maapMessage, 3);

        List<Card> cards = new ArrayList<>();

        if (result.get("code").asInt() == 0) {
            JsonNode list = result.get("list");

            if (list.size() > 0) {

                if (list.size() > 1) {
                    objectNode.put("type", 3);
                }

                List<Suggestion> sugs = new ArrayList<>();
                Suggestion suggestion = new Suggestion(Suggestion.ACTION,
                        "更多医生", guijkUrl + "/uni/#/?source=10024&info="
                        + RSAUtil.getLoginInfo(maapMessage.getUserPhone(), maapMessage.getDestinationAddress()));
                sugs.add(suggestion);

                objectNode.set("suggestions", sObjectMapper.readValue(sObjectMapper.writeValueAsString(sugs), ArrayNode.class));

                list.forEach(object -> {
                    int gender = object.get("gender").asInt();
                    String docDace = object.get("doc_face").asText();

                    // 取到医生各自在csp后台添加过的头像，没有则判断性别取
                    String url = cspService.getAuditMaterialUrlByOriginalUrl(chatBotId, docDace, maapMessage.getMessageBack());
                    if (StrUtil.isEmpty(url)) {
                        switch (gender) {
                            case 0:
                                url = cspService.getAuditMaterialUrl(chatBotId, defaultAvatarId, maapMessage.getMessageBack());
                                break;
                            case 1:
                                url = cspService.getAuditMaterialUrl(chatBotId, manAvatarId, maapMessage.getMessageBack());
                                break;
                            case 2:
                                url = cspService.getAuditMaterialUrl(chatBotId, womanAvatarId, maapMessage.getMessageBack());
                                break;
                        }
                    }

                    MaterialFile file = new MaterialFile(url);
                    // 按钮
                    List<Suggestion> suggestions = new ArrayList<>();
                    Suggestion suggestion1 = new Suggestion(
                            Suggestion.ACTION,
                            "去问诊",
                            guijkUrl + "/uni/#/pages/askDoctor/doctorDetails?doc_id=" + object.get("doc_id").asInt() + "&source=10019&info="
                                    + RSAUtil.getLoginInfo(maapMessage.getUserPhone(), maapMessage.getDestinationAddress()));
                    suggestions.add(suggestion1);

                    Card card = new Card(object.get("doc_name").asText(), object.get("hospital").asText() + " " + object.get("department").asText(), file, suggestions);
                    cards.add(card);
                });
                objectNode.set("content", sObjectMapper.readValue(sObjectMapper.writeValueAsString(cards), ArrayNode.class));
                objectNode.set("layout", defaultStyle());
                mSendToCspService.sendToCsp(objectNode);
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
