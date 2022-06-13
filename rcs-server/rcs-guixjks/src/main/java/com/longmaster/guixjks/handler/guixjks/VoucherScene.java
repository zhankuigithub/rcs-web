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
public class VoucherScene {

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Resource
    private ICspService cspService;

    @Resource
    private ILmtxService lmtxService;

    @Resource
    private ICspService mSendToCspService;

    @Value("${material.voucherId}")
    private Long voucherId;

    @RcsAction(Constants.Scene.VOUCHER_LIST)
    public void voucherList(MaapMessage maapMessage) throws IOException {
        Result list = lmtxService.getVoucherList(maapMessage.getUserPhone());
        Object data = list.getData();
        JsonNode node = sObjectMapper.readTree(sObjectMapper.writeValueAsBytes(data));
        // {
        //    "ask_medical_count":"0",
        //    "ask_medical_time":"有效期至2022年08月11日",
        //    "ask_famous_doc_count":43,
        //    "ask_famous_doc_time_fmt":"有效期至2021年11月05日"
        //}
        sendCard(maapMessage, node);
    }


    private void sendCard(MaapMessage maapMessage, JsonNode node) throws JsonProcessingException {
        List<Card> cards = new ArrayList<>();

        // 卡1
        MaterialFile file = new MaterialFile(cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), voucherId, maapMessage.getMessageBack()));
        List<Suggestion> suggestions = new ArrayList<>();
        Suggestion suggestion = new Suggestion(2, "点击查看详情", lmtxService.buildHtml(maapMessage.getUserPhone(), 7));
        suggestions.add(suggestion);

        int askMedicalCount = 0; // 问医券
        if (node.has("ask_medical_count")) {
            askMedicalCount = node.get("ask_medical_count").asInt();
        }

        String askMedicalTime = ""; // 问医券到期时间
        if (node.has("ask_medical_time")) {
            askMedicalTime = node.get("ask_medical_time").asText();
        }


        Card card1 = new Card("贵象医生在线咨询",
                MessageFormat.format("剩余问医券：{0}张\n{1}", askMedicalCount, askMedicalTime),
                file, suggestions);

        // 卡2
        int askFamousDocCount = 0; // 紫金券
        if (node.has("ask_famous_doc_count")) {
            askFamousDocCount = node.get("ask_famous_doc_count").asInt();
        }
        String askFamousDocTimeFmt = ""; // 紫金券到期时间
        if (node.has("ask_famous_doc_time_fmt")) {
            askFamousDocTimeFmt = node.get("ask_famous_doc_time_fmt").asText();
        }

        Card card2 = new Card("紫金名医问诊",
                MessageFormat.format("剩余问医券：{0}张\n{1}", askFamousDocCount, askFamousDocTimeFmt),
                file, suggestions);

        cards.add(card1);
        cards.add(card2);

        ObjectNode objectNode = buildCommonBody(maapMessage, 3);// 多卡片
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
