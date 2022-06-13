package com.longmaster.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.event.AddPushEventDTO;
import com.longmaster.platform.dto.message.MaapMessage;
import com.longmaster.platform.dto.message.MessageDTO;
import com.longmaster.platform.dto.message.NotifyBodyDTO;
import com.longmaster.platform.entity.Chatbot;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * author zk
 * date 2021/2/22 16:30
 * description 消息处理service
 */
public interface MessageService {

    /**
     * 处理定制化service的请求
     *
     * @param jsonNode
     */
    void receiveCustom(JsonNode jsonNode);

    /**
     * tnapi，挂号成功，取消挂号通知
     *
     * @param
     */
    String notifyWebapi(NotifyBodyDTO notifyBody) throws IOException, TemplateException;

    /**
     * rcs-smc，会诊通知来调用
     *
     * @param jsonNode
     */
    String notifySmc(JsonNode jsonNode);

    /**
     * 更具消息模板id发送消息
     *
     * @param request
     */
    void sendMessageByMsgTmpId(MessageDTO request);

    /**
     * 获取chatbot下，关键词消息模板
     */
    ArrayNode getMsgTmpByKeyword(Chatbot chatbot, String keyword, String userPhone, boolean isMessageBac);

    /**
     * 发送消息
     */
    Result sendGroupMessage(AddPushEventDTO pushEvent) throws JsonProcessingException;

    /**
     * 通过模板id生成标准的消息json
     *
     * @param templateId
     * @param appId
     * @param chatBotId
     * @param userPhone
     * @return
     */
    ObjectNode buildMessage(Long templateId, Long appId, String chatBotId, String userPhone, boolean isMessageBack);


    /**
     * 保存发送队列
     *
     * @param message
     */
    void saveSendQueue(MaapMessage message);

}
