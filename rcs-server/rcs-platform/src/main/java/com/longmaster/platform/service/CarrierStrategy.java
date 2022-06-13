package com.longmaster.platform.service;

import com.longmaster.platform.dto.chatbot.ChatBotWrapperDTO;
import com.longmaster.platform.dto.customer.CustomerCtAuthDTO;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.ChatbotDeveloper;
import com.longmaster.platform.enums.UploadType;

public interface CarrierStrategy {

    Long getCarrier();

    //创建机器人
    Object createChatBot(ChatBotWrapperDTO wrapper);

    //变更机器人
    Object updateChatBot(ChatBotWrapperDTO wrapper);

    //新增客户
    Object createCustomer(Object wrapper) throws Exception;

    //变更客户
    Object updateCustomer(CustomerCtAuthDTO wrapper) throws Exception;

    //上传客户文件
    Object uploadCustomerFile(String fileUrl, UploadType uploadType, Object... args);

    //上传机器人文件
    String uploadChatBotFile(String fileUrl);

    //上线
    default Boolean onlineChatBot(Chatbot chabot) {
        return true;
    }

    //开发者配置
    default void developerConfig(ChatbotDeveloper devConfig, String accessTagNo) {
    }
}
