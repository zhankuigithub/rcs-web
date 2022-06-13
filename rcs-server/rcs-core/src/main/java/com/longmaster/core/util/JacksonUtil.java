package com.longmaster.core.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * author zk
 * date 2021/4/22 14:08
 * description json帮助类
 */
public class JacksonUtil {

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    //private static XmlMapper xmlMapper = new XmlMapper();  jackson-dataformat-xml会影响全局数据响应格式，暂时不使用

    public static JsonNode transFromXmlToJson(String xml) {
        JSONObject object = XML.toJSONObject(xml);
        try {
            return sObjectMapper.readTree(object.toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonNode transFromJson(String json) {
        try {
            return sObjectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String transFromStr(Object ojb) {
        try {
            return sObjectMapper.writeValueAsString(ojb);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
