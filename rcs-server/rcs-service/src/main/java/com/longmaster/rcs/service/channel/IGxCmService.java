package com.longmaster.rcs.service.channel;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * author zk
 * date 2021/4/8 15:48
 * description 广西移动消息接口
 */
public interface IGxCmService {

    // 发消息
    String sendMessage(JsonNode request, String appId, String appKey);

    // 群发消息
    String sendGroupMessage(JsonNode request, String appId, String appKey);

    // 下载文件
    byte[] downloadFile(String chatBotId, String appId, String appKey, String url);

    // 上传文件，返回tid
    String uploadFile(String chatBotId, String sourceUrl);

    // 删除文件
    void deleteFile(String chatBotId, String tid);
}


