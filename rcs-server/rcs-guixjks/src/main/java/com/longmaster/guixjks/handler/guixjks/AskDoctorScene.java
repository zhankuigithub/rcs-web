package com.longmaster.guixjks.handler.guixjks;

import cn.hutool.core.date.DateUtil;
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
import com.longmaster.guixjks.service.ILmtxService;
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
public class AskDoctorScene {

    @Resource
    private ICspService mSendToCspService;

    @Resource
    private IWebApiService mWebApiService;

    @Resource
    private ILmtxService mLmtxService;

    @Resource
    private ICspService cspService;

    @Value("${guijk.url}")
    private String guijkUrl;

    private Map<String, AtomicInteger> clickMap = new ConcurrentHashMap();

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @Value("${material.diagnosisId}")
    private Long diagnosisId;

    @RcsAction(Constants.Scene.SBC_DIAGNOSIS_DOCTOR)
    public void askDoctor(MaapMessage maapMessage) throws IOException {
        clickMap.computeIfAbsent(maapMessage.getUserPhone(), k -> new AtomicInteger(1));

        JsonNode result = mWebApiService.recordList(maapMessage.getUserPhone(), 1);
        sendCard(maapMessage, result);
    }

    @RcsAction(Constants.Scene.SBC_DIAGNOSIS_DOCTOR_NEXT)
    public void askDoctorNext(MaapMessage maapMessage) throws IOException {
        AtomicInteger atomicInteger = clickMap.get(maapMessage.getUserPhone());
        int pageIndex = atomicInteger == null ? 1 : atomicInteger.incrementAndGet();
        clickMap.computeIfAbsent(maapMessage.getUserPhone(), k -> atomicInteger);

        JsonNode result = mWebApiService.recordList(maapMessage.getUserPhone(), pageIndex);
        sendCard(maapMessage, result);
    }

    private void sendCard(MaapMessage maapMessage, JsonNode result) throws JsonProcessingException {
        if (result.get("code").asInt() == 0) {

            List<Card> cards = new ArrayList<>();
            ObjectNode objectNode = buildCommonBody(maapMessage, 3);

            JsonNode list = result.get("list");
            if (list.size() > 0) {
                if (list.size() == 1) {
                    objectNode.put("type", 2);
                }

                // 构建悬浮菜单
                List<Suggestion> sugs = new ArrayList<>();
                //下一页
                Suggestion sug = new Suggestion(Suggestion.ACTION, "更多历史", mLmtxService.buildHtml(maapMessage.getUserPhone(), 4));
                sugs.add(sug);
                objectNode.set("suggestions", sObjectMapper.readValue(sObjectMapper.writeValueAsString(sugs), ArrayNode.class));

                list.forEach(object -> {
                    String url = cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), diagnosisId, maapMessage.getMessageBack());
                    MaterialFile file = new MaterialFile(url);
                    // 按钮
                    List<Suggestion> suggestions = new ArrayList<>();
                    Suggestion suggestion1 = new Suggestion(
                            Suggestion.ACTION,
                            "查看详情",
                            guijkUrl + "/uni/#/pages/inquiry/im/chat?inquiry_id=" + object.get("inquiry_id").asLong() + "&source=10024&info="
                                    + RSAUtil.getLoginInfo(maapMessage.getUserPhone(), maapMessage.getDestinationAddress()));
                    suggestions.add(suggestion1);


                    Card card = new Card(
                            object.get("doc_department").asText() + " " + object.get("doc_name").asText(),
                            object.get("hospital").asText() + "\n" + DateUtil.date(object.get("insert_dt").asLong() * 1000),
                            file,
                            suggestions);
                    cards.add(card);
                });
                objectNode.set("content", sObjectMapper.readValue(sObjectMapper.writeValueAsString(cards), ArrayNode.class));
                objectNode.set("layout", defaultStyle());
                mSendToCspService.sendToCsp(objectNode);
            } else {
                ObjectNode node = buildCommonBody(maapMessage, 1);
                node.put("content", "没有更多记录了");
                mSendToCspService.sendToCsp(node);
            }
        } else {
            ObjectNode node = buildCommonBody(maapMessage, 1);
            node.put("content", "没有更多记录了");
            mSendToCspService.sendToCsp(node);
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
