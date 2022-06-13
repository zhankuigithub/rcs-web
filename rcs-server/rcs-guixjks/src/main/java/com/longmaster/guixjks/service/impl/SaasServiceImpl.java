package com.longmaster.guixjks.service.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.core.util.CommonUtil;
import com.longmaster.core.util.JacksonUtil;
import com.longmaster.core.util.MD5Utils;
import com.longmaster.guixjks.service.ISaasService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class SaasServiceImpl implements ISaasService {

    @Resource
    private RestTemplate restTemplate;


    private static final String AIINQUIRY = "aiinquiry"; //"triage";  // 问诊
    private static final String RECOMMEND = "doctor/recommend";  // 推荐
    private static final String ACCESS = "doctor/access";  // 精排序

    @Value("${saas.url}")
    private String url;

    @Value("${saas.appid}")
    private String appId;

    @Value("${saas.secret}")
    private String secret;

    @Override
    public JsonNode triageGetSession(Map params) {
        Map map = new HashMap<String, Object>();

        map.put("action", "get_session");
        map.put("intent", "triage");
        map.put("params", params);

        return getTriageResult(map);
    }

    @Override
    public JsonNode triageChat(Map params, String session) {
        Map map = new HashMap<String, Object>();

        map.put("action", "chat");
        map.put("intent", "triage");
        map.put("session", session);
        map.put("params", params);
        return getTriageResult(map);
    }


    @Override
    public JsonNode triageSelect(Map params, String session) {
        Map map = new HashMap<String, Object>();

        map.put("action", "select");
        map.put("intent", "triage");
        map.put("session", session);
        map.put("params", params);
        return getTriageResult(map);
    }

    @Override
    public JsonNode triageContinue(String session) {
        Map map = new HashMap<String, Object>();
        map.put("intent", "triage");
        map.put("action", "continue");
        map.put("session", session);
        return getTriageResult(map);
    }

    @Override
    public JsonNode triageNewSession(String session) {
        Map map = new HashMap<String, Object>();
        map.put("intent", "triage");
        map.put("action", "new_session");
        map.put("session", session);
        return getTriageResult(map);
    }

    @Override
    public JsonNode recommend(Map params) {
        params.put("size", 5);
        return getResult(RECOMMEND, params);
    }

    @Override
    public JsonNode access(Map params) {
        return getResult(ACCESS, params);
    }

    private JsonNode getTriageResult(Map map) {
        String ts = System.currentTimeMillis() / 1000 + "";
        String body = CommonUtil.getSortJsonMap(map);


        String string = "Appid=" + this.appId + "&Secret=" + this.secret + "&Ts=" + ts + "&body=" + body;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Sign", MD5Utils.getMD5(string));
        headers.set("Appid", this.appId);
        headers.set("Ts", ts);
        HttpEntity httpEntity = new HttpEntity(body, headers);

        return JacksonUtil.transFromJson(restTemplate.postForEntity(this.url + AIINQUIRY, httpEntity, String.class).getBody());
    }

    private JsonNode getResult(String uri, Map params) {
        String ts = System.currentTimeMillis() / 1000 + "";
        String body = CommonUtil.formatUrlMap(params, false, true);
        String string = "Appid=" + this.appId + "&Secret=" + this.secret + "&Ts=" + ts + "&" + body;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Sign", MD5Utils.getMD5(string));
        headers.set("Appid", this.appId);
        headers.set("Ts", ts);

        HttpEntity httpEntity = new HttpEntity(headers);


        String response = restTemplate.exchange(this.url + uri + "?" + string, HttpMethod.GET, httpEntity, String.class).getBody();
        return JacksonUtil.transFromJson(response);
    }

}
