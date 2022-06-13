package com.longmaster.guixjks.handler.guixjks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.bean.annotation.RcsAction;
import com.longmaster.core.bean.annotation.RcsScene;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.original.Card;
import com.longmaster.core.original.Suggestion;
import com.longmaster.core.original.file.MaterialFile;
import com.longmaster.core.vo.Result;
import com.longmaster.guixjks.Constants;
import com.longmaster.guixjks.service.ICspService;
import com.longmaster.guixjks.service.ILmtxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@RcsScene
public class MessageScene {

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Value("${material.neverReadId}")
    private Long neverReadId;

    @Resource
    private ICspService cspService;

    @Resource
    private ILmtxService lmtxService;

    @Resource
    private ICspService mSendToCspService;

    @RcsAction(Constants.Scene.MESSAGES_LIST)
    public void voucherList(MaapMessage maapMessage) throws IOException {
        Result list = lmtxService.getNeverReadMessage(maapMessage.getUserPhone());
        Object data = list.getData();
        JsonNode node = sObjectMapper.readTree(sObjectMapper.writeValueAsBytes(data));
        // {"count":0}
        sendCard(maapMessage, node);
    }

    private void sendCard(MaapMessage maapMessage, JsonNode node) throws JsonProcessingException {
        List<Card> cards = new ArrayList<>();

        MaterialFile file = new MaterialFile(cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), neverReadId, maapMessage.getMessageBack()));
        List<Suggestion> suggestions = new ArrayList<>();
        Suggestion suggestion = new Suggestion(2, "点击查看详情", lmtxService.buildHtml(maapMessage.getUserPhone(), 5));
        suggestions.add(suggestion);

        Card card = new Card(null,
                MessageFormat.format("你当前有{0}条未读消息", node.get("count").asInt()),
                file, suggestions);
        cards.add(card);

        ObjectNode objectNode = buildCommonBody(maapMessage, 2);// 单卡片
        objectNode.set("content", sObjectMapper.readValue(sObjectMapper.writeValueAsString(cards), ArrayNode.class));
        objectNode.set("layout", defaultStyle());
        mSendToCspService.sendToCsp(objectNode);
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
