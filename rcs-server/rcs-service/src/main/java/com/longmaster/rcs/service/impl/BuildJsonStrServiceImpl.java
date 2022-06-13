package com.longmaster.rcs.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.util.TextUtil;
import com.longmaster.rcs.service.IBuildJsonStrService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BuildJsonStrServiceImpl implements IBuildJsonStrService {

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Value("${cssStyle}")
    private String cssStyle;

    @Override
    public String buildMessage(JsonNode message) {
        ArrayNode arrayNode = sObjectMapper.createArrayNode();

        ObjectNode jsonNode = buildMessageBody(message);
        try {
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
        ObjectNode objectNode1 = buildContent(card);

        contentKey.set("content", objectNode1);
        contentKey.set("layout", buildLayout(layout.get("orientation").asText(), layout.get("width").asText()));

        generalPurposeCardCarouselKey.set("generalPurposeCard", contentKey);

        ObjectNode messageKey = sObjectMapper.createObjectNode();
        messageKey.set("message", generalPurposeCardCarouselKey);

        ObjectNode contentTextKey = sObjectMapper.createObjectNode();
        contentTextKey.set("contentText", messageKey);
        contentTextKey.put("contentType", "application/vnd.gsma.botmessage.v1.0+json");

        ArrayNode messageList = sObjectMapper.createArrayNode();
        messageList.add(contentTextKey);

        objectNode.set("messageList", messageList);
        try {
            // 悬浮
            if (message.has("suggestions")) {
                ObjectNode suggestions = sObjectMapper.createObjectNode();
                suggestions.put("contentType", "application/vnd.gsma.botsuggestion.v1.0+json");
                ObjectNode tmp = sObjectMapper.createObjectNode();
                tmp.set("suggestions", message.get("suggestions"));
                suggestions.set("contentText", tmp);
                messageList.add(suggestions);
            }

            return sObjectMapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {

        }
        return null;
    }

    @Override
    public String buildManyCard(JsonNode message) {

        ObjectNode objectNode = buildMessageBody(message);

        ArrayNode source = (ArrayNode) message.get("content");
        JsonNode layout = message.get("layout");

        ArrayNode content = sObjectMapper.createArrayNode();
        source.forEach(card -> {
            ObjectNode item = buildContent(card);
            content.add(item);
        });

        ObjectNode generalPurposeCardCarouselKey = sObjectMapper.createObjectNode();

        ObjectNode contentKey = sObjectMapper.createObjectNode();
        contentKey.set("content", content);
        contentKey.set("layout", buildLayout(layout.get("orientation").asText(), layout.get("width").asText()));

        generalPurposeCardCarouselKey.set("generalPurposeCardCarousel", contentKey);

        ObjectNode messageKey = sObjectMapper.createObjectNode();
        messageKey.set("message", generalPurposeCardCarouselKey);

        ObjectNode contentTextKey = sObjectMapper.createObjectNode();
        contentTextKey.set("contentText", messageKey);
        contentTextKey.put("contentType", "application/vnd.gsma.botmessage.v1.0+json");

        ArrayNode messageList = sObjectMapper.createArrayNode();
        messageList.add(contentTextKey);

        objectNode.set("messageList", messageList);
        try {
            // 悬浮
            if (message.has("suggestions")) {
                ObjectNode suggestions = sObjectMapper.createObjectNode();
                suggestions.put("contentType", "application/vnd.gsma.botsuggestion.v1.0+json");
                ObjectNode tmp = sObjectMapper.createObjectNode();
                tmp.set("suggestions", message.get("suggestions"));
                suggestions.set("contentText", tmp);
                messageList.add(suggestions);
            }

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
            String urlThumb = thumbJson.get("url").asText();
            String contentTypeThumb = thumbJson.get("contentType").asText();
            int fileSizeThumb = thumbJson.get("fileSize").asInt();
            ObjectNode thumbnail = sObjectMapper.createObjectNode();
            thumbnail.put("fileSize", fileSizeThumb);
            thumbnail.put("type", "thumbnail");
            thumbnail.put("contentType", contentTypeThumb);
            thumbnail.put("url", urlThumb);
            contentText.add(thumbnail);
        }

        // 媒体
        JsonNode fileJson = content.get("file");
        String urlFile = fileJson.get("url").asText();
        String contentTypeFile = fileJson.get("contentType").asText();
        int fileSizeFile = fileJson.get("fileSize").asInt();

        ObjectNode file = sObjectMapper.createObjectNode();
        file.put("fileSize", fileSizeFile);
        file.put("type", "file");
        file.put("contentType", contentTypeFile);
        file.put("url", urlFile);

        contentText.add(file);

        messageDO.set("contentText", contentText);
        messageList.add(messageDO);
        objectNode.set("messageList", messageList);
        try {
            // 悬浮
            if (message.has("suggestions")) {
                ObjectNode suggestions = sObjectMapper.createObjectNode();
                suggestions.put("contentType", "application/vnd.gsma.botsuggestion.v1.0+json");
                ObjectNode tmp = sObjectMapper.createObjectNode();
                tmp.set("suggestions", message.get("suggestions"));
                suggestions.set("contentText", tmp);
                messageList.add(suggestions);
            }

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

        objectNode.put("messageId", message.get("messageId").asText());
        objectNode.put("conversationId", message.get("conversationId").asText());
        objectNode.put("contributionId", message.get("contributionId").asText());
        objectNode.put("senderAddress", message.get("chatBotId").asText());
        objectNode.put("trafficType", "advertisement"); // traffic-type-value = "advertisement" | "payment" | "premium" |"subscription" | "Token"

        objectNode.set("destinationAddress", sObjectMapper.createArrayNode().add("tel:+86" + message.get("userPhone").asText()));

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
    private ObjectNode buildContent(JsonNode card) {
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
        } else {
            // 兼容电信改动
            JsonNode thumbJsonNode = card.get("file");
            media.put("thumbnailUrl", thumbJsonNode.get("url").asText());
            media.put("thumbnailContentType", thumbJsonNode.get("contentType").asText());
            media.put("thumbnailFileSize", thumbJsonNode.get("fileSize").asInt());
        }

        // 文件
        JsonNode fileJsonNode = card.get("file");
        media.put("mediaUrl", fileJsonNode.get("url").asText());
        media.put("mediaContentType", fileJsonNode.get("contentType").asText());
        media.put("mediaFileSize", fileJsonNode.get("fileSize").asInt());

        media.put("height", card.get("height").asText()); // "enum": ["SHORT_HEIGHT", "MEDIUM_HEIGHT", "TALL_HEIGHT"]
        media.put("contentDescription", "  ");
        content.set("media", media);
        return content;
    }


    private ObjectNode buildLayout(String cardOrientation, String cardWidth) {
        ObjectNode layout = sObjectMapper.createObjectNode();

        layout.put("cardOrientation", cardOrientation); // enum : ["VERTICAL","HORIZONTAL"]
        layout.put("imageAlignment", "RIGHT"); // enum: ["LEFT","RIGHT"]
        layout.put("cardWidth", cardWidth); // enum: ["SMALL_WIDTH","MEDIUM_WIDTH"]

        // 样式，字体颜色
        layout.set("titleFontStyle", sObjectMapper.createArrayNode().add("underline").add("bold")); // enum: ["italics", "bold", "underline"]
        layout.set("descriptionFontStyle", sObjectMapper.createArrayNode().add("bold")); // enum: ["italics", "bold", "underline"]

        // style url
        layout.put("style", cssStyle);
        return layout;
    }

}

