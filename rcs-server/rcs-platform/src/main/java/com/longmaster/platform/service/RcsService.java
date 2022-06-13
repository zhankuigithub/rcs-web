package com.longmaster.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.platform.dto.csp.ChatBotWithCtDTO;
import com.longmaster.platform.dto.csp.DeveloperDTO;
import com.longmaster.platform.dto.csp.EnterpriseAuthWithCtDTO;
import com.longmaster.platform.enums.UploadType;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * author zk
 * date 2021/11/24 15:06
 * description rcs接口操作
 */
public interface RcsService {

    // 电信 上传机器人资料
    String ctUploadChatBotInfoFile(String filePath);

    // 电信 创建机器人
    JsonNode ctCreateChatBot(ChatBotWithCtDTO chatBot);

    // 电信 修改机器人
    JsonNode ctUpdateChatBot(ChatBotWithCtDTO chatBot);

    // 电信 上线机器人
    JsonNode ctOnlineChatBot(Map<String, Object> param);

    // 电信 开发者配置
    JsonNode ctDeveloper(DeveloperDTO developer);

    // 电信 提交客户审核
    JsonNode ctSubmitCustomerAudit(HttpMethod method, EnterpriseAuthWithCtDTO enterprise);

    // 电信 上传客户资料
    String ctUploadCustomerFile(String fileUrl, UploadType uploadType);

    // 电信 获取机器人信息
    JsonNode ctChatBotInfo(String chatBotId);

    // 电信 提交菜单审核
    JsonNode ctSendMenuAudit(String chatBotId, String menuJson);

    // 电信 上传素材
    JsonNode ctUploadChatBotMaterial(String chatBotId, String sourceUrl, String uploadMode);

    // 电信 删除素材
    JsonNode ctDeleteChatBotMaterial(String chatBotId, String sourceUrl);

    // 联通 上传机器人资料
    String cuUploadChatBotInfoFile(String filePath);

    // 联通 创建机器人
    JsonNode cuCreateChatBot(ChatBotWithCtDTO chatBot);

    // 联通 修改机器人
    JsonNode cuUpdateChatBot(ChatBotWithCtDTO chatBot);

    // 联通 上线机器人
    JsonNode cuOnlineChatBot(Map<String, Object> param);

    // 联通 开发者配置
    JsonNode cuDeveloper(DeveloperDTO developer);

    // 联通 提交客户审核
    JsonNode cuSubmitCustomerAudit(HttpMethod method, EnterpriseAuthWithCtDTO enterprise);

    // 联通 上传客户资料
    String cuUploadCustomerFile(String fileUrl, UploadType uploadType);

    // 联通 获取机器人信息
    JsonNode cuChatBotInfo(String chatBotId);

    // 联通 提交菜单审核
    JsonNode cuSendMenuAudit(String chatBotId, String menuJson);

    // 联通 上传素材
    JsonNode cuUploadChatBotMaterial(String chatBotId, String sourceUrl, String uploadMode);

    // 联通 删除素材
    JsonNode cuDeleteChatBotMaterial(String chatBotId, String sourceUrl);

    // 移动 上传素材
    String cmUploadChatBotMaterial(String chatBotId, String sourceUrl);

    // 移动 删除素材
    void cmDeleteChatBotMaterial(String chatBotId, String tid);

    // 广西移动 上传素材
    String gxCmUploadChatBotMaterial(String chatBotId, String sourceUrl);

    // 广西移动 删除素材
    void gxCmDeleteChatBotMaterial(String chatBotId, String tid);
}
