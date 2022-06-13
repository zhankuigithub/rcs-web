package com.longmaster.guixjks.service.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.guixjks.service.IHealthInformationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 获取健康资讯
 */
@Component
public class HealthInformationServiceImpl implements IHealthInformationService {

    @Value("${39net.baseUrl}")
    private String baseUrl;

    @Value("${iptv.baseUrl}")
    private String iptvUrl;

    @Resource
    private RestTemplate restTemplate;

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public JsonNode getArticles(int pageIndex, int pageSize) {
        StringBuilder requestUrl = new StringBuilder(baseUrl).append("articlesNew");

        ObjectNode objectNode = sObjectMapper.createObjectNode();

        objectNode.put("forumId", 0);
        objectNode.put("pageIndex", pageIndex);
        objectNode.put("pageSize", pageSize);
        objectNode.put("word", 0);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity httpEntity = new HttpEntity(objectNode.toString(), headers);
        return restTemplate.postForEntity(requestUrl.toString(), httpEntity, JsonNode.class).getBody();
    }

    @Override
    public JsonNode getVideos(int pageIndex, int pageSize) {
        StringBuilder requestUrl = new StringBuilder(iptvUrl)
                .append("videos")
                .append("?current_page=")
                .append(pageIndex)
                .append("&page_size=")
                .append(pageSize);
        return restTemplate.getForObject(requestUrl.toString(), JsonNode.class);
    }

    @Override
    public JsonNode getRecommendsNew(String uuid, int newsTake, int topicTake, int videoTake) {
        StringBuilder requestUrl = new StringBuilder(baseUrl).append("recommendsNew");
        ObjectNode objectNode = sObjectMapper.createObjectNode();

        objectNode.put("uuid", uuid);
        objectNode.put("newsTake", newsTake);
        objectNode.put("topicTake", topicTake);
        objectNode.put("videoTake", videoTake);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity httpEntity = new HttpEntity(objectNode.toString(), headers);
        return restTemplate.postForEntity(requestUrl.toString(), httpEntity, JsonNode.class).getBody();
    }

}
