package com.longmaster.rcs.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.util.PhoneHelpUtil;
import com.longmaster.rcs.service.IGroupBuildXmlService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * author zk
 * date 2021/2/24 18:01
 * description 群发组装xml（移动）
 */
@Component
public class GroupBuildXmlServiceImpl implements IGroupBuildXmlService {

    private static final Configuration configuration = new Configuration();

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    static {
        configuration.setClassForTemplateLoading(BuildXmlServiceImpl.class, "/template/group");
    }

    /**
     * author zk
     * date 2021/2/25 9:16
     * description 构建destinationAddress
     */
    private String buildDestinationAddress(ArrayNode arrayNode) {
        arrayNode = PhoneHelpUtil.filter(arrayNode, 1);
        StringBuilder sb = new StringBuilder();
        arrayNode.forEach(item -> sb.append("<destinationAddress>" + item.asText() + "</destinationAddress>"));
        return sb.toString();
    }

    private Map<String, Object> getCommonMap(JsonNode jsonNode) {
        Map<String, Object> map = new HashMap<>();

        ArrayNode arrayNode = (ArrayNode) jsonNode.get("userPhones");
        map.put("destinationAddress", buildDestinationAddress(arrayNode));

        map.put("senderAddress", jsonNode.get("chatBotId").asText()); // 注意，这里指的是maap的
        map.put("senderName", "name");
        map.put("subject", "text");
        map.put("conversationId", UUID.randomUUID().toString());
        map.put("contributionId", UUID.randomUUID().toString());

        if (jsonNode.has("backType")) {
            // 判断回落类型
            int backType = jsonNode.get("backType").asInt();
            map.put("fallbackSupported", backType == 3); // 回落类型 0（不回落） 1（回落h5） 2（回落普通短信） 3（UP1.0）
            map.put("rcsBodyText", jsonNode.get("smsContent").asText());
        } else {
            map.put("fallbackSupported", false);
            map.put("rcsBodyText", "");
        }
        return map;
    }

    @Override
    public String buildMessage(JsonNode message) {
        try {
            Map<String, Object> map = getCommonMap(message);
            Template template = configuration.getTemplate("textPure.xml");

            if (message.has("suggestions")) {
                template = configuration.getTemplate("textReply.xml");
                ObjectNode objectNode = sObjectMapper.createObjectNode();
                objectNode.set("suggestions", message.get("suggestions"));
                map.put("suggestion", objectNode);
            }

            map.put("text", message.get("content").asText());
            StringWriter out = new StringWriter();
            template.process(map, out);
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public String buildSingleCard(JsonNode message) {
        return buildManyCard(message);
    }

    @Override
    public String buildManyCard(JsonNode message) {

        try {
            Map<String, Object> map = getCommonMap(message);
            Template template = configuration.getTemplate("cardMany.xml");
            ArrayNode userPhones = (ArrayNode) message.get("userPhones");

            if (message.has("suggestions")) {
                template = configuration.getTemplate("cardManyReply.xml");

                ObjectNode objectNode = sObjectMapper.createObjectNode();
                objectNode.set("suggestions", message.get("suggestions"));
                map.put("suggestion", objectNode);
            }

            if (message.has("content")) {
                ArrayNode arrayNode = (ArrayNode) message.get("content");
                map.put("cardMany", getBodyTextJson(arrayNode));
            }

            StringWriter out = new StringWriter();
            template.process(map, out);
            return out.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public String buildFile(JsonNode message) {

        try {
            Map<String, Object> map = getCommonMap(message);
            Template template = configuration.getTemplate("file.xml");

            if (message.has("suggestions")) {
                template = configuration.getTemplate("fileReply.xml");
                ObjectNode objectNode = sObjectMapper.createObjectNode();
                objectNode.set("suggestions", message.get("suggestions"));
                map.put("suggestion", objectNode);
            }

            if (message.has("content")) {
                JsonNode jsonNode = message.get("content").get(0);
                // 只取file
                JsonNode fileJson = jsonNode.get("file");

                map.put("fileType", fileJson.get("contentType").asText());
                map.put("fileUrl", fileJson.get("url").asText());
                map.put("fileSize", fileJson.get("fileSize").asText());
            }

            StringWriter out = new StringWriter();
            template.process(map, out);
            return out.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String getBodyTextJson(ArrayNode cards) throws IOException, TemplateException {
        Template template = configuration.getTemplate("cardContent.json");
        StringWriter out1 = new StringWriter();
        Map<String, Object> map = new HashMap<>();

        List<Map<String, Object>> list = new ArrayList<>();

        cards.forEach(item -> {
            Map<String, Object> m = new HashMap<>();

            String title = "";
            String description = "";

            if (item.has("title")) {
                title = item.get("title").asText();
            }

            if (item.has("description")) {
                description = item.get("description").asText();
            }

            if (item.has("suggestions")) {
                m.put("suggestions", item.get("suggestions"));
            } else {
                m.put("suggestions", sObjectMapper.createArrayNode());
            }

            String urlThumb = "";
            String contentTypeThumb = "";
            String fileSizeThumb = "0";

            JsonNode file = item.get("file");

            if (item.has("thumb")) {
                JsonNode thumb = item.get("thumb");
                urlThumb = thumb.get("url").asText();
                contentTypeThumb = thumb.get("contentType").asText();
                fileSizeThumb = thumb.get("fileSize").asText();
            } else {
                urlThumb = file.get("url").asText();
                contentTypeThumb = file.get("contentType").asText();
                fileSizeThumb = file.get("fileSize").asText();
            }

            String urlFile = file.get("url").asText();
            String contentTypeFile = file.get("contentType").asText();
            String fileSizeFile = file.get("fileSize").asText();

            m.put("title", title);
            m.put("description", description);


            m.put("urlThumb", urlThumb);
            m.put("contentTypeThumb", contentTypeThumb);
            m.put("fileSizeThumb", fileSizeThumb);

            m.put("urlFile", urlFile);
            m.put("contentTypeFile", contentTypeFile);

            list.add(m);
        });

        map.put("UrlLists", list);
        template.process(map, out1);
        return out1.toString();
    }

    private String buildSingleCardContent(ArrayNode card) throws IOException, TemplateException {

        Template template = configuration.getTemplate("singleCard.json");
        StringWriter out1 = new StringWriter();
        Map<String, Object> map = new HashMap<>();

        JsonNode item = card.get(0);

        Map<String, Object> m = new HashMap<>();

        String title = item.get("title").asText();
        String description = item.get("description").asText();

        if (item.has("suggestions")) {
            m.put("suggestions", item.get("suggestions"));
        } else {
            m.put("suggestions", sObjectMapper.createArrayNode());
        }

        String urlThumb = "";
        String contentTypeThumb = "";
        JsonNode file = item.get("file");

        if (item.has("thumb")) {
            JsonNode thumb = item.get("thumb");
            urlThumb = thumb.get("url").asText();
            contentTypeThumb = thumb.get("contentType").asText();
        } else {
            urlThumb = file.get("url").asText();
            contentTypeThumb = file.get("contentType").asText();
        }

        String urlFile = file.get("url").asText();
        String contentTypeFile = file.get("contentType").asText();

        m.put("title", title);
        m.put("description", description);

        m.put("thumbnailUrl", urlThumb);
        m.put("thumbnailContentType", contentTypeThumb);

        m.put("mediaUrl", urlFile);
        m.put("mediaContentType", contentTypeFile);

        map.put("card", m);
        template.process(map, out1);
        return out1.toString();
    }
}
