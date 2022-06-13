package com.longmaster.rcs.service.csp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.rcs.dto.csp.EnterpriseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * author zk
 * date 2021/10/13 16:17
 * description 电信联通，公用的
 */
@Slf4j
public abstract class EnterpriseServiceStrategy extends CommonService implements EnterpriseStrategy {


    @Resource
    private RestTemplate cspRestTemplate;

    private static ObjectMapper sObjectMapper = new ObjectMapper();



    /**
     * 上传客户资料
     *
     * @param file       客户资料
     * @param uploadType 文件类型
     *                   0：客户图片
     *                   1：身份证正反面
     *                   2：合同
     * @return
     */

    @Override
    public JsonNode invokeUploadFile(MultipartFile file, Integer uploadType) {
        String url = baseUrl(carrierId()).append("/uploadFile").toString();

        HttpHeaders headers = baseHeaders(carrierId());
        headers.set("uploadType", String.valueOf(uploadType));
        headers.setContentType(MediaType.parseMediaType("multipart/form-data"));

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", file.getResource());
        log.info("invokeUploadFile url is {}", url);
        JsonNode result = cspRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(form, headers), JsonNode.class).getBody();
        log.info("invokeUploadFile result is {}", result);
        Assert.notNull(result, "上传客户资料失败（NO RESULT）");
        return result;
    }

    @Override
    public byte[] invokeGetFile(String fileUrl) {
        return new byte[0];
    }

    @Override
    public JsonNode invokeCreateCustomer(EnterpriseDTO wrapper) throws JsonProcessingException {
        String url = baseUrl(carrierId()).append("/cspCustomer/addcspCustomer").toString();
        log.info("[createCustomer] params is {}", sObjectMapper.writeValueAsString(wrapper));
        JsonNode result = cspRestTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(sObjectMapper.readTree(sObjectMapper.writeValueAsString(wrapper)), baseHeaders(carrierId())),
                JsonNode.class
        ).getBody();
        log.info("[createCustomer] invoke execute result is {}", result);
        return result;
    }

    @Override
    public JsonNode invokeSelectCspCustomer(String cspEcNo) {
        String url = baseUrl(carrierId()).append("/selectCspCustomer").toString();
        Map<String, Object> map = new HashMap<>();
        map.put("cspEcNo", cspEcNo);

        JsonNode result = cspRestTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(map, baseHeaders(carrierId())),
                JsonNode.class
        ).getBody();
        return result;
    }

    @Override
    public JsonNode invokeDeleteCustomer(String cspEcNo) {
        String url = baseUrl(carrierId()).append("/deleteCustomer").toString();
        Map<String, Object> map = new HashMap<>();
        map.put("cspEcNo", cspEcNo);
        JsonNode result = cspRestTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(map, baseHeaders(carrierId())),
                JsonNode.class
        ).getBody();
        return result;
    }

    @Override
    public JsonNode invokeUpdateCustomer(EnterpriseDTO wrapper) throws JsonProcessingException {
        String url = baseUrl(carrierId()).append("/cspCustomer/editcspCustomer").toString();
        log.info("[invokeUpdateCustomer] params is {}", sObjectMapper.writeValueAsString(wrapper));
        JsonNode result = cspRestTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(sObjectMapper.readTree(sObjectMapper.writeValueAsString(wrapper)), baseHeaders(carrierId())),
                JsonNode.class
        ).getBody();
        log.info("[invokeUpdateCustomer] invoke execute result is {}", result);
        return result;
    }
}

