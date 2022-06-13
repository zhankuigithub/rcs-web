package com.longmaster.guixjks.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.request.WebAPISign;
import com.longmaster.core.util.CommonUtil;
import com.longmaster.guixjks.service.IWebApiService;
import com.longmaster.guixjks.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebApiServiceImpl implements IWebApiService {


    @Value("${webApiCfg.url}")
    private String apiUrl;

    @Value("${webApiCfg.appId}")
    private String appId;

    @Value("${webApiCfg.appKey}")
    private String appKey;

    @Resource
    private RedisService mRedisService;

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @Resource
    private RestTemplate restTemplate;

    private Map baseParams() {
        Map baseParams = new HashMap<String, Object>();
        baseParams.put("app_id", appId);
        baseParams.put("nonce_str", CommonUtil.getRandomString(32));
        baseParams.put("user_id", 10000);
        baseParams.put("c_ver", 5008);
        baseParams.put("version", 9007);
        return baseParams;
    }


    @Override
    public JsonNode doctorList(String keyword, int currPage) throws JsonProcessingException {
        StringBuilder requestUrl = new StringBuilder(apiUrl).append("/inquiry/doctor/list");
        Map params = new HashMap<>();
        params.put("keyword", keyword.substring(0, keyword.indexOf("医生")));
        params.put("curr_page", currPage);
        params.put("page_size", 5);
        params.put("ad_code", 520100);
        params.put("tag_id", 0);
        params.put("doc_type", 3); // 医生类型 1-视频 2-电话 3-图文 4-挂号
        params.put("curr_time", System.currentTimeMillis());

        WebAPISign webAPISign = new WebAPISign(this.appKey);

        Map<String, Object> allParams = new HashMap<>();
        // 合并参数字典表
        allParams.putAll(baseParams());
        allParams.putAll(params);
        // 生产签名
        String sign = webAPISign.makeSign(allParams);
        allParams.put("sign", sign);

        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("json", sObjectMapper.writeValueAsString(allParams));


        HttpEntity httpEntity = new HttpEntity(objectNode);
        ResponseEntity<JsonNode> exchange = restTemplate.exchange(requestUrl.toString(), HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public JsonNode orderList(String phone, Long timeStamp) throws JsonProcessingException {
        StringBuilder requestUrl = new StringBuilder(apiUrl).append("/registration/order/list");
        Map params = new HashMap<>();
        params.put("user_id", getUserId(phone));
        params.put("timeStamp", timeStamp);
        params.put("pageSize", 5);
        WebAPISign webAPISign = new WebAPISign(this.appKey);

        Map<String, Object> allParams = new HashMap<>();
        // 合并参数字典表
        allParams.putAll(baseParams());
        allParams.putAll(params);
        // 生产签名
        String sign = webAPISign.makeSign(allParams);
        allParams.put("sign", sign);

        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("json", sObjectMapper.writeValueAsString(allParams));

        HttpEntity httpEntity = new HttpEntity(objectNode);
        ResponseEntity<JsonNode> exchange = restTemplate.exchange(requestUrl.toString(), HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public JsonNode recordList(String phone, int currPage) throws JsonProcessingException {
        StringBuilder requestUrl = new StringBuilder(apiUrl).append("/inquiry/im/record/list");
        Map params = new HashMap<>();
        params.put("curr_page", currPage);
        params.put("page_size", 5);
        //params.put("from", 1); // 1:APP； 2:微信；3-快应用
        params.put("record_state", 5);
        params.put("user_id", getUserId(phone));

        WebAPISign webAPISign = new WebAPISign(this.appKey);

        Map<String, Object> allParams = new HashMap<>();
        // 合并参数字典表
        allParams.putAll(baseParams());
        allParams.putAll(params);
        // 生产签名
        String sign = webAPISign.makeSign(allParams);
        allParams.put("sign", sign);

        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("json", sObjectMapper.writeValueAsString(allParams));

        HttpEntity httpEntity = new HttpEntity(objectNode);
        ResponseEntity<JsonNode> exchange = restTemplate.exchange(requestUrl.toString(), HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public int getUserId(String phone) {

        return mRedisService.unblockingGet("USER_ID_" + phone, 60 * 60 * 30, () -> {
            StringBuilder requestUrl = new StringBuilder(apiUrl).append("/common/user/ivrgetuserid");
            Map params = new HashMap<>();
            params.put("phone_number", phone.substring(phone.length() - 11));

            WebAPISign webAPISign = new WebAPISign(this.appKey);

            Map<String, Object> allParams = new HashMap<>();
            // 合并参数字典表
            allParams.putAll(baseParams());
            allParams.putAll(params);
            // 生产签名
            String sign = webAPISign.makeSign(allParams);
            allParams.put("sign", sign);

            ObjectNode objectNode = sObjectMapper.createObjectNode();
            try {
                objectNode.put("json", sObjectMapper.writeValueAsString(allParams));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            HttpEntity httpEntity = new HttpEntity(objectNode);
            ResponseEntity<JsonNode> exchange = restTemplate.exchange(requestUrl.toString(), HttpMethod.POST, httpEntity, JsonNode.class);
            JsonNode body = exchange.getBody();
            return body.get("user_id").asInt();
        });

    }

    @Override
    public JsonNode patientList(String phone) throws JsonProcessingException {
        StringBuilder requestUrl = new StringBuilder(apiUrl).append("/registration/patient/list");
        Map params = new HashMap<>();
        params.put("user_id", getUserId(phone));
        WebAPISign webAPISign = new WebAPISign(this.appKey);

        Map<String, Object> allParams = new HashMap<>();
        // 合并参数字典表
        allParams.putAll(baseParams());
        allParams.putAll(params);
        // 生产签名
        String sign = webAPISign.makeSign(allParams);
        allParams.put("sign", sign);

        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("json", sObjectMapper.writeValueAsString(allParams));

        HttpEntity httpEntity = new HttpEntity(objectNode);
        ResponseEntity<JsonNode> exchange = restTemplate.exchange(requestUrl.toString(), HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }

    @Override
    public JsonNode voucherList(String phone, int index, int size) throws JsonProcessingException {
        StringBuilder requestUrl = new StringBuilder(apiUrl).append("/currency/voucher/list");
        Map params = new HashMap<>();
        params.put("user_id", getUserId(phone));
        params.put("curr_page", index);
        params.put("page_size", size);
        params.put("voucher_range", "0,1,3,4,5,6,7,8,9");
        params.put("voucher_sort", "4");
        params.put("voucher_state", "1");
        params.put("voucher_type", "1,2,3");
        params.put("curr_time", System.currentTimeMillis());

        WebAPISign webAPISign = new WebAPISign(this.appKey);

        Map<String, Object> allParams = new HashMap<>();
        // 合并参数字典表
        allParams.putAll(baseParams());
        allParams.putAll(params);
        // 生产签名
        String sign = webAPISign.makeSign(allParams);
        allParams.put("sign", sign);

        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("json", sObjectMapper.writeValueAsString(allParams));

        HttpEntity httpEntity = new HttpEntity(objectNode);
        ResponseEntity<JsonNode> exchange = restTemplate.exchange(requestUrl.toString(), HttpMethod.POST, httpEntity, JsonNode.class);
        return exchange.getBody();
    }
}
