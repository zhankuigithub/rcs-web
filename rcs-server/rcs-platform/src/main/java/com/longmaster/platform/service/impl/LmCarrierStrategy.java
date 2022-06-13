package com.longmaster.platform.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.chatbot.ChatBotWrapperDTO;
import com.longmaster.platform.entity.Application;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Slf4j
@Service("lmCarrierStrategy")
@RefreshScope
public class LmCarrierStrategy extends AbstractCarrierAdapter {

    private static final Long LM_CARRIER_ID = 4L;

    @Value("${system.rcsMaap.server}")
    private String serverUrl;

    private static final String RCS_MAAP_URL = "/chatBot/save";

    @Override
    public Long getCarrier() {
        return LM_CARRIER_ID;
    }

    /**
     * 保存机器人信息 同时下发到 rcs-maap-server
     *
     * @param wrapper
     * @return
     */
    @Override
    public Object createChatBot(ChatBotWrapperDTO wrapper) {
        wrapper.getChatbot().setAuditStatus(1); // lm无需审核
        super.updateChatBot(wrapper);
        save2LmMaap(wrapper);
        return true;
    }

    @Override
    public Object updateChatBot(ChatBotWrapperDTO wrapper) {
        super.updateChatBot(wrapper);
        save2LmMaap(wrapper);
        return true;
    }

    private void save2LmMaap(ChatBotWrapperDTO wrapper) {
        Chatbot chatbot = wrapper.getChatbot();
        Application application = wrapper.getApplication();
        JSONObject chatbotInfo = JSONUtil.createObj()
                .set("id", chatbot.getId())
                .set("chatBotId", chatbot.getChatBotId())
                .set("name", application.getName())
                .set("logoUrl", UrlUtil.buildMinIoURL(application.getLogoUrl()));
        Result result = balancedRestTemplate.postForEntity(serverUrl + RCS_MAAP_URL, chatbotInfo, Result.class).getBody();
        Assert.isTrue(result.getCode() == 200, new ServerException(result.getMsg()));
    }
}
