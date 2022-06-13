package com.longmaster.rcs.service.channel;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * author zk
 * date 2021/4/8 15:35
 * description 电信消息接口
 */
public interface ICtService {

    // 获取token
    String getAccessToken(String chatBotId) throws InterruptedException;

    // 发送消息
    String sendMessage(JsonNode request);

    // 群发消息
    String sendGroupMessage(JsonNode request);

    // 下载文件
    byte[] downloadFile(String chatBotId, String sourceUrl);

    // 获取信息
    JsonNode getChatBotInformation(String chatBotId);

    // 提交菜单审核
    JsonNode sendMenuAudit(String chatBotId, String menuJson);

    // 提交配置审核
    String updateOptionals(String chatBotId, String paramsJson);

    // 上传素材
    JsonNode uploadFile(String chatBotId, String sourceUrl, String uploadMode);

    // 删除素材
    JsonNode deleteFile(String chatBotId, String sourceUrl);
}
