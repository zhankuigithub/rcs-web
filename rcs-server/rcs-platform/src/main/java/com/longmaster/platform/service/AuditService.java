package com.longmaster.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * author zk
 * date 2021/2/27 9:19
 * description 运营商审核回调逻辑处理接口
 */
public interface AuditService {

    /**
     * author zk
     * date 2021/2/27 9:21
     * description 移动审核
     */
    int auditCMCC(JsonNode jsonNode);


    /**
     * author zk
     * date 2021/2/27 9:21
     * description 电信审核
     */
    int auditCTCC(JsonNode jsonNode) throws JsonProcessingException;


    /**
     * author zk
     * date 2021/10/25 13:46
     * description 联通审核
     */
    int auditCUCC(JsonNode jsonNode) throws JsonProcessingException;

}
