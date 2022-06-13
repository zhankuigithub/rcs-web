package com.longmaster.rcs.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.rcs.service.IBuildXmlService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BuildXmlServiceImpl implements IBuildXmlService {

    private static final Configuration configuration = new Configuration();

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    static {
        configuration.setClassForTemplateLoading(BuildXmlServiceImpl.class, "/template/alone");
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


    private Map<String, Object> getCommonMap(JsonNode jsonNode) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", jsonNode.get("userPhone").asText());
        map.put("senderAddress", jsonNode.get("chatBotId").asText()); // 注意，这里指的是maap的
        map.put("senderName", "name");
        map.put("subject", "text");
        map.put("conversationId", jsonNode.get("conversationId").asText());
        map.put("contributionId", jsonNode.get("contributionId").asText());
        map.put("inReplyToContributionId", jsonNode.get("contributionId").asText());
        // 判断回落类型
        int backType = jsonNode.has("backType") ? jsonNode.get("backType").asInt() : 0;
        map.put("fallbackSupported", backType == 3); //回落类型 0（不回落） 1（回落h5） 2（回落普通短信） 3（UP1.0）
        map.put("rcsBodyText", jsonNode.has("smsContent") ? jsonNode.get("smsContent").asText() : "");
        return map;
    }

    private String getBodyTextJson(ArrayNode cards) throws IOException, TemplateException {
        Template template = configuration.getTemplate("cardContent.json");
        StringWriter out1 = new StringWriter();
        Map<String, Object> map = new HashMap<>();

        List<Map<String, Object>> list = new ArrayList<>();

        for (JsonNode item : cards) {
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
                JsonNode jsonNode = item.get("suggestions");
                m.put("suggestions", jsonNode);
            } else {
                m.put("suggestions", sObjectMapper.createArrayNode());
            }

            JsonNode file = item.get("file");

            String urlThumb = "";
            String contentTypeThumb = "";
            String fileSizeThumb = "0";
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
            //String fileSizeFile = file.get("fileSize").asText();

            m.put("title", title);
            m.put("description", description);


            m.put("urlThumb", urlThumb);
            m.put("contentTypeThumb", contentTypeThumb);
            m.put("fileSizeThumb", fileSizeThumb);

            m.put("urlFile", urlFile);
            m.put("contentTypeFile", contentTypeFile);

            list.add(m);
        }

        map.put("UrlLists", list);
        template.process(map, out1);
        return out1.toString();
    }
}
