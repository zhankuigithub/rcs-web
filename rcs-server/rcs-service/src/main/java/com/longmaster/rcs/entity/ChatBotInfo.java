package com.longmaster.rcs.entity;

import lombok.Data;

/**
 * author zk
 * date 2021/3/11 11:30
 * description chatbot信息
 */
@Data
public class ChatBotInfo {

    private String chatBotId;
    private String appId;
    private String appKey;
    private Long carrierId;

}
