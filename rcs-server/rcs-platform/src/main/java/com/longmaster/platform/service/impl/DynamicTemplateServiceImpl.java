package com.longmaster.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.util.PhoneHelpUtil;
import com.longmaster.platform.dto.dynamicTemplate.*;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.DynamicTemplate;
import com.longmaster.platform.entity.Material;
import com.longmaster.platform.entity.MaterialAuditRecord;
import com.longmaster.platform.mapper.ChatbotMapper;
import com.longmaster.platform.mapper.DynamicTemplateMapper;
import com.longmaster.platform.mapper.MaterialAuditRecordMapper;
import com.longmaster.platform.mapper.MaterialMapper;
import com.longmaster.platform.service.DynamicTemplateService;
import com.longmaster.platform.util.UrlUtil;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DynamicTemplateServiceImpl extends ServiceImpl<DynamicTemplateMapper, DynamicTemplate> implements DynamicTemplateService {


    @Resource
    private ChatbotMapper chatbotMapper;

    @Resource
    private MaterialMapper materialMapper;

    @Resource
    private MaterialAuditRecordMapper materialAuditRecordMapper;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    // 贵健康和医共体因为在广西移动，需要把渠道号转成5
    List<Long> appList = Arrays.asList(1368077132038148098L, 1375335328021250049L);

    private static String rebuildPayload(String payload, JsonNode body, JsonNode suggestion) throws JsonProcessingException {
        if (body != null) {
            JsonNodeType bodyNodeType = body.getNodeType();
            if (bodyNodeType == JsonNodeType.OBJECT) {
                payload = replaceParams(payload, sObjectMapper.readValue(body.toString(), Map.class));
            } else if (bodyNodeType == JsonNodeType.ARRAY) {
                // 说明需要copy卡片
                JsonNode tree = sObjectMapper.readTree(payload);
                ArrayNode content = (ArrayNode) tree.get("content");
                ObjectNode baseNode = (ObjectNode) content.get(0);

                for (int i = 0; i < body.size(); i++) {
                    JsonNode node = content.get(i);
                    if (node == null) {
                        node = baseNode;
                        content.add(node);
                    }
                    Map map = sObjectMapper.readValue(body.get(i).toString(), Map.class);
                    String string = node.toString();
                    string = replaceParams(string, map);
                    content.set(i, sObjectMapper.readTree(string));
                }
                payload = sObjectMapper.writeValueAsString(tree);
            }
        }

        if (suggestion != null && !"null".equals(suggestion.asText())) {
            JsonNodeType suggestionNodeType = suggestion.getNodeType();
            if (suggestionNodeType == JsonNodeType.OBJECT) {
                payload = replaceParams(payload, sObjectMapper.readValue(suggestion.toString(), Map.class));
            } else if (suggestionNodeType == JsonNodeType.ARRAY) {

                // 说明需要生成多个悬浮菜单
                JsonNode tree = sObjectMapper.readTree(payload);
                ArrayNode suggestions = (ArrayNode) tree.get("suggestions");
                ObjectNode baseNode = (ObjectNode) suggestions.get(0);

                for (int i = 0; i < suggestion.size(); i++) {
                    JsonNode node = suggestions.get(i);
                    if (node == null) {
                        node = baseNode;
                        suggestions.add(node);
                    }
                    Map map = sObjectMapper.readValue(suggestion.get(i).toString(), Map.class);
                    String string = node.toString();
                    string = replaceParams(string, map);
                    suggestions.set(i, sObjectMapper.readTree(string));
                }
                payload = sObjectMapper.writeValueAsString(tree);
            }
        } else {
            // 不使用按钮，直接移除
            ObjectNode tree = (ObjectNode) sObjectMapper.readTree(payload);
            tree.remove("suggestions");

            payload = sObjectMapper.writeValueAsString(tree);
        }
        return payload;
    }

    private static String replaceParams(String payload, Map<String, Object> params) {
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            payload = payload.replace("{{" + key + "}}", (String) params.get(key));
        }
        return payload;
    }

    @Override
    public IPage<DynamicTemplateDTO> pageQuery(PageParam<DynamicTemplateQueryDTO> pageParam, String carrierIds) throws JsonProcessingException {
        IPage records = baseMapper.pageSelect(pageParam.getPage(), pageParam.getParams());
        List<DynamicTemplateDTO> list = records.getRecords();
        for (DynamicTemplateDTO template : list) {
            Integer type = template.getType();

            JsonNode payload = sObjectMapper.readTree(template.getPayload());
            List<Long> collect = Arrays.stream(carrierIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
            template.setAuditRecords(new ArrayList<>()); // 赋空值
            switch (type) {
                case 2:
                case 3:
                    // 获取所有素材id
                    List<Long> ids = new ArrayList<>();
                    JsonNode content = payload.get("content");
                    for (JsonNode node : content) {
                        long file = node.get("file").asLong();
                        if (file > 0L) {
                            ids.add(file);
                        }
                    }
                    if (ids.size() > 0) {
                        template.setAuditRecords(materialAuditRecordMapper.getAuditStatusByIds(ids, collect));
                    }
                    break;
                case 5:
                case 6:
                case 7:
                    long materialId = payload.get("content").asLong();
                    if (materialId > 0L) {
                        template.setAuditRecords(materialAuditRecordMapper.getAuditStatusByIds(Arrays.asList(materialId), collect));
                    }
            }
        }
        return records;
    }

    @Override
    public Object sendMessage(Long id, List<String> phones, JsonNode body, JsonNode suggestion, boolean isMessageBack) throws IOException, TemplateException {
        DynamicTemplate dynamicTemplate = this.getById(id);
        Map<Object, Object> result = new HashMap<>();
        if (dynamicTemplate != null) {

            String payload = dynamicTemplate.getPayload();
            if (!StrUtil.isEmpty(payload)) {
                payload = rebuildPayload(payload, body, suggestion);

                Map<Integer, List<String>> phoneMap = new HashMap<>();

                // 手机号分渠道
                for (String phone : phones) {
                    Integer carrier = PhoneHelpUtil.isChinaMobilePhoneNum(phone);
                    if (carrier == 1 && appList.contains(dynamicTemplate.getAppId())) {
                        carrier = 5; // 如果移动的号码从贵健康、医共体发消息，改到广西移动
                    }
                    List<String> list = phoneMap.get(carrier);
                    if (list == null) {
                        List<String> arrayList = new ArrayList<>();
                        arrayList.add(phone);
                        phoneMap.put(carrier, arrayList);
                    } else {
                        list.add(phone);
                    }
                }

                Iterator<Integer> iterator = phoneMap.keySet().iterator();
                while (iterator.hasNext()) {
                    Integer carrierId = iterator.next();

                    Chatbot chatbot = chatbotMapper
                            .selectOne(new LambdaQueryWrapper<Chatbot>()
                                    .eq(Chatbot::getAppId, dynamicTemplate.getAppId())
                                    .eq(Chatbot::getCarrierId, Long.parseLong(String.valueOf(carrierId))).select(Chatbot::getChatBotId));

                    if (chatbot != null) {
                        ObjectNode data = (ObjectNode) sObjectMapper.readTree(payload);

                        switch (dynamicTemplate.getType()) {
                            case 2:
                            case 3:
                                // 替换content中的file
                                JsonNode content = data.get("content");
                                for (JsonNode node : content) {
                                    long materialId = node.get("file").asLong();

                                    ((ObjectNode) node).set("file", buildMaterial(materialId, chatbot.getChatBotId(), isMessageBack));

                                    Material material = materialMapper.selectOne(new LambdaQueryWrapper<Material>().eq(Material::getId, materialId));
                                    if (material != null) {
                                        Long thumbId = material.getThumbId();
                                        if (thumbId > 0L) {
                                            ((ObjectNode) node).set("thumb", buildMaterial(thumbId, chatbot.getChatBotId(), isMessageBack));
                                        }
                                    }
                                }
                                break;
                            case 5:
                            case 6:
                            case 7:
                                // 替换content
                                ArrayNode arrayNode = sObjectMapper.createArrayNode();
                                ObjectNode objectNode = sObjectMapper.createObjectNode();

                                long materialId = data.get("content").asLong();
                                objectNode.set("file", buildMaterial(materialId, chatbot.getChatBotId(), isMessageBack));

                                Material material = materialMapper.selectOne(new LambdaQueryWrapper<Material>().eq(Material::getId, materialId));
                                if (material != null) {
                                    Long thumbId = material.getThumbId();
                                    if (thumbId > 0L) {
                                        objectNode.set("thumb", buildMaterial(thumbId, chatbot.getChatBotId(), isMessageBack));
                                    }
                                }
                                arrayNode.add(objectNode);
                                data.set("content", arrayNode);
                        }
                        data.set("userPhones", sObjectMapper.readTree(sObjectMapper.writeValueAsString(phoneMap.get(carrierId))));
                        data.put("chatBotId", chatbot.getChatBotId());
                        data.put("carrierId", carrierId);
                        data.put("backType", 0);
                        data.put("smsContent", "");
                        data.put("isMessageBack", isMessageBack);

                        String messageId = rocketMQTemplate.sendAndReceive("RcsMessageSendTaskTopic:TaskTag", data, String.class);
                        log.info("RcsMessageSendTaskTopic:TaskTag，data：{}，result：{}", data, messageId);

                        result.put(phoneMap.get(carrierId), messageId);
                        return result;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public KeysBodyDTO getAllKeys(Long id) throws JsonProcessingException {
        KeysBodyDTO body = new KeysBodyDTO();
        DynamicTemplate dynamicTemplate = this.getById(id);
        Assert.notNull(dynamicTemplate, new ServerException("未找到模板信息，id：%s", id));
        ObjectNode payload = (ObjectNode) sObjectMapper.readTree(dynamicTemplate.getPayload());

        if (payload.has("suggestions")) {
            JsonNode suggestions = payload.get("suggestions");
            body.setSuggestionKeys(getKeys(suggestions.toString()).stream().distinct().collect(Collectors.toList()));
        }
        payload.remove("suggestions");
        // 获取其他的
        body.setBodyKeys(getKeys(payload.toString()).stream().distinct().collect(Collectors.toList()));
        return body;
    }

    @Override
    public DynamicTemplateDTO item(Long id) {
        return baseMapper.item(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(DynamicTemplate entity) {
        String payload = entity.getPayload();
        try {
            PayloadDTO value = sObjectMapper.readValue(payload, PayloadDTO.class);
            entity.setPayload(sObjectMapper.writeValueAsString(value));
            return this.save(entity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(DynamicTemplate entity) {
        String payload = entity.getPayload();
        try {
            PayloadDTO value = sObjectMapper.readValue(payload, PayloadDTO.class);
            entity.setPayload(sObjectMapper.writeValueAsString(value));
            return this.updateById(entity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void interactionMessage(InteractionDynamicTemplateBodyDTO dynamicTemplateBody) throws JsonProcessingException {
        Long id = dynamicTemplateBody.getTemplateId();
        JsonNode body = dynamicTemplateBody.getBody();
        JsonNode suggestion = dynamicTemplateBody.getSuggestion();
        boolean isMessageBack = dynamicTemplateBody.isMessageBack();
        String chatBotId = dynamicTemplateBody.getChatBotId();

        DynamicTemplate dynamicTemplate = this.getById(id);
        if (dynamicTemplate != null) {

            String payload = dynamicTemplate.getPayload();
            if (!StrUtil.isEmpty(payload)) {
                payload = rebuildPayload(payload, body, suggestion);

                ObjectNode message = (ObjectNode) sObjectMapper.readTree(payload);

                switch (dynamicTemplate.getType()) {
                    case 2:
                    case 3:
                        // 替换content中的file
                        JsonNode content = message.get("content");
                        for (JsonNode node : content) {
                            long materialId = node.get("file").asLong();

                            ((ObjectNode) node).set("file", buildMaterial(materialId, chatBotId, isMessageBack));

                            Material material = materialMapper.selectOne(new LambdaQueryWrapper<Material>().eq(Material::getId, materialId));
                            if (material != null) {
                                Long thumbId = material.getThumbId();
                                if (thumbId > 0L) {
                                    ((ObjectNode) node).set("thumb", buildMaterial(thumbId, chatBotId, isMessageBack));
                                }
                            }
                        }
                        break;
                    case 5:
                    case 6:
                    case 7:
                        // 替换content
                        ArrayNode arrayNode = sObjectMapper.createArrayNode();
                        ObjectNode objectNode = sObjectMapper.createObjectNode();

                        long materialId = message.get("content").asLong();
                        objectNode.set("file", buildMaterial(materialId, chatBotId, isMessageBack));

                        Material material = materialMapper.selectOne(new LambdaQueryWrapper<Material>().eq(Material::getId, materialId));
                        if (material != null) {
                            Long thumbId = material.getThumbId();
                            if (thumbId > 0L) {
                                objectNode.set("thumb", buildMaterial(thumbId, chatBotId, isMessageBack));
                            }
                        }
                        arrayNode.add(objectNode);
                        message.set("content", arrayNode);
                }
                message.put("backType", 0);
                message.put("smsContent", "");

                // 构建data
                ObjectNode data = sObjectMapper.createObjectNode();

                // 构建messages
                ArrayNode messages = sObjectMapper.createArrayNode();
                messages.add(message);
                data.set("messages", messages);

                // 拼接info
                ObjectNode info = sObjectMapper.createObjectNode();
                info.put("contributionId", dynamicTemplateBody.getContributionId());
                info.put("conversationId", dynamicTemplateBody.getConversationId());
                info.put("userPhone", dynamicTemplateBody.getPhone());
                info.put("chatBotId", chatBotId);


                data.put("isMessageBack", isMessageBack);
                data.set("info", info);

                // 构建root
                ObjectNode root = sObjectMapper.createObjectNode();
                root.set("data", data);
                root.put("mid", UUID.randomUUID().toString());

                rocketMQTemplate.asyncSendOrderly("RcsMessageSendTopic", root, dynamicTemplateBody.getPhone(), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        try {
                            log.info("生产：{}，sendResult：{}", root, sObjectMapper.writeValueAsString(sendResult));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });
            }
        }
    }

    private JsonNode buildMaterial(long materialId, String chatBotId, boolean isMessageBack) {
        ObjectNode objectNode = sObjectMapper.createObjectNode();

        MaterialAuditRecord fileRecord = materialAuditRecordMapper.selectMaterialAuditRecord(materialId, chatBotId);

        if (fileRecord == null) {
            throw new ServerException("存在未审核的素材，不能发送消息：chatBotId：" + chatBotId + "，素材id：" + materialId);
        }

        if (isMessageBack) {
            Material one = materialMapper.selectOne(new LambdaQueryWrapper<Material>().eq(Material::getId, materialId).select(Material::getSourceUrl));
            objectNode.put("url", UrlUtil.buildMinIoURL(one.getSourceUrl()));
        } else {
            objectNode.put("url", fileRecord.getMaterialUrl());
        }

        objectNode.put("contentType", fileRecord.getContentType());
        objectNode.put("fileSize", fileRecord.getFileSize());
        objectNode.put("until", fileRecord.getUntil());
        objectNode.put("fileName", fileRecord.getFileName());
        return objectNode;
    }

    // 获取模板内容中的动态参数key
    private List<String> getKeys(String content) {
        Pattern pattern = Pattern.compile("\\{\\{[\\w]*\\}\\}");
        Matcher matcher = pattern.matcher(content);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            String e = matcher.group();
            list.add(e.replace("{{", "").replace("}}", ""));
        }
        return list;
    }

}
