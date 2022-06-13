package com.longmaster.rcs.service.csp;

import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.rcs.dto.csp.DeveloperDTO;

/**
 * csp 认证
 */
public interface AuthStrategy {

    Long carrierId();
    /**
     * 获取 CSp  accessToken
     * @return accessToken
     */
    String invokeAccessToken();

    /**
     * 开发者配置
     * @param developer  开发者信息
     */
    JsonNode invokeUpdateDeveloper(DeveloperDTO developer);

    /**
     * 获取accessToken
     * @return
     */
    String getAccessToken();
}
