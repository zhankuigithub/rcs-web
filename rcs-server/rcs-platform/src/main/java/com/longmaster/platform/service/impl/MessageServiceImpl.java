package com.longmaster.platform.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.threadpool.AsyncTaskThreadManager;
import com.longmaster.core.util.PhoneHelpUtil;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.cache.CacheHelper;
import com.longmaster.platform.cache.GlobalCacheKeyDefine;
import com.longmaster.platform.dto.card.CardDTO;
import com.longmaster.platform.dto.event.AddPushEventDTO;
import com.longmaster.platform.dto.message.MaapMessage;
import com.longmaster.platform.dto.message.MessageDTO;
import com.longmaster.platform.dto.message.NotifyBodyDTO;
import com.longmaster.platform.entity.*;
import com.longmaster.platform.enums.CardHeightEnum;
import com.longmaster.platform.enums.MessageTemplateOrientationEnum;
import com.longmaster.platform.enums.MessageTemplateWidthEnum;
import com.longmaster.platform.mapper.*;
import com.longmaster.platform.service.*;
import com.longmaster.platform.util.RSAUtil;
import com.longmaster.platform.util.UrlUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * author zk
 * date 2021/2/22 16:31
 * description 关键词获取消息模板
 */
@Slf4j
@Component
@RefreshScope
public class MessageServiceImpl implements MessageService {

    @Resource
    private ApplicationService applicationService;

    @Resource
    private ChatbotService chatbotService;

    @Resource
    private KeywordReplyConfigService keywordReplyConfigService;

    @Resource
    private MessageTemplateMapper messageTemplateMapper;

    @Resource
    private SuggestionItemMapper suggestionItemMapper;

    @Resource
    private CardMapper cardMapper;

    @Resource
    private MaterialAuditRecordMapper materialAuditRecordMapper;

    @Resource
    private ChatbotMapper chatbotMapper;

    @Resource
    private CustomService customService;

    @Resource
    private RedisService redisService;

    @Resource
    private SceneNodeService sceneService;

    @Resource
    private StatisticalService statisticalService;

    @Resource
    private PushEventService pushEventService;

    @Resource
    private MaterialService materialService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RestTemplate balancedRestTemplate;

    @Value("${notify.reg-record-url}")
    private String regRecordUrl;

    @Value("${notify.im-des-url}")
    private String imDesUrl;

    @Value("${notify.phi-order-list-url}")
    private String phiOrderListUrl;

    @Resource
    private PayloadDataService payloadDataService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";
    private static final String CONTENT_TYPE_JSON = "application/vnd.gsma.botsuggestion.response.v1.0+json";
    private static final String CONTENT_TYPE_XML = "application/vnd.gsma.rcs-ft-http+xml";
    private static final String CONTENT_TYPE_CLIENT = "application/vnd.gsma.botsharedclientdata.v1.0+json";
    private static final String SPECIAL_TAG = "sbc_menu_guidance"; // 智能导致特殊标志
    private static final String SPECIAL_REG = "医生$"; // 智能导致特殊标志

    private static final String MENU = "menu";
    private static final String SUG = "sug";

    private static final List<String> DEFAULT_MSGS = Arrays.asList("new_bot_user_initiation_zh", "new_bot_user_initiation");

    private static final Configuration configuration = new Configuration();

    static {
        configuration.setClassForTemplateLoading(MessageServiceImpl.class, "/static/");
    }

    // source素材id的map
    private static Map<String, Map<String, Long>> sourceMaterialMap = new HashMap() {
        {
            put("10024", new HashMap() {
                        {
                            put("1", 1439100449702592513L); // 1. 预约挂号
                            put("2", 1439124771980754946L); // 2. 取消挂号
                            put("3", 1449273009500164098L); // 3. 图文
                            put("4", 1449273009500164098L); // 4. 医生回复（同3）
                            put("5", 1438326252374114305L); // 5. 体检
                        }
                    }
            ); // 贵象健康
            put("10019", new HashMap() {
                        {
                            put("1", 1374549134421377025L); // 1. 预约挂号
                            put("2", 1374549055958532098L); // 2. 取消挂号
                            put("3", 1427554637197656066L); // 3. 图文
                            put("4", 1427554637197656066L); // 4. 医生回复（同3）
                            put("5", 1403164664424087553L); // 5. 体检
                        }
                    }
            ); // 贵健康
        }
    };

    @Value("${system.rcsSmc.server}")
    private String smcServerUrl;

    @Override
    public void receiveCustom(JsonNode jsonNode) {
        // 基础信息
        ObjectNode info = sObjectMapper.createObjectNode();
        info.put("contributionId", jsonNode.get("contributionId").asText());
        info.put("conversationId", jsonNode.get("conversationId").asText());
        info.put("userPhone", jsonNode.get("userPhone").asText());
        info.put("chatBotId", jsonNode.get("chatBotId").asText());

        ObjectNode result = sObjectMapper.createObjectNode(); // 最终放入队列的
        result.put("isMessageBack", jsonNode.has("isMessageBack") && jsonNode.get("isMessageBack").asBoolean());
        result.set("info", info);

        // 消息信息
        ArrayNode messages = sObjectMapper.createArrayNode();
        messages.add(jsonNode);
        result.set("messages", messages);

        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("mid", UUID.randomUUID().toString());
        objectNode.set("data", result);

        rocketMQTemplate.asyncSendOrderly("RcsMessageSendTopic", objectNode, jsonNode.get("userPhone").asText(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                try {
                    log.info("生产：{}，sendResult：{}", objectNode, sObjectMapper.writeValueAsString(sendResult));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }

    public String createFileUrl(Integer source, Integer modal, String chatBotId) {
        Long materialId = sourceMaterialMap.get(String.valueOf(source)).get(String.valueOf(modal));

        if (materialId > 0) {
            MaterialAuditRecord one = materialAuditRecordMapper.selectOne(new LambdaQueryWrapper<MaterialAuditRecord>()
                    .eq(MaterialAuditRecord::getChatBotId, chatBotId)
                    .eq(MaterialAuditRecord::getMaterialId, materialId)
                    .select(MaterialAuditRecord::getMaterialUrl));
            if (one != null) {
                return one.getMaterialUrl();
            }
        }
        return "";
    }

    @Override
    public String notifyWebapi(NotifyBodyDTO notifyBody) throws IOException, TemplateException {
        String phone = notifyBody.getPhone();
        String chatBotId = notifyBody.getChatBotId();
        Integer carrierId = PhoneHelpUtil.isChinaMobilePhoneNum(phone);

        Map<String, Object> map = new HashMap();
        map.put("phone", phone);
        map.put("chatBotId", chatBotId);
        map.put("carrierId", carrierId);
        map.put("type", carrierId == 1 ? 3 : 2); // 移动发多卡片；其他推单卡片

        Chatbot one = chatbotService.getOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, chatBotId).select(Chatbot::getAppId));
        Application application = applicationService.getById(one.getAppId());
        String appName = application.getName();
        Integer modal = notifyBody.getModal();

        String title = "";
        String displayText = "";
        String description = "";
        String openUrl = "";

        JsonNode params = notifyBody.getParams();
        switch (modal) {
            case 1:
                NotifyBodyDTO.RegSuccess regSuccess = sObjectMapper.readValue(params.toString(), NotifyBodyDTO.RegSuccess.class);
                title = "预约挂号成功";
                displayText = "查看详情";

                // 构建description
                description = "{0}提醒：{1}您已预约{2}-{3}-{4}-{5}-({6}号)号源，流水号:{7}，请于当天{8}到院就诊。";
                description = MessageFormat.format(description, appName,
                        regSuccess.getPatientName(), // 患者1
                        regSuccess.getHospitalName(), // 医院2
                        regSuccess.getHdeptName(), // 科室3
                        regSuccess.getExpertName(), // 医生4
                        DateUtil.format(DateUtil.date(regSuccess.getShiftDate() * 1000), "MM月dd日HH:mm:ss"), // 5时间
                        regSuccess.getIgsOrder(),// 多少号6
                        DateUtil.format(DateUtil.date(regSuccess.getShiftDate() * 1000), "yyyyMMdd") + "-" + regSuccess.getIgSerialNumber(), //流水号7
                        DateUtil.format(DateUtil.offset(DateUtil.date(regSuccess.getShiftDate() * 1000), DateField.MINUTE, -30), "HH:mm:ss") // 提前半小时8
                );
                openUrl = regRecordUrl + regSuccess.getSource() + "&info=" + RSAUtil.getLoginInfo(phone, chatBotId);

                break;
            case 2:
                NotifyBodyDTO.RegSuccess regSuccess2 = sObjectMapper.readValue(params.toString(), NotifyBodyDTO.RegSuccess.class);
                title = "取消预约挂号成功";
                displayText = "查看详情";

                // 构建description
                description = "{0}提醒：{1}您已成功取消{2}-{3}-{4}-{5}-({6}号)号源的预约。";
                description = MessageFormat.format(description, appName,
                        regSuccess2.getPatientName(), // 患者1
                        regSuccess2.getHospitalName(), // 医院2
                        regSuccess2.getHdeptName(), // 科室3
                        regSuccess2.getExpertName(), // 医生4
                        DateUtil.format(DateUtil.date(regSuccess2.getShiftDate() * 1000), "MM月dd日HH:mm:ss"), // 5时间
                        regSuccess2.getIgsOrder()// 多少号6
                );
                openUrl = regRecordUrl + regSuccess2.getSource() + "&info=" + RSAUtil.getLoginInfo(phone, chatBotId);
                break;
            case 3:
                NotifyBodyDTO.ImSuccess imSuccess = sObjectMapper.readValue(params.toString(), NotifyBodyDTO.ImSuccess.class);

                title = "图文问诊支付成功";
                displayText = "立即进入问诊";

                description = appName + "提醒：图文问诊下单成功，欢迎进入问诊";
                openUrl = imDesUrl + imSuccess.getSource() + "?inquiry_id" + imSuccess.getInquiryId() + "&info=" + RSAUtil.getLoginInfo(phone, chatBotId);
                break;

            case 4:
                NotifyBodyDTO.ImSuccess imSuccess2 = sObjectMapper.readValue(params.toString(), NotifyBodyDTO.ImSuccess.class);

                title = "医生回复提醒";
                displayText = "立即进入问诊";

                description = appName + "提醒：医生" + imSuccess2.getDocName() + "已回复了您的问诊：" + imSuccess2.getContent();
                openUrl = imDesUrl + imSuccess2.getSource() + "?inquiry_id" + imSuccess2.getInquiryId() + "&info=" + RSAUtil.getLoginInfo(phone, chatBotId);
                break;

            case 5:
                NotifyBodyDTO.PhysicalSuccess physicalSuccess = sObjectMapper.readValue(params.toString(), NotifyBodyDTO.PhysicalSuccess.class);
                title = "预约成功提醒";
                displayText = "查看订单";

                description = appName + "提醒：您的预约体检订单已下单成功！";
                openUrl = phiOrderListUrl + physicalSuccess.getSource() + "?inquiry_id" + physicalSuccess.getInquiryId() + "&info=" + RSAUtil.getLoginInfo(phone, chatBotId);
                break;

        }
        map.put("title", title);
        map.put("displayText", displayText);
        map.put("description", description);
        map.put("openUrl", openUrl);
        map.put("fileUrl", createFileUrl(params.get("source").asInt(), modal, chatBotId));

        Template template = configuration.getTemplate("notify.json");
        StringWriter sw = new StringWriter();
        template.process(map, sw);
        String json = sw.toString();
        JsonNode jsonNode = sObjectMapper.readTree(json);

        try {
            return rocketMQTemplate.sendAndReceive("RcsMessageSendTaskTopic:NotifyTag", jsonNode, String.class);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String notifySmc(JsonNode jsonNode) {
        try {
            return rocketMQTemplate.sendAndReceive("RcsMessageSendTaskTopic:NotifyTag", jsonNode, String.class);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void sendMessageByMsgTmpId(MessageDTO request) {
        ObjectNode info = sObjectMapper.createObjectNode();
        info.put("contributionId", UUID.randomUUID().toString());
        info.put("conversationId", UUID.randomUUID().toString());
        info.put("userPhone", request.getUserPhone());
        info.put("chatBotId", request.getChatBotId());

        ObjectNode result = sObjectMapper.createObjectNode(); // 最终放入队列的
        result.put("isMessageBack", request.getIsMessageBack());
        result.set("info", info);

        // 消息信息
        ArrayNode messages = sObjectMapper.createArrayNode();

        Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, request.getChatBotId()));
        if (chatbot == null) {
            return;
        }
        ObjectNode objectNode = buildMessage(request.getMessageTmpId(), chatbot.getAppId(), request.getChatBotId(), request.getUserPhone(), request.getIsMessageBack());
        if (objectNode == null) {
            return;
        }
        messages.add(objectNode);
        result.set("messages", messages);

        ObjectNode msg = sObjectMapper.createObjectNode();
        msg.put("mid", UUID.randomUUID().toString());
        msg.set("data", result);

        rocketMQTemplate.asyncSendOrderly("RcsMessageSendTopic", msg, request.getUserPhone(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                try {
                    log.info("生产：{}，sendResult：{}", msg, sObjectMapper.writeValueAsString(sendResult));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }

    @Override
    public ArrayNode getMsgTmpByKeyword(Chatbot chatbot, String keyword, String userPhone, boolean isMessageBack) {
        List<KeywordReplyConfig> list = keywordReplyConfigService.getKeyword(chatbot.getAppId(), keyword);
        if (list == null || list.size() == 0) {
            return null;
        }
        ArrayNode arrayNode = sObjectMapper.createArrayNode();
        String finalUserPhone = userPhone;
        list.forEach(item -> {
            Long replyId = item.getReplyId(); // 回复的模板id
            ObjectNode data = buildMessage(replyId, chatbot.getAppId(), chatbot.getChatBotId(), finalUserPhone, isMessageBack);
            data.put("carrierId", chatbot.getCarrierId());
            arrayNode.add(data);
        });
        return arrayNode;
    }

    private void transformData(MaapMessage message) throws JsonProcessingException {
        String contentType = message.getContentType();
        if (CONTENT_TYPE_JSON.contains(contentType)) {
            String payload = message.getBodyText();
            JsonNode jsonNode = sObjectMapper.readTree(payload);
            String data = jsonNode.get("response").get("reply").get("postback").get("data").asText();
            if (DEFAULT_MSGS.contains(data)) {
                // 电信首次进入chatbot
                message.setContentType(CONTENT_TYPE_TEXT);
                message.setBodyText("你好");
            } else {
                String response = payloadDataService.rebuildPayLoad(jsonNode.get("response").toString());
                ObjectNode objectNode = sObjectMapper.createObjectNode();
                objectNode.set("response", sObjectMapper.readTree(response)); // 保持这个格式，其他地方都已约束好了
                message.setBodyText(objectNode.toString());
            }
        }
    }

    private ObjectNode resolveScene(String payload, String userPhone, Long appId, Long carrierId, String chatBotId, boolean isMessageBack) {
        // 判断是否处于场景
        Long currentSceneId = sceneService.getCurrentSceneId(userPhone);
        Long messageTemplateId;
        if (currentSceneId != null) {
            messageTemplateId = sceneService.getMessageTemplateInScene(appId, carrierId, userPhone, payload, currentSceneId);
        } else {
            messageTemplateId = sceneService.findSceneToGetMsgTmpId(appId, carrierId, payload, userPhone);
        }
        return buildMessage(messageTemplateId, appId, chatBotId, userPhone, isMessageBack);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result sendGroupMessage(AddPushEventDTO pushEvent) {
        List<Long> carrierIds = pushEvent.getCarrierIds();
        Long appId = pushEvent.getAppId();
        Long templateId = pushEvent.getTemplateId();
        List<String> userPhones = pushEvent.getUserPhones();
        String name = pushEvent.getName();

        // 记录整个事件
        MessageTemplate template = messageTemplateMapper.selectById(templateId);

        PushEvent event = new PushEvent();
        event.setAppId(appId);
        event.setCarrierIds(Joiner.on(",").join(carrierIds));
        event.setPhoneNums(Joiner.on(",").join(userPhones));
        event.setCustomerId(pushEvent.getCustomerId());
        event.setMessageTemplateId(templateId);
        event.setMessageTemplateName(template.getName());
        event.setMessageTemplateType(template.getType());
        event.setMessageTemplateBackType(template.getBackType());
        event.setName(name);
        event.setRemark(pushEvent.getRemark());
        event.setSendDt(pushEvent.getSendDt());
        event.setSendStatus(PushEvent.SEND_STATUS_INIT);
        boolean save = pushEventService.save(event);

        if (save) {
            Long id = event.getId();
            MessageTemplate messageTemplate = messageTemplateMapper.selectOne(new LambdaQueryWrapper<MessageTemplate>()
                    .eq(MessageTemplate::getId, templateId)
                    .eq(MessageTemplate::getAppId, appId).select(MessageTemplate::getName, MessageTemplate::getType, MessageTemplate::getBackType));

            for (Long carrierId : carrierIds) {
                if (carrierId.intValue() == Carrier.LONGMASTER.intValue()) {
                    userPhones.forEach(item -> statisticalService.saveGroupEventMessage(id, "", name, "", appId, item,
                            carrierId, templateId, messageTemplate.getName(), messageTemplate.getType(), messageTemplate.getBackType(), 0));
                } else {
                    //过滤对应渠道商的号码

                    List<String> list = PhoneHelpUtil.filter(userPhones, carrierId.intValue());
                    list.forEach(item -> statisticalService.saveGroupEventMessage(id, "", name, "", appId, item,
                            carrierId, templateId, messageTemplate.getName(), messageTemplate.getType(), messageTemplate.getBackType(), 0));
                }
            }
        }
        return Result.SUCCESS();
    }


    /**
     * author zk
     * date 2021/2/24 15:34
     * description 通过模板id生成标准的消息json
     */
    @Override
    public ObjectNode buildMessage(Long templateId, Long appId, String chatBotId, String userPhone, boolean isMessageBack) {
        ObjectNode data = sObjectMapper.createObjectNode();

        MessageTemplate messageTemplate = messageTemplateMapper.selectOne(new LambdaQueryWrapper<MessageTemplate>().eq(MessageTemplate::getId, templateId).eq(MessageTemplate::getAppId, appId));

        if (messageTemplate == null) {
            return null;
        }

        String sugIds = messageTemplate.getSugIds();
        if (!StrUtil.isEmpty(sugIds)) {
            data.set("suggestions", buildSuggestions(sugIds, userPhone, templateId, chatBotId)); // 获取悬浮菜单
        }

        Integer type = messageTemplate.getType(); //1文本消息，2单卡片，3多卡片，4地理消息 5图片 6音频 7视频
        data.put("type", type);
        data.put("backType", messageTemplate.getBackType());
        data.put("smsContent", messageTemplate.getSmsContent());
        switch (type) {
            case MessageTemplate.TYPE_TEXT:
            case MessageTemplate.TYPE_MANY_LOCATION:
                data.put("content", messageTemplate.getPayload());
                break;

            case MessageTemplate.TYPE_IMAGE:
            case MessageTemplate.TYPE_AUDIO:
            case MessageTemplate.TYPE_VIDEO:
                data.set("content", buildFile(messageTemplate.getPayload(), chatBotId, isMessageBack));
                break;
            case MessageTemplate.TYPE_SINGLE_CARD:
            case MessageTemplate.TYPE_MANY_CARD:
                data.set("content", buildCard(messageTemplate.getPayload(), chatBotId, userPhone, templateId, isMessageBack));
                ObjectNode layout = sObjectMapper.createObjectNode();
                layout.put("width", MessageTemplateWidthEnum.geModeByType(messageTemplate.getWidth()));
                layout.put("orientation", MessageTemplateOrientationEnum.geModeByType(messageTemplate.getOrientation()));
                data.set("layout", layout);
                break;
        }
        return data;
    }

    @Override
    public void saveSendQueue(MaapMessage message) {
        try {
            // 转化格式
            transformData(message);
            boolean isMessageBack = message.getMessageBack(); // 是否为回落消息对话
            String bodyText = message.getBodyText();
            String chatBotId = message.getDestinationAddress();
            String contentType = message.getContentType();
            String userPhone = message.getUserPhone();

            String key = CacheHelper.buildChatBotCacheKey(GlobalCacheKeyDefine.CHAT_BOT_INFO, chatBotId);
            Chatbot chatbot = redisService.unblockingGet(key, 60 * 60L, () ->
                    chatbotService.getOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, chatBotId).
                            select(Chatbot::getAppId, Chatbot::getCarrierId, Chatbot::getChatBotId)));

            if (chatbot == null) {
                return;
            }

            // 基础信息
            ObjectNode info = sObjectMapper.createObjectNode();
            info.put("contributionId", message.getContributionId());
            info.put("conversationId", message.getConversationId());
            info.put("userPhone", message.getUserPhone());
            info.put("chatBotId", message.getDestinationAddress());

            ObjectNode result = sObjectMapper.createObjectNode(); // 最终放入队列的
            result.set("info", info);

            // 消息信息
            ArrayNode arrayNode = sObjectMapper.createArrayNode();
            result.set("messages", arrayNode);
            result.put("isMessageBack", isMessageBack);

            // 建议回复时
            if (CONTENT_TYPE_JSON.contains(contentType)) {
                JsonNode response = sObjectMapper.readTree(bodyText).get("response");
                String data = response.get("reply").get("postback").get("data").asText();
                String type = sObjectMapper.readTree(data).get("type").asText();

                // 菜单类型时
                if (MENU.equals(type)) {
                    String dataJson = sObjectMapper.readTree(bodyText).get("response").get("reply").get("postback").get("data").asText(); // 原始类型为string
                    JsonNode jsonNode = sObjectMapper.readTree(dataJson);

                    // 消息模板时，存在key为mid
                    if (jsonNode.has("mid")) {
                        long mid = sObjectMapper.readTree(dataJson).get("mid").asLong();
                        arrayNode.add(buildMessage(mid, chatbot.getAppId(), chatBotId, userPhone, isMessageBack));
                        sendToQueue(result, userPhone);
                        return;
                    }
                    // 特殊处理时，存在key为action
                    if (jsonNode.has("action")) {
                        AsyncTaskThreadManager.getInstance().submit(() -> customService.sendToCustom(message));
                        return;
                    }
                    return;
                }

                // 悬浮按钮
                if (SUG.equals(type)) {
                    String dataJson = sObjectMapper.readTree(bodyText).get("response").get("reply").get("postback").get("data").asText(); // 原始类型为string
                    JsonNode jsonNode = sObjectMapper.readTree(dataJson);

                    String action = jsonNode.get("action").asText();
                    // action不为空时特殊处理
                    if (!StrUtil.isEmpty(action)) {
                        AsyncTaskThreadManager.getInstance().submit(() -> customService.sendToCustom(message));
                        return;
                    }
                    // 执行找场景
                    arrayNode.add(resolveScene(jsonNode.get("id").asText(), userPhone, chatbot.getAppId(), chatbot.getCarrierId(), chatbot.getChatBotId(), isMessageBack));
                    //  写队列
                    sendToQueue(result, userPhone);
                }
            } else if (CONTENT_TYPE_TEXT.contains(contentType)) {

                // 如果当前存在智能导诊
                String opAction = redisService.get(GlobalCacheKeyDefine.OP_ACTION + "_" + userPhone);
                if (!StrUtil.isEmpty(opAction) && opAction.contains(SPECIAL_TAG)) {
                    AsyncTaskThreadManager.getInstance().submit(() -> customService.sendToCustom(message));
                    return;
                }
                // 执行找场景
                ObjectNode node = resolveScene(bodyText, userPhone, chatbot.getAppId(), chatbot.getCarrierId(), chatbot.getChatBotId(), isMessageBack);
                if (node != null) {
                    message.setHasKeyword(true);
                    arrayNode.add(node);
                    //  写队列
                    sendToQueue(result, userPhone);
                    return;
                }
                // 关键词
                ArrayNode keyword = this.getMsgTmpByKeyword(chatbot, bodyText, userPhone, isMessageBack);
                if (keyword != null) {
                    message.setHasKeyword(true);
                    //  写队列
                    result.set("messages", keyword);
                    sendToQueue(result, userPhone);
                    return;
                }
                // 匹配模式
                if (Pattern.compile(SPECIAL_REG).matcher(bodyText).find()) {
                    AsyncTaskThreadManager.getInstance().submit(() -> customService.sendToCustom(message));
                    return;
                }
                return;
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            // 保存上行消息
            statisticalService.saveReceiveMessage(message);
        }
    }

    /**
     * author zk
     * date 2021/2/22 18:03
     * description 获取按钮json格式+
     */
    private ArrayNode buildSuggestions(String sugIds, String userPhone, Long templateId, String chatBotId) {

        ArrayNode arrayNode = sObjectMapper.createArrayNode();

        if (StrUtil.isEmpty(sugIds)) { // 返空数组，rcs那边拼空数组即可
            return arrayNode;
        }

        suggestionItemMapper
                .selectSuggestionItems(sugIds).stream()
                .sorted(Comparator.comparing(SuggestionItem::getWeight).reversed())
                .forEach(item -> {
                    try {
                        String payload = item.getPayload();

                        if (item.getType() == 1) { //建议回复类型的，重新构造payload
                            payload = payloadDataService.saveOrRebuildPayload(payload, templateId);
                        }

                        if (item.getType() == 2) { // 跳转链接，id获取链接
                            try {
                                JsonNode node = sObjectMapper.readTree(payload);
                                Long materialId = node.get("action").get("urlAction").get("openUrl").get("url").asLong();

                                if (materialId > 0L) {
                                    String key = CacheHelper.buildMaterialUrlCacheKey(GlobalCacheKeyDefine.MATERIAL_URL_ID, String.valueOf(materialId));

                                    String url = redisService.unblockingGet(key, 5 * 60L, () -> {
                                        Material material = materialService.getOne(new LambdaQueryWrapper<Material>()
                                                .eq(Material::getId, materialId)
                                                .select(Material::getOriginalUrl));

                                        if (material != null) {
                                            return material.getOriginalUrl();
                                        }

                                        return null;
                                    });

                                    if (!StrUtil.isEmpty(url)) {
                                        payload = payload.replaceAll(String.valueOf(materialId), url);  // 直接replace
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            // 贵健康
                            if (payload.contains("%s")) {
                                payload = payload.replaceAll("%s", RSAUtil.getLoginInfo(userPhone, chatBotId));
                            }
                            // 医共体
                            if (payload.contains("%a")) {
                                if (!StrUtil.isEmpty(userPhone)) {
                                    String key = CacheHelper.buildUserIdPhoneCacheKey(GlobalCacheKeyDefine.SMC_PHONE_USER_ID, userPhone);
                                    String userId = redisService.unblockingGet(key, 60 * 60L, () -> {
                                        String result = balancedRestTemplate.getForObject(smcServerUrl + "client/getUserId/" + userPhone, String.class);
                                        JsonNode node = null;
                                        try {
                                            node = sObjectMapper.readTree(result);
                                        } catch (JsonProcessingException e) {
                                            e.printStackTrace();
                                        }
                                        return node.get("data").asText();
                                    });
                                    if (!StrUtil.isEmpty(userId)) {
                                        payload = payload.replaceAll("%a", userId);
                                    }
                                }
                            }
                            // 朗玛通信的‘贵健康商用版’
                            if (payload.contains("%b")) {
                                payload = payload.replaceAll("%b", RSAUtil.getLmTxLoginInfo(userPhone, payload));
                            }
                        }
                        arrayNode.add(sObjectMapper.readValue(payload, JsonNode.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return arrayNode;
    }

    /**
     * author zk
     * date 2021/2/22 18:03
     * description 获取卡片
     */
    private ArrayNode buildCard(String cardId, String chatBotId, String userPhone, Long templateId, boolean isMessageBack) {
        ArrayNode arrayNode = sObjectMapper.createArrayNode();
        List<CardDTO> cards = cardMapper.selectCardDto(cardId);

        ObjectNode errorMessage = sObjectMapper.createObjectNode();

        for (Card item : cards) {
            ObjectNode objectNode = sObjectMapper.createObjectNode();

            objectNode.put("title", item.getTitle());
            objectNode.put("description", item.getDescription());
            objectNode.put("height", CardHeightEnum.geModeByType(item.getHeight()));

            // 卡片内的按钮
            if (!StrUtil.isEmpty(item.getSugIds())) { // 电信的卡片不允许存空按钮
                objectNode.set("suggestions", buildSuggestions(item.getSugIds(), userPhone, templateId, chatBotId));
            }

            // 组建卡片的素材
            Long thumbId = item.getThumbId();
            if (thumbId > 0L) {
                MaterialAuditRecord thumbMaterial = materialAuditRecordMapper.selectMaterialAuditRecord(thumbId, chatBotId);

                if (thumbMaterial == null) {
                    errorMessage.put("code", LogTaskSendEvent.STATUS_MATERIAL_NEVER_AUDIT);
                    errorMessage.put("message", "存在未审核的素材，不能发送消息：chatBotId：" + chatBotId + "，素材id：" + thumbId);
                    throw new ServerException(errorMessage.toString());
                }
                ObjectNode thumb = sObjectMapper.createObjectNode();

                Material one = materialService.getOne(new LambdaQueryWrapper<Material>().eq(Material::getId, thumbId).select(Material::getSourceUrl));
                if (one == null) {
                    throw new ServerException("素材id不存在：" + thumbId);
                }
                if (isMessageBack) {
                    thumb.put("url", UrlUtil.buildMinIoURL(one.getSourceUrl()));
                } else {
                    thumb.put("url", thumbMaterial.getMaterialUrl());
                }

                thumb.put("showUrl", UrlUtil.buildMinIoURL(one.getSourceUrl()));
                thumb.put("contentType", thumbMaterial.getContentType());
                thumb.put("fileSize", thumbMaterial.getFileSize());
                objectNode.set("thumb", thumb);
            }
            Long materialId = item.getMaterialId();
            MaterialAuditRecord fileMaterial = materialAuditRecordMapper.selectMaterialAuditRecord(materialId, chatBotId);
            ObjectNode file = sObjectMapper.createObjectNode();

            if (fileMaterial == null) {
                errorMessage.put("code", LogTaskSendEvent.STATUS_MATERIAL_NEVER_AUDIT);
                errorMessage.put("message", "存在未审核的素材，不能发送消息：chatBotId：" + chatBotId + "，素材id：" + materialId);
                throw new ServerException(errorMessage.toString());
            }
            Material one = materialService.getOne(new LambdaQueryWrapper<Material>().eq(Material::getId, materialId).select(Material::getSourceUrl));
            if (one == null) {
                throw new ServerException("素材id不存在：" + materialId);
            }

            if (isMessageBack) {
                file.put("url", UrlUtil.buildMinIoURL(one.getSourceUrl()));
            } else {
                file.put("url", fileMaterial.getMaterialUrl());
            }
            file.put("showUrl", UrlUtil.buildMinIoURL(one.getSourceUrl()));

            file.put("contentType", fileMaterial.getContentType());
            file.put("fileSize", fileMaterial.getFileSize());
            objectNode.set("file", file);
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }


    /**
     * author zk
     * date 2021/2/23 10:50
     * description 生成文件
     */
    private ArrayNode buildFile(String payload, String chatBotId, boolean isMessageBack) {
        String[] array = payload.split(",");
        ArrayNode arrayNode = sObjectMapper.createArrayNode();
        ObjectNode errorMessage = sObjectMapper.createObjectNode();

        if (array.length == 1) {
            // 文件
            long fileId = Long.parseLong(array[0]);
            ObjectNode objectNode = sObjectMapper.createObjectNode();
            MaterialAuditRecord fileRecord = materialAuditRecordMapper.selectMaterialAuditRecord(fileId, chatBotId);
            if (fileRecord == null) {
                errorMessage.put("code", LogTaskSendEvent.STATUS_MATERIAL_NEVER_AUDIT);
                errorMessage.put("message", "存在未审核的素材，不能发送消息：chatBotId：" + chatBotId + "，素材id：" + fileId);
                throw new ServerException(errorMessage.toString());
            }
            ObjectNode file = sObjectMapper.createObjectNode();

            Material one = materialService.getOne(new LambdaQueryWrapper<Material>().eq(Material::getId, fileId).select(Material::getSourceUrl));
            if (isMessageBack) {
                file.put("url", UrlUtil.buildMinIoURL(one.getSourceUrl()));
            } else {
                file.put("url", fileRecord.getMaterialUrl());
            }
            file.put("showUrl", UrlUtil.buildMinIoURL(one.getSourceUrl()));

            file.put("contentType", fileRecord.getContentType());
            file.put("fileSize", fileRecord.getFileSize());
            file.put("until", fileRecord.getUntil());
            file.put("fileName", fileRecord.getFileName());

            objectNode.set("file", file);
            arrayNode.add(objectNode);
        }

        if (array.length == 2) {
            Long thumbId = Long.parseLong(array[0]);
            Long fileId = Long.parseLong(array[1]);

            ObjectNode objectNode = sObjectMapper.createObjectNode();

            if (thumbId > 0L) {
                // 封面
                MaterialAuditRecord thumbRecord = materialAuditRecordMapper.selectMaterialAuditRecord(thumbId, chatBotId);

                if (thumbRecord == null) {
                    errorMessage.put("code", LogTaskSendEvent.STATUS_MATERIAL_NEVER_AUDIT);
                    errorMessage.put("message", "存在未审核的素材，不能发送消息：chatBotId：" + chatBotId + "，素材id：" + thumbId);
                    throw new ServerException(errorMessage.toString());
                }
                ObjectNode thumb = sObjectMapper.createObjectNode();

                Material one = materialService.getOne(new LambdaQueryWrapper<Material>().eq(Material::getId, thumbId).select(Material::getSourceUrl));
                if (one == null) {
                    throw new ServerException("素材id不存在：" + thumbId);
                }
                // 兼容虚拟终端
                if (isMessageBack) {
                    thumb.put("url", UrlUtil.buildMinIoURL(one.getSourceUrl()));
                } else {
                    thumb.put("url", thumbRecord.getMaterialUrl());
                }
                thumb.put("showUrl", UrlUtil.buildMinIoURL(one.getSourceUrl()));
                thumb.put("contentType", thumbRecord.getContentType());
                thumb.put("fileSize", thumbRecord.getFileSize());
                thumb.put("until", thumbRecord.getUntil());
                thumb.put("fileName", thumbRecord.getFileName());
                objectNode.set("thumb", thumb);
            }

            // 文件
            MaterialAuditRecord fileRecord = materialAuditRecordMapper.selectMaterialAuditRecord(fileId, chatBotId);

            if (fileRecord == null) {
                errorMessage.put("code", LogTaskSendEvent.STATUS_MATERIAL_NEVER_AUDIT);
                errorMessage.put("message", "存在未审核的素材，不能发送消息：chatBotId：" + chatBotId + "，素材id：" + fileId);
                throw new ServerException(errorMessage.toString());
            }

            ObjectNode file = sObjectMapper.createObjectNode();

            Material one = materialService.getOne(new LambdaQueryWrapper<Material>().eq(Material::getId, fileId).select(Material::getSourceUrl));
            if (one == null) {
                throw new ServerException("素材id不存在：" + fileId);
            }

            file.put("showUrl", UrlUtil.buildMinIoURL(one.getSourceUrl()));
            if (isMessageBack) {
                file.put("url", UrlUtil.buildMinIoURL(one.getSourceUrl()));
            } else {
                file.put("url", fileRecord.getMaterialUrl());
            }

            file.put("contentType", fileRecord.getContentType());
            file.put("fileSize", fileRecord.getFileSize());
            file.put("until", fileRecord.getUntil());
            file.put("fileName", fileRecord.getFileName());
            objectNode.set("file", file);
            arrayNode.add(objectNode);
        }

        return arrayNode;
    }

    /**
     * 保存发送队列
     *
     * @param arrayNode
     * @param userPhone
     */
    private void sendToQueue(JsonNode arrayNode, String userPhone) {

        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("mid", UUID.randomUUID().toString());
        objectNode.set("data", arrayNode);

        rocketMQTemplate.asyncSendOrderly("RcsMessageSendTopic", objectNode, userPhone, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                try {
                    log.info("生产：{}，sendResult：{}", objectNode, sObjectMapper.writeValueAsString(sendResult));
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
