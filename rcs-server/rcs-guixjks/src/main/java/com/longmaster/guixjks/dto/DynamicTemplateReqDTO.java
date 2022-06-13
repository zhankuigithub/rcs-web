package com.longmaster.guixjks.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

/**
 * 具体的key命名，需要去csp后台自行查看
 */
@Data
@Builder
public class DynamicTemplateReqDTO {


    private Long templateId;

    private String phone;

    private JsonNode body;

    private JsonNode suggestion;

    private String chatBotId;

    private String conversationId;

    private String contributionId;

    private boolean isMessageBack;
}
