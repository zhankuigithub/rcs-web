package com.longmaster.guixjks.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.core.util.RSAUtil;
import com.longmaster.core.vo.Result;
import com.longmaster.guixjks.service.ILmtxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class LmtxServiceImpl implements ILmtxService {

    @Value("${lmtx.html}")
    private String htmlUrl;

    @Value("${lmtx.api}")
    private String requestUrl;

    @Value("${lmtx.publicKey}")
    private String publicKey;

    @Resource
    private RestTemplate restTemplate;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public Result getNeverReadMessage(String phone) {
        if (phone.length() > 11) {
            phone = phone.substring(phone.length() - 11);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);

        String encrypt = "";
        try {
            encrypt = RSAUtil.encrypt(sObjectMapper.writeValueAsString(map), publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.put("param_rsa_encrypt", Collections.singletonList(encrypt));

        StringBuilder builder = new StringBuilder(requestUrl).append("/guijiankang/get_no_read_num_5g");
        return restTemplate.postForEntity(builder.toString(), new HttpEntity(form, headers), Result.class).getBody();
    }

    @Override
    public Result getVoucherList(String phone) {
        if (phone.length() > 11) {
            phone = phone.substring(phone.length() - 11);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);

        String encrypt = "";
        try {
            encrypt = RSAUtil.encrypt(sObjectMapper.writeValueAsString(map), publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.put("param_rsa_encrypt", Collections.singletonList(encrypt));

        StringBuilder builder = new StringBuilder(requestUrl).append("/guijiankang/get_health_num_5g");
        return restTemplate.postForEntity(builder.toString(), new HttpEntity(form, headers), Result.class).getBody();
    }

    @Override
    public String buildHtml(String phone, int type) {
        if (phone.length() > 11) {
            phone = phone.substring(phone.length() - 11);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("type", type);

        String encrypt = "";
        try {
            encrypt = RSAUtil.encrypt(sObjectMapper.writeValueAsString(map), publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new StringBuilder(htmlUrl).append("?info=").append(URLEncoder.encode(encrypt)).toString();
    }

}
