package com.longmaster.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.card.CardDTO;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateAddDTO;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateDTO;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateQueryDTO;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateUpdDTO;
import com.longmaster.platform.entity.*;
import com.longmaster.platform.mapper.*;
import com.longmaster.platform.service.MessageTemplateService;
import com.longmaster.platform.service.PayloadDataService;
import com.longmaster.platform.service.PushEventService;
import com.longmaster.platform.service.SuggestionItemService;
import com.longmaster.platform.util.UrlUtil;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 消息模板 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Service
public class MessageTemplateServiceImpl extends ServiceImpl<MessageTemplateMapper, MessageTemplate> implements MessageTemplateService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private SuggestionItemMapper suggestionItemMapper;

    @Resource
    private CardMapper cardMapper;

    @Resource
    private MaterialMapper materialMapper;

    @Resource
    private MaterialAuditRecordMapper materialAuditRecordMapper;

    @Resource
    private SuggestionItemService suggestionItemService;

    @Resource
    private PayloadDataService payloadDataService;

    @Resource
    private PushEventService pushEventService;

    @Override
    public IPage<MessageTemplateDTO> pageQuery(PageParam<MessageTemplateQueryDTO> pageParam, String carrierIds) {

        IPage<MessageTemplateDTO> page = pageParam.getPage();
        MessageTemplateQueryDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));

        IPage<MessageTemplateDTO> messageTemplateDtoIPage = baseMapper.pageSelect(page, params);
        List<MessageTemplateDTO> records = messageTemplateDtoIPage.getRecords();
        records.forEach(one -> {
            one.setAuditRecords(getAuditStatusOfMessageTemplate(one, carrierIds));

            // 悬浮
            String sugIds = one.getSugIds();
            if (!StrUtil.isEmpty(sugIds) && !"0".equals(sugIds)) {
                List<SuggestionItem> suggestionItems = suggestionItemMapper.selectSuggestionItems(sugIds);
                one.setSuggestions(suggestionItems);
            }

            Integer type = one.getType();

            switch (type) {
                // 构建卡片
                case MessageTemplate.TYPE_SINGLE_CARD:
                case MessageTemplate.TYPE_MANY_CARD:
                    List<CardDTO> cards = buildCard(one.getPayload());
                    one.setCards(cards);
                    break;
                case MessageTemplate.TYPE_IMAGE:
                case MessageTemplate.TYPE_AUDIO:
                case MessageTemplate.TYPE_VIDEO:
                    List<Material> materials = buildMaterial(one.getPayload());
                    one.setMaterials(materials);
                    break;
            }

        });
        return messageTemplateDtoIPage;
    }

    @Override
    public ArrayNode getAuditStatusOfMessageTemplate(MessageTemplate messageTemplate, String carrierIds) {
        List<Long> longs = new ArrayList<>();
        int type = messageTemplate.getType();
        List<Map<String, Object>> auditStatus = new ArrayList<>();
        switch (type) {
            case MessageTemplate.TYPE_SINGLE_CARD:
            case MessageTemplate.TYPE_MANY_CARD:
                // 查出卡片对应的所有的素材id
                String cardIdStr = messageTemplate.getPayload(); // 为类似于 1 或者1,2 格式
                if (!StrUtil.isEmpty(cardIdStr)) {
                    List<CardDTO> cards = cardMapper.selectCardDto(cardIdStr);
                    cards.forEach(item -> {
                        Long materialId = item.getMaterialId();
                        Long thumbId = item.getThumbId();
                        longs.add(materialId);
                        longs.add(thumbId);
                    });
                    List<Long> collect = longs.stream().filter(a -> a > 0).distinct().collect(Collectors.toList());
                    if (collect != null && collect.size() > 0) {
                        auditStatus = materialAuditRecordMapper.getAuditStatusByIds(collect, Arrays.stream(carrierIds.split(",")).map(Long::parseLong).collect(Collectors.toList()));
                    }
                }
                break;
            case MessageTemplate.TYPE_IMAGE:
            case MessageTemplate.TYPE_AUDIO:
            case MessageTemplate.TYPE_VIDEO:
                String payload = messageTemplate.getPayload();
                if (!StrUtil.isEmpty(payload)) {
                    auditStatus = materialAuditRecordMapper.getAuditStatusByIds(
                            Arrays.stream(payload.split(",")).map(Long::parseLong).filter(a -> a > 0).collect(Collectors.toList()),
                            Arrays.stream(carrierIds.split(",")).map(Long::parseLong).collect(Collectors.toList()));
                }
                break;
        }
        ArrayNode arrayNode = objectMapper.createArrayNode();

        auditStatus.forEach(map -> {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("status", Integer.parseInt(String.valueOf(map.get("status"))));
            objectNode.put("carrierId", Integer.parseInt(String.valueOf(map.get("carrier_id"))));
            objectNode.put("carrierName", String.valueOf(map.get("carrier_name")));
            arrayNode.add(objectNode);
        });
        return arrayNode;
    }

    @Override
    public MessageTemplateDTO getOne(Long id, String carrierIds) {
        MessageTemplateDTO one = baseMapper.getOne(id);
        if (one == null) {
            return null;
        }
        // 组装悬浮菜单
        one.setSuggestions(buildSuggestions(one.getSugIds()));
        // 组装card或者material
        Integer type = one.getType();
        switch (type) {
            // 构建卡片
            case MessageTemplate.TYPE_SINGLE_CARD:
            case MessageTemplate.TYPE_MANY_CARD:
                List<CardDTO> cards = buildCard(one.getPayload());
                one.setCards(cards);
                break;
            case MessageTemplate.TYPE_IMAGE:
            case MessageTemplate.TYPE_AUDIO:
            case MessageTemplate.TYPE_VIDEO:
                one.setMaterials(buildMaterial(one.getPayload()));
                break;
        }
        one.setAuditRecords(getAuditStatusOfMessageTemplate(one, carrierIds));
        return one;
    }

    private List<SuggestionItem> buildSuggestions(String idStr) {
        if (StrUtil.isEmpty(idStr)) {
            return null;
        }
        List<SuggestionItem> list = suggestionItemMapper.selectSuggestionItems(idStr);
        // 重组按钮的payload
        for (SuggestionItem suggestionItem : list) {
            if (suggestionItem.getType() == 1) {
                suggestionItem.setPayload(payloadDataService.rebuildPayLoad(suggestionItem.getPayload()));
            }
        }
        return list;
    }

    private List<CardDTO> buildCard(String idStr) {
        if (StrUtil.isEmpty(idStr)) {
            return null;
        }
        List<CardDTO> cardDtos = cardMapper.selectCardDto(idStr);
        cardDtos.forEach(item -> {
            String sugIdsOfCard = item.getSugIds();
            // 构建卡片内的按钮
            if (!StrUtil.isEmpty(sugIdsOfCard)) {
                item.setSuggestions(buildSuggestions(sugIdsOfCard));
            }
            // 构建卡片内的素材
            CardDTO.CardMaterial cardMaterial = new CardDTO.CardMaterial();
            Material thumb = materialMapper.selectOne(new LambdaQueryWrapper<Material>().eq(Material::getId, item.getThumbId()));
            if (thumb != null) {
                cardMaterial.setThumbnailUrl(UrlUtil.buildMinIoURL(thumb.getSourceUrl()));
            }

            Material file = materialMapper.selectOne(new LambdaQueryWrapper<Material>().eq(Material::getId, item.getMaterialId()));
            if (file != null) {
                cardMaterial.setFileUrl(UrlUtil.buildMinIoURL(file.getSourceUrl()));
                cardMaterial.setType(file.getType());
            }
            item.setMaterial(cardMaterial);
        });

        return cardDtos;
    }

    private List<Material> buildMaterial(String idStr) {
        if (StrUtil.isEmpty(idStr)) {
            return null;
        }
        List<Material> materials = materialMapper.selectMaterialByIdStr(idStr);
        materials.forEach(item -> item.setSourceUrl(UrlUtil.buildMinIoURL(item.getSourceUrl())));
        return materials;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMessageTemplate(MessageTemplateAddDTO request) throws JsonProcessingException {
        // 先加按钮
        List<JsonNode> suggestions = request.getSuggestions();
        // 组装成逗号隔开
        List<SuggestionItem> itemList = buildSugIds(suggestions, request.getAppId());
        List<Long> list = itemList.stream().map(CommonEntity::getId).collect(Collectors.toList());
        MessageTemplate messageTemplate = new MessageTemplate();
        messageTemplate.setAppId(request.getAppId());
        messageTemplate.setName(request.getName());
        messageTemplate.setSugIds(Joiner.on(",").join(list));
        messageTemplate.setStatus(0);
        messageTemplate.setType(request.getType());
        messageTemplate.setBackType(request.getBackType());
        messageTemplate.setPayload(request.getPayload());
        messageTemplate.setWidth(request.getWidth());
        messageTemplate.setOrientation(request.getOrientation());
        messageTemplate.setSmsContent(request.getSmsContent());
        baseMapper.insert(messageTemplate);

        List<Long> list1 = itemList.stream().filter(a -> a.getType() == 1).map(CommonEntity::getId).collect(Collectors.toList());
        payloadDataService.updateMessageTemplateIds(list1, messageTemplate.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updMessageTemplate(MessageTemplateUpdDTO request) {
        MessageTemplate entity = new MessageTemplate();
        int weight = 99;

        // 1.处理悬浮菜单
        List<SuggestionItem> suggestions = request.getSuggestions();
        for (int i = 0; i < suggestions.size(); i++) {
            suggestions.get(i).setWeight(weight--);
            suggestions.get(i).setAppId(request.getAppId());
        }

        suggestions.forEach(suggestionItemService::updateOrSaveSuggestionItem);
        long[] longs = suggestions.stream().mapToLong(SuggestionItem::getId).toArray();
        String newStr = StrUtil.join(",", longs);

        String sugIds = request.getSugIds();
        if (!StrUtil.isEmpty(sugIds)) {
            List<String> oldList = Arrays.asList(sugIds.split(","));
            List<String> newList = Arrays.asList(newStr.split(","));

            List<String> delList = oldList.stream().filter(item -> !newList.contains(item)).collect(Collectors.toList());
            if (delList.size() > 0) {
                suggestionItemService.deleteSug(StrUtil.join(",", delList)); // 删除多余的按钮
            }
        }
        // 2.处理payload
        entity.setPayload(request.getPayload());
        entity.setId(request.getId());
        entity.setAppId(request.getAppId());
        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setBackType(request.getBackType());
        entity.setSugIds(newStr);
        entity.setStatus(request.getStatus());
        entity.setOrientation(request.getOrientation());
        entity.setWidth(request.getWidth());
        entity.setImageAlignment(request.getImageAlignment());
        entity.setSmsContent(request.getSmsContent());
        this.updateById(entity);

        List<Long> ids = new ArrayList<>();
        for (SuggestionItem suggestion : suggestions) {
            if (suggestion.getType() == 1) {
                ids.add(suggestion.getId());
            }
        }
        payloadDataService.updateMessageTemplateIds(ids, entity.getId()); // 更新新的消息模板id
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessageTemplate(Long id) {
        Assert.notNull(id, new ServerException("消息模板ID不允许为空！"));
        PushEvent one = pushEventService.getOne(new LambdaQueryWrapper<PushEvent>().eq(PushEvent::getMessageTemplateId, id).select(PushEvent::getId));
        Assert.isNull(one, new ServerException("当前模板正处于任务队列中~"));
        MessageTemplate template = this.getById(id);
        suggestionItemService.deleteSug(template.getSugIds());
        this.removeById(id);
    }

    private List<SuggestionItem> buildSugIds(List<JsonNode> suggestions, Long appId) {
        List<SuggestionItem> suggestionList = new ArrayList<>();
        int weight = 99;
        for (JsonNode item : suggestions) {
            int type = 0;
            String displayText = "";

            if (item.has("reply")) {
                type = 1;
                JsonNode reply = item.get("reply");
                displayText = reply.get("displayText").asText();
            }

            if (item.has("action")) {
                JsonNode action = item.get("action");
                if (action.has("urlAction")) {
                    type = 2;
                }
                if (action.has("dialerAction")) {
                    type = 3;
                }
                if (action.has("mapAction")) {
                    type = 4;
                }
                if (action.has("calendarAction")) {
                    type = 5;
                }
                displayText = action.get("displayText").asText();
            }

            SuggestionItem entity = new SuggestionItem();
            entity.setAppId(appId);
            entity.setStatus(0);
            entity.setDisplayText(displayText);
            try {
                entity.setPayload(objectMapper.writeValueAsString(item));
            } catch (JsonProcessingException e) {
                entity.setPayload("");
            }
            entity.setType(type);
            entity.setWeight(weight--);
            suggestionItemMapper.insert(entity);
            suggestionItemService.updatePayload(entity); // 更新内容
            //Long id = entity.getId();
            suggestionList.add(entity);
        }
        return suggestionList;
    }
}
