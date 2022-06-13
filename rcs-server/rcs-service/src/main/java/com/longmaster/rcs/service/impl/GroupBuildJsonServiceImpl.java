package com.longmaster.rcs.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.util.TextUtil;
import com.longmaster.rcs.service.IGroupBuildJsonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * author zk
 * date 2021/2/24 18:00
 * description 群发组装json（电信）
 */
@Component
public class GroupBuildJsonServiceImpl implements IGroupBuildJsonService {

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Value("${cssStyle}")
    private String cssStyle;

    @Override
    public String buildMessage(JsonNode message) {
        ArrayNode arrayNode = sObjectMapper.createArrayNode();

        ObjectNode jsonNode = buildMessageBody(message);

        // 构建 messageList
        if (message.has("content")) {
            ObjectNode messageDO = sObjectMapper.createObjectNode();
            messageDO.put("contentType", "text/plain");
            messageDO.put("contentEncoding", "utf8");
            messageDO.put("contentText", message.get("content").asText());
            arrayNode.add(messageDO);
        }

        if (message.has("suggestions")) {
            ObjectNode suggestions = sObjectMapper.createObjectNode();
            suggestions.put("contentType", "application/vnd.gsma.botsuggestion.v1.0+json");

            ObjectNode objectNode = sObjectMapper.createObjectNode();
            objectNode.set("suggestions", message.get("suggestions"));
            suggestions.set("contentText", objectNode);
            arrayNode.add(suggestions);
        }

        jsonNode.set("messageList", arrayNode);
        try {
            return sObjectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
        }
        return null;
    }

    @Override
    public String buildSingleCard(JsonNode message) {
        ObjectNode objectNode = buildMessageBody(message);
        JsonNode card = message.get("content").get(0);
        JsonNode layout = message.get("layout");

        ObjectNode generalPurposeCardCarouselKey = sObjectMapper.createObjectNode();

        ObjectNode contentKey = sObjectMapper.createObjectNode();
        ObjectNode objectNode1 = buildContent(card, "TALL_HEIGHT");

        contentKey.set("content", objectNode1);
        contentKey.set("layout", buildLayout(layout));
        generalPurposeCardCarouselKey.set("generalPurposeCard", contentKey);

        ObjectNode messageKey = sObjectMapper.createObjectNode();
        messageKey.set("message", generalPurposeCardCarouselKey);

        ObjectNode contentTextKey = sObjectMapper.createObjectNode();
        contentTextKey.set("contentText", messageKey);
        contentTextKey.put("contentType", "application/vnd.gsma.botmessage.v1.0+json");

        ArrayNode messageList = sObjectMapper.createArrayNode();
        messageList.add(contentTextKey);

        objectNode.set("messageList", messageList);

        // 悬浮
        if (message.has("suggestions")) {
            ObjectNode suggestions = sObjectMapper.createObjectNode();
            suggestions.put("contentType", "application/vnd.gsma.botsuggestion.v1.0+json");
            ObjectNode tmp = sObjectMapper.createObjectNode();
            tmp.set("suggestions", message.get("suggestions"));
            suggestions.set("contentText", tmp);
            messageList.add(suggestions);
        }

        try {
            return sObjectMapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {

        }
        return null;
    }

    @Override
    public String buildManyCard(JsonNode message) {
        ObjectNode objectNode = buildMessageBody(message);
        JsonNode layout = message.get("layout");
        ArrayNode source = (ArrayNode) message.get("content");

        ArrayNode content = sObjectMapper.createArrayNode();
        source.forEach(card -> {
            ObjectNode item = buildContent(card, "TALL_HEIGHT");
            content.add(item);
        });

        ObjectNode generalPurposeCardCarouselKey = sObjectMapper.createObjectNode();

        ObjectNode contentKey = sObjectMapper.createObjectNode();
        contentKey.set("content", content);
        contentKey.set("layout", buildLayout(layout));

        generalPurposeCardCarouselKey.set("generalPurposeCardCarousel", contentKey);

        ObjectNode messageKey = sObjectMapper.createObjectNode();
        messageKey.set("message", generalPurposeCardCarouselKey);

        ObjectNode contentTextKey = sObjectMapper.createObjectNode();
        contentTextKey.set("contentText", messageKey);
        contentTextKey.put("contentType", "application/vnd.gsma.botmessage.v1.0+json");

        ArrayNode messageList = sObjectMapper.createArrayNode();
        messageList.add(contentTextKey);

        objectNode.set("messageList", messageList);

        // 悬浮
        if (message.has("suggestions")) {
            ObjectNode suggestions = sObjectMapper.createObjectNode();
            suggestions.put("contentType", "application/vnd.gsma.botsuggestion.v1.0+json");
            ObjectNode tmp = sObjectMapper.createObjectNode();
            tmp.set("suggestions", message.get("suggestions"));
            suggestions.set("contentText", tmp);
            messageList.add(suggestions);
        }

        try {
            return sObjectMapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {

        }
        return null;
    }

    @Override
    public String buildFile(JsonNode message) {

        ObjectNode objectNode = buildMessageBody(message);
        ArrayNode messageList = sObjectMapper.createArrayNode();

        ObjectNode messageDO = sObjectMapper.createObjectNode();
        messageDO.put("contentType", "application/vnd.gsma.rcs-ft-http");
        messageDO.put("contentEncoding", "utf8");

        // 构建contentText
        JsonNode content = message.get("content").get(0); // 取第一个

        ArrayNode contentText = sObjectMapper.createArrayNode();

        if (content.has("thumb")) {
            // 封面
            JsonNode thumbJson = content.get("thumb");
            ObjectNode thumbnail = sObjectMapper.createObjectNode();
            thumbnail.put("type", "thumbnail");
            thumbnail.put("url", thumbJson.get("url").asText());
            thumbnail.put("contentType", thumbJson.get("contentType").asText());
            thumbnail.put("fileSize", thumbJson.get("fileSize").asInt());

            contentText.add(thumbnail);
        } else {
            // 兼容电信改动
            JsonNode thumbJson = content.get("file");
            ObjectNode thumbnail = sObjectMapper.createObjectNode();
            thumbnail.put("type", "thumbnail");
            thumbnail.put("url", thumbJson.get("url").asText());
            thumbnail.put("contentType", thumbJson.get("contentType").asText());
            thumbnail.put("fileSize", thumbJson.get("fileSize").asInt());

            contentText.add(thumbnail);
        }

        // 媒体
        JsonNode fileJson = content.get("file");

        ObjectNode file = sObjectMapper.createObjectNode();
        file.put("type", "file");
        file.put("url", fileJson.get("url").asText());
        file.put("contentType", fileJson.get("contentType").asText());
        file.put("fileSize", fileJson.get("fileSize").asInt());

        contentText.add(file);

        messageDO.set("contentText", contentText);
        messageList.add(messageDO);
        objectNode.set("messageList", messageList);

        // 悬浮
        if (message.has("suggestions")) {
            ObjectNode suggestions = sObjectMapper.createObjectNode();
            suggestions.put("contentType", "application/vnd.gsma.botsuggestion.v1.0+json");
            ObjectNode tmp = sObjectMapper.createObjectNode();
            tmp.set("suggestions", message.get("suggestions"));
            suggestions.set("contentText", tmp);
            messageList.add(suggestions);
        }

        try {
            return sObjectMapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {

        }
        return null;
    }


    /**
     * author zk
     * date 2021/2/23 16:05
     * description 消息公共体
     */
    private ObjectNode buildMessageBody(JsonNode message) {
        ObjectNode objectNode = sObjectMapper.createObjectNode();

        // 设置状态报告
        ArrayNode reportRequest = sObjectMapper.createArrayNode();
        // sent：消息已发送
        //failed：消息发送失败
        //delivered：消息已送达
        //displayed：消息已阅读
        //deliveredToNetwork：已转短消息已送达
        reportRequest.add("sent");
        reportRequest.add("failed");
        reportRequest.add("delivered");
        reportRequest.add("displayed");
        reportRequest.add("deliveredToNetwork");
        objectNode.set("reportRequest", reportRequest);

        objectNode.put("messageId", UUID.randomUUID().toString());
        objectNode.put("conversationId", UUID.randomUUID().toString());
        objectNode.put("contributionId", UUID.randomUUID().toString());
        objectNode.put("senderAddress", message.get("chatBotId").asText());
        objectNode.put("trafficType", "advertisement"); // traffic-type-value = "advertisement" | "payment" | "premium" |"subscription" | "Token"

        objectNode.set("destinationAddress", buildDestinationAddress(message.get("userPhones")));

        // 回落类型 0（不回落） 1（回落h5） 2（回落普通短信） 3（UP1.0） 3才是真正运营商的回落
        int backType = message.has("backType") ? message.get("backType").asInt() : 0;
        objectNode.put("smsSupported", backType == 3);
        if (backType == 3) {
            objectNode.put("smsContent", message.get("smsContent").asText());
        }

        objectNode.put("storeSupported", false);

        ObjectNode serviceCapability = sObjectMapper.createObjectNode();
        serviceCapability.put("capabilityId", "ChatbotSA");
        serviceCapability.put("version", "+g.gsma.rcs.botversion=\"#=1\"");
        objectNode.set("serviceCapability", sObjectMapper.createArrayNode().add(serviceCapability));
        return objectNode;
    }


    /**
     * author zk
     * date 2021/2/23 16:05
     * description 卡片内部content
     */
    private ObjectNode buildContent(JsonNode card, String style) {
        ObjectNode content = sObjectMapper.createObjectNode();

        if (card.has("title") && !TextUtil.isEmpty(card.get("title").asText())) {
            content.put("title", card.get("title").asText());
        }

        if (card.has("description") && !TextUtil.isEmpty(card.get("description").asText())) {
            content.put("description", card.get("description").asText());
        }

        if (card.has("suggestions")) {
            content.set("suggestions", card.get("suggestions"));
        }

        ObjectNode media = sObjectMapper.createObjectNode();

        // 封面
        if (card.has("thumb")) {
            JsonNode thumbJsonNode = card.get("thumb");
            if (!TextUtil.isEmpty(thumbJsonNode.get("url").asText())) {
                media.put("thumbnailUrl", thumbJsonNode.get("url").asText());
            }
            if (!TextUtil.isEmpty(thumbJsonNode.get("contentType").asText())) {
                media.put("thumbnailContentType", thumbJsonNode.get("contentType").asText());
            }

            if (thumbJsonNode.get("fileSize").asInt() > 0) {
                media.put("thumbnailFileSize", thumbJsonNode.get("fileSize").asInt());
            }
        }

        // 文件
        JsonNode fileJsonNode = card.get("file");
        media.put("mediaUrl", fileJsonNode.get("url").asText());
        media.put("mediaContentType", fileJsonNode.get("contentType").asText());
        media.put("mediaFileSize", fileJsonNode.get("fileSize").asInt());

        media.put("height", style); // "enum": ["SHORT_HEIGHT", "MEDIUM_HEIGHT", "TALL_HEIGHT"]
        media.put("contentDescription", "  ");
        content.set("media", media);
        return content;
    }


    private ObjectNode buildLayout(JsonNode jLayout) {

        ObjectNode layout = sObjectMapper.createObjectNode();

        layout.put("cardOrientation", jLayout != null && jLayout.has("orientation") ? jLayout.get("orientation").asText() : "VERTICAL"); // enum : ["VERTICAL","HORIZONTAL"]
        layout.put("imageAlignment", "RIGHT"); // enum: ["LEFT","RIGHT"]
        layout.put("cardWidth", jLayout != null && jLayout.has("width") ? jLayout.get("width").asText() : "MEDIUM_WIDTH"); // enum: ["SMALL_WIDTH","MEDIUM_WIDTH"]

        // 样式，字体颜色
        layout.set("titleFontStyle", sObjectMapper.createArrayNode().add("underline").add("bold")); // enum: ["italics", "bold", "underline"]
        layout.set("descriptionFontStyle", sObjectMapper.createArrayNode().add("bold")); // enum: ["italics", "bold", "underline"]

        // style url
        layout.put("style", cssStyle);
        return layout;
    }

    private ArrayNode buildDestinationAddress(JsonNode arrayNode) {
        ArrayNode jsonNodes = sObjectMapper.createArrayNode();
        arrayNode.forEach(phone -> jsonNodes.add("tel:+86" + phone.asText()));
        return jsonNodes;
    }

}
