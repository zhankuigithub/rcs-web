package com.longmaster.rcs.service.csp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.rcs.dto.csp.ChatBotDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * csp chatbot 调用
 */
public interface ChatBotStrategy {

    Long carrierId();


    /* * 上传客户资料
     *
     * @param file 客户资料
     * @return 资料 url
     */
    JsonNode invokeUploadChatBotFile(MultipartFile file);

    /**
     * 新增 chatbot
     *
     * @return chatbot id
     */

    JsonNode invokeCreateChatBot(ChatBotDTO wrapper) throws JsonProcessingException;


    /*
     * 变更 chatbot 状态
     *
     * @param accessTagNo 机器人编码
     * @param type        上下线标志，1:上线，2:下线
     * @return chatbot id
     */

    JsonNode invokeIsOnlineUpdate(String accessTagNo, Integer type);

    /*
     *
     * 变更chatbot信息
     *
     * @param chatbot 变更信息
     * @return 变更结果
     */

    JsonNode invokeUpdateChatbot(ChatBotDTO wrapper);

    /**
     * 删除chatbot
     *
     * @param accessTagNo
     * @return
     */
    JsonNode invokeDeleteChatbot(String accessTagNo);

}
