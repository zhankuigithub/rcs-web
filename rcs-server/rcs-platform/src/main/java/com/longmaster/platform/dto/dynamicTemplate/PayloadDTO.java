package com.longmaster.platform.dto.dynamicTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.List;
import java.util.Map;

// 动态模板标准key的字段，以此过滤多余字段
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayloadDTO {

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private JsonNode suggestions;

    private int type;

    private String smsContent;

    private Object content;

    private JsonNode layout;

    public Object getContent() {
        switch (type) {
            case 2:
            case 3:
                try {
                    String value = sObjectMapper.writeValueAsString(content);
                    List<Map> list = sObjectMapper.readValue(value, List.class);
                    for (Map map : list) {
                        map.remove("material");
                    }
                    return list;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return content;
        }
        return content;
    }
}
