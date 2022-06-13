package com.longmaster.rcs.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.longmaster.core.bean.maap.MaapFile;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.util.CommonUtil;
import com.longmaster.core.util.JacksonUtil;
import com.longmaster.rcs.Constants;
import com.longmaster.rcs.dto.maap.BodyTextDTO;
import com.longmaster.rcs.dto.maap.MessageDTO;
import com.longmaster.rcs.service.IRcsService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class RcsServiceImpl implements IRcsService {

    private static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";
    private static final String CONTENT_TYPE_JSON = "application/vnd.gsma.botsuggestion.response.v1.0+json";
    private static final String CONTENT_TYPE_XML = "application/vnd.gsma.rcs-ft-http+xml";
    private static final String CONTENT_TYPE_CLIENT = "application/vnd.gsma.botsharedclientdata.v1.0+json";

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public MaapMessage parseCm(String xml) {
        MaapMessage requestDTO = new MaapMessage();

        JsonNode jsonNode = JacksonUtil.transFromXmlToJson(xml);
        JsonNode inboundMessage = jsonNode.get("msg:inboundMessageNotification").get("inboundMessage");

        // 目前未使用到
        if (inboundMessage.has(Constants.Maap.SERVICE_CAPABILITY)) {


        }

        if (inboundMessage.has(Constants.Maap.DESTINATION_ADDRESS)) {
            requestDTO.setDestinationAddress(inboundMessage.get(Constants.Maap.DESTINATION_ADDRESS).asText());
        }

        if (inboundMessage.has(Constants.Maap.SENDER_ADDRESS)) {
            String sendAddress = inboundMessage.get(Constants.Maap.SENDER_ADDRESS).asText();

            requestDTO.setSenderAddress(sendAddress);
            requestDTO.setUserPhone(CommonUtil.filterPhone(sendAddress));
        }

        if (inboundMessage.has(Constants.Maap.ORIG_USER)) {
            requestDTO.setOrigUser(inboundMessage.get(Constants.Maap.ORIG_USER).asText());
        }

        if (inboundMessage.has(Constants.Maap.RESOURCE_URL)) {
            requestDTO.setResourceURL(inboundMessage.get(Constants.Maap.RESOURCE_URL).asText());
        }

        if (inboundMessage.has(Constants.Maap.CONTENT_TYPE)) {
            requestDTO.setContentType(inboundMessage.get(Constants.Maap.CONTENT_TYPE).asText());
        }

        if (inboundMessage.has(Constants.Maap.MESSAGE_ID)) {
            requestDTO.setMessageId(inboundMessage.get(Constants.Maap.MESSAGE_ID).asText());
        }

        if (inboundMessage.has(Constants.Maap.CONVERSATION_ID)) {
            requestDTO.setConversationId(inboundMessage.get(Constants.Maap.CONVERSATION_ID).asText());
        }

        if (inboundMessage.has(Constants.Maap.CONTRIBUTION_ID)) {
            requestDTO.setContributionId(inboundMessage.get(Constants.Maap.CONTRIBUTION_ID).asText());
        }

        if (inboundMessage.has(Constants.Maap.DATE_TIME)) {
            requestDTO.setDateTime(inboundMessage.get(Constants.Maap.DATE_TIME).asText());
        }

        if (inboundMessage.has(Constants.Maap.BODY_TEXT)) {
            if (inboundMessage.has(Constants.Maap.CONTENT_ENCODING) && "base64".equalsIgnoreCase(inboundMessage.get(Constants.Maap.CONTENT_ENCODING).asText())) {
                String bodyTextStr = new String(Base64.getDecoder()
                        .decode(inboundMessage.get(Constants.Maap.BODY_TEXT).asText().trim().getBytes(StandardCharsets.UTF_8)),
                        StandardCharsets.UTF_8);

                parseFile(requestDTO, bodyTextStr, inboundMessage.get(Constants.Maap.CONTENT_TYPE).asText());

                if (CONTENT_TYPE_JSON.equals(inboundMessage.get(Constants.Maap.CONTENT_TYPE).asText())) {
                    JsonNode node = null;
                    try {
                        node = sObjectMapper.readTree(bodyTextStr);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    if (node.get("response").has("reply")) {
                        requestDTO.setBodyText(bodyTextStr);
                    } else {
                        // {"response":{"action":{"displayText":"立即问医"}}}
                        return null;
                    }
                } else if (CONTENT_TYPE_TEXT.equals(inboundMessage.get(Constants.Maap.CONTENT_TYPE).asText())) {
                    requestDTO.setBodyText(bodyTextStr);
                }

            } else {
                parseFile(requestDTO, inboundMessage.get(Constants.Maap.BODY_TEXT).asText().trim(), inboundMessage.get(Constants.Maap.CONTENT_TYPE).asText());
                requestDTO.setBodyText(inboundMessage.get(Constants.Maap.BODY_TEXT).asText().trim());
            }
        }
        return requestDTO;
    }

    private void parseFile(MaapMessage requestDTO, String bodyText, String contentType) {

        if (contentType.equals(Constants.Maap.TYPE_FILE)) {

            JsonNode jsonNode = JacksonUtil.transFromXmlToJson(bodyText);
            JsonNode file = jsonNode.get("file");
            JsonNode fileInfo = file.get("file-info");

            if (fileInfo.getNodeType() == JsonNodeType.ARRAY) {
                ArrayNode arrayNode = (ArrayNode) fileInfo;
                for (JsonNode item : arrayNode) {
                    if ("file".equals(item.get("type").asText())) {
                        buildMaapFileDTO(requestDTO, item);
                    }
                }
            } else {
                buildMaapFileDTO(requestDTO, fileInfo);
            }
        }
    }

    private void buildMaapFileDTO(MaapMessage requestDTO, JsonNode jsonNode) {
        MaapFile fileDTO = new MaapFile();

        int fileSize = 0;

        if (jsonNode.has("file-size")) {
            fileSize = jsonNode.get("file-size").asInt();
            fileDTO.setFileSize(fileSize);
        }

        String fileName = "";
        if (jsonNode.has("file-name")) {
            fileName = jsonNode.get("file-name").asText();
            fileDTO.setFileName(fileName);
        }

        if (jsonNode.has("content-type")) {
            fileDTO.setContentType(jsonNode.get("content-type").asText());
        }

        String maapFileUrl = "";

        if (jsonNode.has("data")) {
            JsonNode dataJson = jsonNode.get("data");
            maapFileUrl = dataJson.get("url").asText();
            fileDTO.setUrl(maapFileUrl);
        }

        List files = new ArrayList();
        files.add(fileDTO);
        requestDTO.setMaapFile(files);
    }

    @Override
    public MaapMessage parseCt(String json) throws JsonProcessingException {
        MessageDTO messageDTO = sObjectMapper.readValue(json, MessageDTO.class);
        MaapMessage message = new MaapMessage();
        message.setDestinationAddress(messageDTO.getDestinationAddress());
        message.setConversationId(messageDTO.getConversationId());
        message.setContributionId(messageDTO.getContributionId());
        message.setUserPhone(messageDTO.getSenderAddress());
        message.setMessageId(messageDTO.getMessageId());

        List<BodyTextDTO> messageList = messageDTO.getMessageList();
        BodyTextDTO bodyTextDTO = messageList.get(0);
        String contentType = bodyTextDTO.getContentType();
        Object contentText = bodyTextDTO.getContentText();

        message.setContentType(contentType);

        if (contentType.contains("text/plain")) {
            message.setContentType("text/plain;charset=UTF-8");
            message.setBodyText(String.valueOf(contentText));
        } else if (contentType.contains("application/vnd.gsma.rcs-ft-http")) {
            List<LinkedHashMap> files = (List<LinkedHashMap>) contentText;
            List<MaapFile> list = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                MaapFile file = new MaapFile();
                file.setFileSize(Integer.parseInt(String.valueOf(files.get(i).get("fileSize"))));
                file.setContentType(String.valueOf(files.get(i).get("contentType")));
                file.setFileName(String.valueOf(files.get(i).get("fileName")));
                file.setUrl(String.valueOf(files.get(i).get("url")));
                list.add(file);
            }
            message.setMaapFile(list);
        } else {
            JsonNode node = sObjectMapper.readTree(sObjectMapper.writeValueAsString(contentText));
            if (node.get("response").has("reply")) {
                message.setBodyText(sObjectMapper.writeValueAsString(contentText));
            } else {
                return null;
            }
        }
        return message;
    }

    @Override
    public MaapMessage parseLm(String json) throws JsonProcessingException {
        MessageDTO messageDTO = sObjectMapper.readValue(json, MessageDTO.class);
        MaapMessage message = new MaapMessage();
        message.setMessageBack(true);
        message.setDestinationAddress(messageDTO.getDestinationAddress());
        message.setConversationId(messageDTO.getConversationId());
        message.setContributionId(messageDTO.getContributionId());
        message.setUserPhone(messageDTO.getSenderAddress());
        message.setMessageId(messageDTO.getMessageId());

        List<BodyTextDTO> messageList = messageDTO.getMessageList();
        BodyTextDTO bodyTextDTO = messageList.get(0);
        String contentType = bodyTextDTO.getContentType();
        Object contentText = bodyTextDTO.getContentText();

        message.setContentType(contentType);

        if (contentType.contains("text/plain")) {
            message.setContentType("text/plain;charset=UTF-8");
            message.setBodyText(String.valueOf(contentText));
        } else if ("application/vnd.gsma.rcs-ft-http+xml".equalsIgnoreCase(contentType)) {
            List<LinkedHashMap> files = (List<LinkedHashMap>) contentText;
            List<MaapFile> list = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                MaapFile file = new MaapFile();
                file.setFileSize(Integer.parseInt(String.valueOf(files.get(i).get("fileSize"))));
                file.setContentType(String.valueOf(files.get(i).get("contentType")));
                file.setFileName(String.valueOf(files.get(i).get("fileName")));
                file.setUrl(String.valueOf(files.get(i).get("url")));
                list.add(file);
            }
            message.setMaapFile(list);
        } else {
            message.setBodyText(sObjectMapper.writeValueAsString(contentText));
        }
        return message;
    }

}
