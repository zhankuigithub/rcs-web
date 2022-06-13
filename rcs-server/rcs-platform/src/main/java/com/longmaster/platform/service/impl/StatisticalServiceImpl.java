package com.longmaster.platform.service.impl;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.threadpool.AsyncTaskThreadManager;
import com.longmaster.core.util.DateUtil;
import com.longmaster.core.util.MathUtil;
import com.longmaster.platform.dto.group.LogTaskSendEventDTO;
import com.longmaster.platform.dto.message.MaapMessage;
import com.longmaster.platform.dto.receive.LogReceiveMessageDTO;
import com.longmaster.platform.dto.statistical.*;
import com.longmaster.platform.entity.*;
import com.longmaster.platform.enums.ContentTypeEnum;
import com.longmaster.platform.mapper.*;
import com.longmaster.platform.service.*;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticalServiceImpl implements StatisticalService {

    @Resource
    private StatisticalMapper statisticalMapper;

    @Resource
    private LogTaskSendEventMapper logTaskSendEventMapper;

    @Resource
    private LogTaskSendEventService logTaskSendEventService;

    @Resource
    private LogMessageTemplateEventMapper logMessageTemplateEventMapper;

    @Resource
    private LogReceiveMessageMapper logReceiveMessageMapper;

    @Resource
    private LogSceneMessageMapper logSceneMessageMapper;

    @Resource
    private ChatbotMapper chatbotMapper;

    @Resource
    private MenuAuditRecordMapper menuAuditRecordMapper;

    @Resource
    private ApplicationMapper applicationMapper;

    @Resource
    private CarrierMapper carrierMapper;

    @Resource
    private ChatbotService chatbotService;

    @Resource
    private LogReceiveMessageService logReceiveMessageService;

    @Resource
    private MessageTemplateService messageTemplateService;

    @Resource
    private LogMessageTemplateEventService logMessageTemplateEventService;

    @Resource
    private PayloadDataService payloadDataService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private static final String TABLE_STAT_GROUP_MESSAGE_EVENT = "t_log_task_send_event";
    private static final String TABLE_STAT_MENU_EVENT = "t_log_receive_message";
    private static final String TABLE_STAT_MESSAGE_TEMPLATE_EVENT = "t_log_message_template_event";
    private static final String TABLE_STAT_RECEIVE_EVENT = "t_log_receive_message";
    private static final String TABLE_STAT_SEND_EVENT = "t_log_send_message";
    private static final String TABLE_STAT_SCENE_EVENT = "t_log_scene_message";

    // 移动，电信一致
    private static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";
    private static final String CONTENT_TYPE_JSON = "application/vnd.gsma.botsuggestion.response.v1.0+json";
    private static final String CONTENT_TYPE_XML = "application/vnd.gsma.rcs-ft-http+xml";
    private static final String CONTENT_TYPE_CLIENT = "application/vnd.gsma.botsharedclientdata.v1.0+json";

    private static final String TYPE_SUG = "sug";
    private static final String TYPE_MENU = "menu";

    @Override
    public Map<String, Object> getHomeStatistical(Long customerId) {
        List<String> chatBotIds = chatbotMapper.getAllChatBotIdByCustomerId(customerId);
        return statisticalMapper.getHomeStatistical(chatBotIds);
    }

    @Override
    public void saveGroupEventMessage(Long taskId, String messageId, String name,
                                      String chatBotId, Long appId,
                                      String phoneNum, Long carrierId,
                                      Long messageTemplateId, String messageTemplateName,
                                      Integer messageTemplateType, Integer messageTemplateBackType, Integer status) {
        LogTaskSendEvent logTaskSendEvent = new LogTaskSendEvent();
        logTaskSendEvent.setTaskId(taskId);
        logTaskSendEvent.setMessageId(messageId);
        logTaskSendEvent.setName(name);
        logTaskSendEvent.setAppId(appId);
        logTaskSendEvent.setChatBotId(chatBotId);
        logTaskSendEvent.setPhoneNum(phoneNum);
        logTaskSendEvent.setCarrierId(carrierId);
        logTaskSendEvent.setMessageTemplateId(messageTemplateId);
        logTaskSendEvent.setMessageTemplateName(messageTemplateName);
        logTaskSendEvent.setMessageTemplateType(messageTemplateType);
        logTaskSendEvent.setStatus(status);
        logTaskSendEvent.setMessageTemplateBackType(messageTemplateBackType);
        logTaskSendEventService.save(logTaskSendEvent);
    }

    @Override
    public SataDTO getGroupEventStat(EventStatDTO groupEventStat) {
        SataDTO sata = new SataDTO();
        CruxDTO crux = statisticalMapper.getGroupEventStatCrux(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setCrux(crux);

        buildPercentageData(crux, statisticalMapper.getGroupPercentageData(groupEventStat.getAppIds()));

        List<TrendDTO> trends = statisticalMapper.getGroupEventStatTrend(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setTrends(buildTrendData(trends, groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt()));

        List<CarrierDTO> carriers = statisticalMapper.getGroupEventStatCarrier(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setCarriers(carriers);
        return sata;
    }

    @Override
    public SataDTO getMenuEventStat(EventStatDTO groupEventStat) {
        SataDTO sata = new SataDTO();

        CruxDTO crux = statisticalMapper.getMenuEventStatCrux(groupEventStat.getAppIds());
        sata.setCrux(crux);
        buildPercentageData(crux, statisticalMapper.getMenuPercentageData(groupEventStat.getAppIds()));

        List<TrendDTO> trends = statisticalMapper.getMenuEventStatTrend(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setTrends(buildTrendData(trends, groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt()));

        List<CarrierDTO> carriers = statisticalMapper.getMenuStatCarrier(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setCarriers(carriers);
        return sata;
    }

    @Override
    public JsonNode getAllMenuItems(MenuItemsSearchDTO menuItemsSearch) {

        List<Long> appIds = menuItemsSearch.getAppIds();
        List<Long> carrierIds = menuItemsSearch.getCarrierIds();

        ArrayNode arrayNode = sObjectMapper.createArrayNode();

        appIds.forEach(appId -> {
            carrierIds.forEach(carrierId -> {

                List<MenuItemClickEntity> items = logReceiveMessageMapper.selectAllMenuItemClickItems(appId, carrierId);

                Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getAppId, appId)
                        .eq(Chatbot::getCarrierId, carrierId)
                        .select(Chatbot::getId, Chatbot::getChatBotId, Chatbot::getCarrierId));
                if (chatbot != null) {
                    String latestPass = menuAuditRecordMapper.getLatestPass(chatbot.getId(), chatbot.getCarrierId());

                    if (!StrUtil.isEmpty(latestPass)) {
                        try {
                            JsonNode jsonNode = sObjectMapper.readTree(latestPass);
                            JsonNode entries = jsonNode.get("menu").get("entries");
                            resolveEntries(entries, items);

                            ((ObjectNode) jsonNode).put("appId", appId);
                            Application application = applicationMapper.selectOne(new LambdaQueryWrapper<Application>().eq(Application::getId, appId).select(Application::getName));
                            ((ObjectNode) jsonNode).put("appName", application.getName());

                            ((ObjectNode) jsonNode).put("carrierId", carrierId);
                            Carrier carrier = carrierMapper.selectOne(new LambdaQueryWrapper<Carrier>().eq(Carrier::getId, carrierId).select(Carrier::getName));
                            ((ObjectNode) jsonNode).put("carrierName", carrier.getName());

                            arrayNode.add(jsonNode);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
        return arrayNode;
    }


    private void resolveEntries(JsonNode entries, List<MenuItemClickEntity> items) {
        if (entries.getNodeType() == JsonNodeType.ARRAY) {
            entries.forEach(entry -> {
                if (entry.has("reply")) {
                    JsonNode reply = entry.get("reply");
                    Long payloadId = reply.get("postback").get("data").asLong();
                    PayloadData payloadData = payloadDataService.getById(payloadId); // 从t_payload_data表中获取菜单id
                    Long id = payloadData.getMenuId();

//                        JsonNode dataNode = sObjectMapper.readTree(dataStr);
//                        Long id = dataNode.get("id").asLong();

                    Optional<MenuItemClickEntity> optional = items.stream().filter(a -> a.getMenuId().intValue() == id.intValue()).findFirst();

                    if (optional.isPresent()) {
                        ((ObjectNode) reply).put("clickCnt", optional.get().getClickCnt());
                        ((ObjectNode) reply).put("clickUsers", optional.get().getClickUsers());
                        ((ObjectNode) reply).put("clickAverage", optional.get().getClickAverage());
                    } else {
                        ((ObjectNode) reply).put("clickCnt", 0);
                        ((ObjectNode) reply).put("clickUsers", 0);
                        ((ObjectNode) reply).put("clickAverage", 0);
                    }

                }

                if (entry.has("menu")) {
                    JsonNode menu = entry.get("menu");
                    if (menu.has("entries")) {
                        resolveEntries(menu.get("entries"), items);
                    }
                }
            });
        }
    }

    @Override
    public SataDTO getMessageTemplateEventStat(EventStatDTO groupEventStat) {
        SataDTO sata = new SataDTO();
        CruxDTO crux = statisticalMapper.getMessageTemplateStatCrux(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setCrux(crux);

        buildPercentageData(crux, statisticalMapper.getMessageTemplatePercentageData(groupEventStat.getAppIds()));

        List<TrendDTO> trends = statisticalMapper.getMessageTemplateStatTrend(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setTrends(buildTrendData(trends, groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt()));

        List<CarrierDTO> carriers = statisticalMapper.getMessageTemplateStatCarrier(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setCarriers(carriers);
        return sata;
    }

    @Override
    public SataDTO getReceiveEventStat(EventStatDTO groupEventStat) {
        SataDTO sata = new SataDTO();
        CruxDTO crux = statisticalMapper.getReceiveEventStatCrux(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setCrux(crux);

        buildPercentageData(crux, statisticalMapper.getReceivePercentageData(groupEventStat.getAppIds()));

        List<TrendDTO> trends = statisticalMapper.getReceiveEventStatTrend(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setTrends(buildTrendData(trends, groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt()));

        List<CarrierDTO> carriers = statisticalMapper.getReceiveEventStatCarrier(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setCarriers(carriers);
        return sata;
    }

    @Override
    public SataDTO getSceneEventStat(EventStatDTO groupEventStat) {
        SataDTO sata = new SataDTO();
        CruxDTO crux = statisticalMapper.getSceneEventStatCrux(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setCrux(crux);

        buildPercentageData(crux, statisticalMapper.getScenePercentageData(groupEventStat.getAppIds()));

        List<TrendDTO> trends = statisticalMapper.getSceneEventStatTrend(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setTrends(buildTrendData(trends, groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt()));

        List<CarrierDTO> carriers = statisticalMapper.getSceneEventStatCarrier(groupEventStat.getAppIds(), groupEventStat.getType(), groupEventStat.getBeginDt(), groupEventStat.getEndDt());
        sata.setCarriers(carriers);
        return sata;
    }


    @Override
    public IPage<LogTaskSendEventDTO> groupItems(PageParam<SearchDTO> pageParam) {
        IPage<SearchDTO> page = pageParam.getPage();
        SearchDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        return logTaskSendEventMapper.selectItems(page, params);
    }

    @Override
    public IPage<TemplateEventItemsDTO> messageTemplateEventItems(PageParam<SearchDTO> pageParam) {
        IPage<SearchDTO> page = pageParam.getPage();
        SearchDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        return logMessageTemplateEventMapper.selectMessageTemplateEventItems(page, params);
    }

    @Override
    public IPage<LogReceiveMessageDTO> logReceiveMessageItems(PageParam<SearchUpDTO> pageParam) {
        IPage<SearchUpDTO> page = pageParam.getPage();
        SearchUpDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        return logReceiveMessageMapper.selectReceiveEventItems(page, params);
    }

    @Override
    public IPage<LogSceneMessage> logSceneMessageItems(PageParam<SearchUpDTO> pageParam) {
        IPage<SearchUpDTO> page = pageParam.getPage();
        SearchUpDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        return logSceneMessageMapper.selectSceneMessageItems(page, params);
    }

    @Override
    public void saveReceiveMessage(MaapMessage maapMessage) {
        AsyncTaskThreadManager.getInstance().submit(() -> {
            // 1. 记录上行消息
            try {
                saveLogReceiveMessageService(maapMessage);
                String contentType = maapMessage.getContentType();
                if (CONTENT_TYPE_JSON.equalsIgnoreCase(contentType)) {
                    saveLogMessageTemplateEventService(maapMessage.getMessageId(), maapMessage.getDestinationAddress(), maapMessage.getUserPhone(), sObjectMapper.readTree(maapMessage.getBodyText()));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public PageResult<Map<String, String>> selectSendAndReceiveLog(PageParam<Map<String, Object>> pageParam) {
        IPage<Map<String,String>> iPage = statisticalMapper.selectSendAndReceiveLog(pageParam.getPage(), pageParam.getParams());
        return new PageResult<>(iPage.getTotal(), iPage.getRecords());
    }

    @Override
    public PageResult<Map<String, String>> selectChatLog(PageParam<Map<String, Object>> pageParam) {
        IPage<Map<String, String>> iPage = statisticalMapper.selectChatLog(pageParam.getPage(), pageParam.getParams());
        return new PageResult<>(iPage.getTotal(), iPage.getRecords());
    }

    private void buildPercentageData(CruxDTO cruxDTO, Map<String, Object> map) {
        if (map != null) {
            double userCnt1 = Double.valueOf((Long) map.get("user_cnt_1")); // 今天的
            double userCnt2 = Double.valueOf((Long) map.get("user_cnt_2")); // 昨天的

            double userTimes1 = Double.valueOf((Long) map.get("user_times_1")); // 今天的
            double userTimes2 = Double.valueOf((Long) map.get("user_times_2")); // 昨天的

            // 人数-变动率
            if (userCnt1 == 0 && userCnt2 == 0) {
                cruxDTO.setUserCntPercentage("+0%");
            } else if (userCnt1 == 0 && userCnt2 > 0) {
                cruxDTO.setUserCntPercentage("-100%");
            } else if (userCnt1 > 0 && userCnt2 == 0) {
                cruxDTO.setUserCntPercentage("+" + MathUtil.mul(userCnt1, 100) + "%");
            } else {
                double div = MathUtil.div(MathUtil.sub(userCnt1, userCnt2), userCnt2);
                cruxDTO.setUserCntPercentage((div > 0 ? "+" : "") + MathUtil.mul(div, 100) + "%");
            }

            // 次数-变动率
            if (userTimes1 == 0 && userTimes2 == 0) {
                cruxDTO.setUserTimesPercentage("+0%");
            } else if (userTimes1 == 0 && userTimes2 > 0) {
                cruxDTO.setUserTimesPercentage("-100%");
            } else if (userTimes1 > 0 && userTimes2 == 0) {
                cruxDTO.setUserTimesPercentage("+" + MathUtil.mul(userTimes1, 100) + "%");
            } else {
                double div = MathUtil.div(MathUtil.sub(userTimes1, userTimes2), userTimes2);
                cruxDTO.setUserTimesPercentage((div > 0 ? "+" : "") + MathUtil.mul(div, 100) + "%");
            }

            // 平均点击数-变动率

            double data1 = (userTimes1 > 0 && userCnt1 > 0) ? MathUtil.div(userTimes1, userCnt1) : 0;
            double data2 = (userTimes2 > 0 && userCnt2 > 0) ? MathUtil.div(userTimes2, userCnt2) : 0;

            if (data1 == 0 && data2 == 0) {
                cruxDTO.setUserAveragePercentage("+0%");
            } else if (data1 == 0 && data2 > 0) {
                cruxDTO.setUserAveragePercentage("-100%");
            } else if (data1 > 0 && data2 == 0) {
                cruxDTO.setUserAveragePercentage("+" + MathUtil.mul(data1, 100) + "%");
            } else {
                double div = MathUtil.div(MathUtil.sub(data1, data2), data2);
                cruxDTO.setUserAveragePercentage((div > 0 ? "+" : "") + MathUtil.mul(div, 100) + "%");
            }

        } else {
            cruxDTO.setUserCntPercentage("+0%");
            cruxDTO.setUserTimesPercentage("+0%");
            cruxDTO.setUserAveragePercentage("+0%");
        }
    }

    private List<TrendDTO> buildTrendData(List<TrendDTO> trends, Integer type, String beginDt, String endDt) {
        List<String> list = new ArrayList<>();
        switch (type) {
            case 1:
                list = DateUtil.getDaysBetween(beginDt, endDt);
                break;
            case 2:
                list = DateUtil.getWeekBetween(beginDt, endDt);
                break;
            case 3:
                list = DateUtil.getMonthBetween(beginDt, endDt);
                break;
            case 4:
                list = DateUtil.getYearBetween(beginDt, endDt);
                break;
        }

        List<String> collect = trends.stream().map(TrendDTO::getStatDt).collect(Collectors.toList());

        list.forEach(item -> {
            if (!collect.contains(item)) {
                TrendDTO trend = new TrendDTO();
                trend.setStatDt(item);
                trend.setUserAverage(0.00);
                trend.setUserCnt(0);
                trend.setUserTimes(0);
                trends.add(trend);
            }
        });

        return trends.stream().sorted(Comparator.comparing(TrendDTO::getStatDt)).collect(Collectors.toList());
    }

    // 记录上行消息表
    private void saveLogReceiveMessageService(MaapMessage maapMessage) throws JsonProcessingException {
        LogReceiveMessage logReceiveMessage = new LogReceiveMessage();
        logReceiveMessage.setMessageId(maapMessage.getMessageId()); // messageId
        logReceiveMessage.setChatBotId(maapMessage.getDestinationAddress()); // destinationAddress
        logReceiveMessage.setPhoneNum(maapMessage.getUserPhone()); // senderAddress
        logReceiveMessage.setContent(sObjectMapper.writeValueAsString(maapMessage));
        logReceiveMessage.setIsKeyword(maapMessage.getHasKeyword() ? 1 : 0);

        Integer type = ContentTypeEnum.getTypeByMode(maapMessage.getContentType());
        if (type == 1) { // 记录解析后的内容
            logReceiveMessage.setPayload(URLDecoder.decode(maapMessage.getBodyText(), Charset.defaultCharset()));

        } else if (type == 2) { // 区分菜单还是普通按钮
            String bodyText = maapMessage.getBodyText();
            JsonNode jsonNode = sObjectMapper.readTree(bodyText);
            JsonNode reply = jsonNode.get("response").get("reply");

            String data = reply.get("postback").get("data").asText(); // 必须先转string
            JsonNode dataNode = sObjectMapper.readTree(data);
            String sugType = dataNode.get("type").asText();

            // 保存点击文案
            logReceiveMessage.setPayload(reply.get("displayText").asText());
            // {"reply":{"displayText":"发起会诊","postback":{"data":"{\"type\":\"menu\",\"id\":\"1212305792918500\",\"mid\":\"1390484970570563585\"}"}}}
            // {"reply":{"displayText":"会诊列表","postback":{"data":"{\"type\":\"menu\",\"id\":\"1212305792588400\",\"action\":\"sbc_consultation_list\"}"}}}
            // {"reply":{"displayText":"2","postback":{"data":"{\"type\":\"sug\",\"id\":\"1405400133435674625\",\"action\":\"\"}"}}}
            // {"reply":{"displayText":"1","postback":{"data":"{\"type\":\"sug\",\"id\":\"1405400132521316354\",\"action\":\"sbc_1\"}"}}}

            if (TYPE_MENU.equals(sugType)) {
                type = 5;
                // 保存点击菜单
                logReceiveMessage.setMenuId(dataNode.get("id").asLong());
            } else {
                String action = dataNode.get("action").asText();
                if (!StrUtil.isEmpty(action)) {
                    type = 6;
                }
            }
        } else if (type == 3) {
            logReceiveMessage.setPayload(sObjectMapper.writeValueAsString(maapMessage.getMaapFile()));
        } else if (type == 4) {
            logReceiveMessage.setPayload(maapMessage.getBodyText());
        }

        logReceiveMessage.setContentType(type);

        Chatbot chatbot = chatbotService.getOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, maapMessage.getDestinationAddress()).select(Chatbot::getAppId, Chatbot::getCarrierId));
        if (chatbot != null) {
            logReceiveMessage.setAppId(chatbot.getAppId());
            logReceiveMessage.setCarrierId(chatbot.getCarrierId());
            logReceiveMessageService.save(logReceiveMessage);
        }
    }

    // 记录点击事件表
    private void saveLogMessageTemplateEventService(String messageId, String chatBotId, String phone, JsonNode payLoad) {
        try {

            String data = payLoad.get("response").get("reply").get("postback").get("data").asText(); // 原始类型为string，必须先转换一次
            JsonNode dataTree = sObjectMapper.readTree(data);

            String type = dataTree.get("type").asText();
            if (TYPE_MENU.equals(type) || TYPE_SUG.equals(type)) {
                if (dataTree.has("id")) {
                    Long sugId = dataTree.get("id").asLong();
                    if (dataTree.has("mid")) {
                        Long messageTemplateId = dataTree.get("mid").asLong();
                        MessageTemplate messageTemplate = messageTemplateService.getOne(new LambdaQueryWrapper<MessageTemplate>().eq(MessageTemplate::getId, messageTemplateId).select(MessageTemplate::getName));

                        LogMessageTemplateEvent logMessageTemplateEvent = new LogMessageTemplateEvent();
                        logMessageTemplateEvent.setMessageId(messageId);
                        logMessageTemplateEvent.setMessageTemplateId(messageTemplateId);
                        logMessageTemplateEvent.setMessageTemplateName(messageTemplate.getName());
                        logMessageTemplateEvent.setChatBotId(chatBotId);
                        logMessageTemplateEvent.setPhoneNum(phone);
                        logMessageTemplateEvent.setSugId(sugId);
                        logMessageTemplateEvent.setType(TYPE_MENU.equals(type) ? 1 : 2);
                        Chatbot chatbot = chatbotService.getOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getChatBotId, chatBotId).select(Chatbot::getAppId, Chatbot::getCarrierId));

                        if (chatbot != null) {
                            logMessageTemplateEvent.setAppId(chatbot.getAppId());
                            logMessageTemplateEvent.setCarrierId(chatbot.getCarrierId());
                            logMessageTemplateEventService.save(logMessageTemplateEvent);
                        }
                    }
                }
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

}
