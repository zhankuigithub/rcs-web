package com.longmaster.guixjks.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface IHealthVideoService {

    /**
     * 启生，拉取随机推荐视频接口
     *
     * @param uuid
     * @param cType
     * @param pageSize
     * @return
     */
    JsonNode getVideos(String uuid, int cType, int pageSize) throws JsonProcessingException;

}
