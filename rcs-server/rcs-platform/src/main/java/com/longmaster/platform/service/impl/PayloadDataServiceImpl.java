package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.platform.entity.MenuItem;
import com.longmaster.platform.entity.PayloadData;
import com.longmaster.platform.entity.SuggestionItem;
import com.longmaster.platform.mapper.PayloadDataMapper;
import com.longmaster.platform.service.PayloadDataService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayloadDataServiceImpl extends ServiceImpl<PayloadDataMapper, PayloadData> implements PayloadDataService {

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Override
    public String updateOrSaveDataOfMenu(String payload, MenuItem entity) throws JsonProcessingException {
        JsonNode payloadJson = sObjectMapper.readTree(payload);
        if (payloadJson.has("reply")) {

            PayloadData payloadData = new PayloadData();
            payloadData.setMenuId(entity.getId());

            String data = payloadJson.get("reply").get("postback").get("data").asText(); // 原始数据为字符串，必须转一次
            ObjectNode dataJson = (ObjectNode) sObjectMapper.readTree(data);
            // 更新id
            dataJson.put("id", String.valueOf(entity.getId()));
            String newData = sObjectMapper.writeValueAsString(dataJson);
            ObjectNode node = (ObjectNode) payloadJson.get("reply").get("postback");

            payloadData.setData(newData);

            PayloadData one = baseMapper.selectOne(new LambdaQueryWrapper<PayloadData>().eq(PayloadData::getMenuId, entity.getId()));

            if (one != null) {  // 存在则更新
                payloadData.setId(one.getId());
                baseMapper.updateById(payloadData);
            } else {
                baseMapper.insert(payloadData);
            }
            node.put("data", String.valueOf(payloadData.getId()));
            return payloadJson.toString();
        }
        return null;
    }

    @Override
    public String updateOrSaveDataOfSug(String payload, SuggestionItem entity) throws JsonProcessingException {
        JsonNode payloadJson = sObjectMapper.readTree(payload);
        if (payloadJson.has("reply")) {

            String data = payloadJson.get("reply").get("postback").get("data").asText();
            ObjectNode objectNode = sObjectMapper.createObjectNode();
            objectNode.put("type", "sug");
            objectNode.put("id", String.valueOf(entity.getId()));
            objectNode.put("action", data);
            String newData = sObjectMapper.writeValueAsString(objectNode);
            ObjectNode node = (ObjectNode) payloadJson.get("reply").get("postback");

            PayloadData payloadData = new PayloadData();
            payloadData.setSuggestionId(entity.getId());
            payloadData.setData(newData);

            PayloadData one = baseMapper.selectOne(new LambdaQueryWrapper<PayloadData>().eq(PayloadData::getSuggestionId, entity.getId()));
            if (one != null) {  // 存在则更新
                payloadData.setId(one.getId());
                baseMapper.updateById(payloadData);
            } else {
                baseMapper.insert(payloadData);
            }
            node.put("data", String.valueOf(payloadData.getId()));
            return payloadJson.toString();
        }
        return null;
    }

    @Override
    public String rebuildPayLoad(String payload) {
        JsonNode jsonNode = null;
        try {
            jsonNode = sObjectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        long payDataId = jsonNode.get("reply").get("postback").get("data").asLong();
        if (payDataId == 0L) {
            return payload;
        }
        PayloadData payloadData = this.getById(payDataId);
        ObjectNode node = (ObjectNode) jsonNode.get("reply").get("postback");
        node.put("data", payloadData.getData());
        return jsonNode.toString();
    }

    @Override
    public void updateCardIds(List<Long> sugIds, Long cardId) {
        for (Long sugId : sugIds) {
            this.update(new LambdaUpdateWrapper<PayloadData>().set(PayloadData::getCardId, cardId).eq(PayloadData::getSuggestionId, sugId));
        }
    }

    @Override
    public void updateMessageTemplateIds(List<Long> sugIds, Long templateId) {
        for (Long sugId : sugIds) {
            PayloadData one = this.getOne(new LambdaQueryWrapper<PayloadData>().eq(PayloadData::getSuggestionId, sugId));
            String data = one.getData();
            ObjectNode dataJson = null;
            try {
                dataJson = (ObjectNode) sObjectMapper.readTree(data);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            assert dataJson != null;
            dataJson.put("mid", templateId);
            this.update(new LambdaUpdateWrapper<PayloadData>()
                    .set(PayloadData::getMessageTemplateId, templateId)
                    .set(PayloadData::getData, dataJson.toString())
                    .eq(PayloadData::getSuggestionId, sugId));
        }
    }

    @Override
    public String saveOrRebuildPayload(String payload, Long templateId) {
        JsonNode jsonNode = null;
        try {
            jsonNode = sObjectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assert jsonNode != null;

        long aLong = jsonNode.get("reply").get("postback").get("data").asLong();
        if (aLong == 0L) {
            return payload;
        }
        PayloadData payloadData = this.getById(aLong);

        // 查找此按钮与消息模板有没有已经存在的数据
        Long suggestionId = payloadData.getSuggestionId();
        PayloadData one = this.getOne(new LambdaQueryWrapper<PayloadData>()
                .eq(PayloadData::getSuggestionId, suggestionId).eq(PayloadData::getMessageTemplateId, templateId)
                .select(PayloadData::getId));

        String data = payloadData.getData();
        if (one == null) {
            // 不存在时需要新增
            ObjectNode dataJson = null;
            try {
                dataJson = (ObjectNode) sObjectMapper.readTree(data);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            assert dataJson != null;
            dataJson.put("mid", templateId);

            PayloadData entity = new PayloadData();
            entity.setData(dataJson.toString());
            entity.setMessageTemplateId(templateId);
            entity.setSuggestionId(payloadData.getSuggestionId());
            entity.setCardId(payloadData.getCardId());
            this.save(entity);

            // 返回新id的json串
            ObjectNode result = (ObjectNode) jsonNode.get("reply").get("postback");
            result.put("data", String.valueOf(entity.getId()));
            return jsonNode.toString();
        } else {
            ObjectNode result = (ObjectNode) jsonNode.get("reply").get("postback");
            result.put("data", String.valueOf(one.getId()));
            return jsonNode.toString();
        }
    }

    @Override
    public void deleteBySugIds(String sugIds) {
        baseMapper.deleteBySugIds(sugIds);
    }

    @Override
    public void deleteByMenuIds(String menuIds) {
        baseMapper.deleteByMenuIds(menuIds);
    }
}
