package com.longmaster.platform.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.util.SmsTemplateUtil;
import com.longmaster.platform.cache.GlobalCacheKeyDefine;
import com.longmaster.platform.dto.chatbot.ChatBotDTO;
import com.longmaster.platform.dto.chatbot.ChatBotWrapperDTO;
import com.longmaster.platform.entity.*;
import com.longmaster.platform.mapper.ChatbotMapper;
import com.longmaster.platform.service.*;
import com.longmaster.platform.util.UrlUtil;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 机器人信息表 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Slf4j
@Service
public class ChatbotServiceImpl extends ServiceImpl<ChatbotMapper, Chatbot> implements ChatbotService {

    @Resource
    private List<CarrierStrategy> carrierStrategy;

    @Resource
    private ApplicationService applicationService;

    @Resource
    private CustomerAuditRecordService customerAuditRecordService;

    @Resource
    private MaterialAuditRecordService materialAuditRecordService;

    @Resource
    private MenuAuditRecordService menuAuditRecordService;

    @Resource
    private RedisService redisService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Value("${sms.apiUrl}")
    private String smsApiUrl;

    @Value("${sms.appId}")
    private String appId;

    @Value("${sms.clientId}")
    private String clientId;

    @Value("${sms.clientKey}")
    private String clientKey;

    /**
     * 分页查询 机器人列表  包含 客户、运营商信息
     *
     * @return
     */
    @Override
    public IPage<ChatBotDTO> pageSelect(PageParam<ChatBotDTO> pageParam, String carrierIds) {
        IPage<ChatBotDTO> page = pageParam.getPage(ChatBotDTO.class);
        ChatBotDTO params = pageParam.getParams();
        return baseMapper.pageSelect(page, params, carrierIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveChatBot(ChatBotWrapperDTO wrapper) {
        //校验参数
        // this.validParams(wrapper);
        Long appId = wrapper.getApplication().getId();
        if (appId == null) {
            throw new ServerException("appId 不允许为空");
        }
        Application application = applicationService.getById(appId);
        Assert.notNull(application, new ServiceException("应用信息不匹配，无法创建机器人"));
        Assert.isTrue(application.getStatus() == 1, new ServiceException("当前应用还未通过审核，请先审核应用~"));

        List<Chatbot> createdList = list(new LambdaQueryWrapper<Chatbot>()
                .eq(Chatbot::getAppId, wrapper.getChatbot().getAppId())
                .eq(Chatbot::getCarrierId, wrapper.getChatbot().getCarrierId())
        );
        if (CollectionUtil.isNotEmpty(createdList)) {
            throw new ServiceException("当前应用已在该运营商提交机器人审核，请勿重复创建");
        }

        wrapper.getApplication().setCustomerId(application.getCustomerId());
        wrapper.getApplication().setId(application.getId());

        //检查客户是否通过了审核
        CustomerAuditRecord customerAuditRecord = customerAuditRecordService.getOne(new LambdaQueryWrapper<CustomerAuditRecord>()
                .eq(CustomerAuditRecord::getCustomerId, application.getCustomerId())
                .eq(CustomerAuditRecord::getCarrierId, wrapper.getChatbot().getCarrierId())
                .eq(CustomerAuditRecord::getStatus, 1)
        );
        Assert.notNull(customerAuditRecord, new ServiceException("当前客户还未提交或通过平台审核~"));
        // 保存chatbot
        Chatbot chatbot = wrapper.getChatbot();
        chatbot.setCspEcNo(customerAuditRecord.getCspEcNo());
        saveOrUpdate(chatbot);
        // 更新应用
        // wrapper.getApplication().setLogoUrl(UrlUtil.getURI(wrapper.getApplication().getLogoUrl()));
        applicationService.updateById(wrapper.getApplication());
    }

    /**
     * 获取机器信息
     *
     * @param id 机器人ID
     * @return
     */
    @Override
    public ChatBotWrapperDTO selectDetail(Long id) {
        ChatBotWrapperDTO dto = baseMapper.queryDetail(id);
        Application application = dto.getApplication();
        application.setLogoUrl(UrlUtil.buildMinIoURL(application.getLogoUrl()));
        return dto;
    }

    @Override
    public ChatBotWrapperDTO selectDetail(String chatBotId) {
        ChatBotWrapperDTO dto = baseMapper.queryDetailByChatBotId(chatBotId);
        Application application = dto.getApplication();
        application.setLogoUrl(UrlUtil.buildMinIoURL(application.getLogoUrl()));
        return dto;
    }


    @Override
    @Transactional
    public boolean onlineChatbot(String id) {
        Assert.isTrue(StrUtil.isNotBlank(id), new ServiceException("无效机器人ID"));
        Chatbot chatbot = getOne(new LambdaQueryWrapper<Chatbot>().ge(Chatbot::getAuditStatus, 0).eq(Chatbot::getId, id));
        Assert.notNull(chatbot, new ServiceException("无效机器人，找不到机器人信息"));
        Assert.isTrue(StrUtil.isNotBlank(chatbot.getAccessTagNo()), new ServiceException("无效机器人"));

        carrierStrategy.forEach(strategy -> {
            if (strategy.getCarrier().equals(chatbot.getCarrierId())) {
                strategy.onlineChatBot(chatbot);
            }
        });
        return true;
    }

    @Override
    @Transactional
    public boolean createChatBot(ChatBotWrapperDTO wrapper) {
        Long carrierId = wrapper.getChatbot().getCarrierId();
        List<Chatbot> list = this.list(new LambdaQueryWrapper<Chatbot>()
                .eq(Chatbot::getCarrierId, wrapper.getChatbot().getCarrierId())
                .eq(Chatbot::getAppId, wrapper.getApplication().getId())
        );
        Assert.isTrue(CollectionUtil.isEmpty(list), new ServerException("应用已在当前运营商提交过机器人"));
        this.saveChatBot(wrapper);  //这里是统一保存数据，如果后期每个运营商有个性化的需求 则可将保存操作放到指定的策略中去完成。
        carrierStrategy.forEach(strategy -> {   //根据策略处理具体的逻辑
            if (strategy.getCarrier().equals(carrierId)) {
                strategy.createChatBot(wrapper);
            }
        });
        return true;
    }

    @Override
    @Transactional
    public boolean editChatBot(ChatBotWrapperDTO wrapper) {
        this.saveChatBot(wrapper);
        for (CarrierStrategy strategy : carrierStrategy) {
            if (strategy.getCarrier().equals(wrapper.getChatbot().getCarrierId())) {
                strategy.updateChatBot(wrapper);
            }
        }
        return true;
    }

    @Override
    public void sendCode(String token, String sessionId) throws JsonProcessingException {
        JsonNode tree = sObjectMapper.readTree((String) redisService.get(token));
        String phone = tree.get("phone").asText();

        String randomCode = String.valueOf(1000 + (int) (Math.random() * (9999 - 1000 + 1)));
        redisService.set(GlobalCacheKeyDefine.CAPTCHA_CACHE_KEY_RM_CHATBOT + sessionId, randomCode, 180L);

        String url = smsApiUrl + "/api/sendSms";

        String apiUri = SmsTemplateUtil.getReqUri(url, appId, clientId, clientKey, phone, "1251");

        ObjectNode smsInfo = sObjectMapper.createObjectNode();

        smsInfo.put("client_id", clientId);
        smsInfo.put("app_id", appId);
        smsInfo.put("template_id", 1251);
        smsInfo.put("mobile", phone);
        smsInfo.put("send_status", 1);

        ObjectNode templateArgs = sObjectMapper.createObjectNode();

        templateArgs.put("code", randomCode);
        templateArgs.put("p1", "融合通信运营管理平台");
        templateArgs.put("p2", "3分钟");

        smsInfo.set("template_args", templateArgs);

        SmsTemplateUtil.apiRequest(apiUri, smsInfo.toString());
    }

    @Override
    @Transactional
    public boolean deleteChatBot(Long id, String code, String sessionId) {

        String key = GlobalCacheKeyDefine.CAPTCHA_CACHE_KEY_RM_CHATBOT + sessionId;
        Assert.isTrue(redisService.isExist(key), new ServiceException("验证码已过期"));
        Assert.isTrue(code.equals(redisService.get(key)), new ServiceException("验证码错误！"));

        // 1. 先下线机器人
        Chatbot chatbot = this.getById(id);
        if (chatbot != null) {

            carrierStrategy.forEach(strategy -> {
                if (strategy.getCarrier().equals(chatbot.getCarrierId())) {
                    strategy.onlineChatBot(chatbot); // 根据当前状态执行上线或者下线
                }
            });

            // 也许不存在对应的记录，不检查是否成功
            materialAuditRecordService.remove(new LambdaQueryWrapper<MaterialAuditRecord>().eq(MaterialAuditRecord::getChatBotId, chatbot.getChatBotId()));
            menuAuditRecordService.remove(new LambdaQueryWrapper<MenuAuditRecord>().eq(MenuAuditRecord::getChatBotId, chatbot.getId()));

            return this.removeById(id);
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> selectChatBotByCid(String customerId) {
        return baseMapper.selectChatBotByCid(Long.parseLong(customerId));
    }

}
