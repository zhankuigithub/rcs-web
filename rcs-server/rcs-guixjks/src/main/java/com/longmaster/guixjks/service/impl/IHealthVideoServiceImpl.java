package com.longmaster.guixjks.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.guixjks.service.IHealthVideoService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Component
public class IHealthVideoServiceImpl implements IHealthVideoService {

    @Resource
    private RestTemplate restTemplate;

    private static ObjectMapper sObjectMapper = new ObjectMapper();


    @Override
    public JsonNode getVideos(String uuid, int cType, int pageSize) throws JsonProcessingException {
        StringBuilder append = new StringBuilder("https://interface.39.net/cmswebapi/api/fastapp/getlooklistbytype")
                .append("?uuid=")
                .append(uuid)
                .append("&ctype=")
                .append(cType)
                .append("&pagesize=")
                .append(pageSize);
        return sObjectMapper.readTree(restTemplate.getForObject(append.toString(), String.class));
    }

}
