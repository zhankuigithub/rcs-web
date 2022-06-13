package com.longmaster.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.card.CardDTO;
import com.longmaster.platform.dto.card.CardQueryDTO;
import com.longmaster.platform.dto.card.CardWrapperDto;
import com.longmaster.platform.dto.material.MaterialDTO;
import com.longmaster.platform.dto.menu.MenuWrapperWithCtDTO;
import com.longmaster.platform.entity.Card;
import com.longmaster.platform.entity.Material;
import com.longmaster.platform.entity.MessageTemplate;
import com.longmaster.platform.entity.SuggestionItem;
import com.longmaster.platform.mapper.CardMapper;
import com.longmaster.platform.mapper.MaterialAuditRecordMapper;
import com.longmaster.platform.mapper.MaterialMapper;
import com.longmaster.platform.mapper.SuggestionItemMapper;
import com.longmaster.platform.service.CardService;
import com.longmaster.platform.service.MessageTemplateService;
import com.longmaster.platform.service.PayloadDataService;
import com.longmaster.platform.service.SuggestionItemService;
import com.longmaster.platform.util.UrlUtil;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 卡片信息表 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Slf4j
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private MaterialAuditRecordMapper materialAuditRecordMapper;

    @Resource
    private MaterialMapper materialMapper;

    @Resource
    private SuggestionItemMapper suggestionItemMapper;

    @Resource
    private SuggestionItemService suggestionItemService;

    @Resource
    private PayloadDataService payloadDataService;

    @Resource
    private MessageTemplateService messageTemplateService;

    @Override
    public IPage<CardDTO> pageQuery(PageParam<CardQueryDTO> pageParam, String carrierIds) {

        IPage<CardDTO> page = pageParam.getPage();
        CardQueryDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        IPage<CardDTO> messageTemplateDtoIPage = baseMapper.pageSelect(page, params);
        List<CardDTO> records = messageTemplateDtoIPage.getRecords();
        records.forEach(item ->
                {
                    item.setAuditRecords(getAuditStatusOfCard(item, carrierIds));

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
                    String sugIds = item.getSugIds();
                    if (!StrUtil.isEmpty(sugIds)) {
                        List<SuggestionItem> suggestionItems = suggestionItemMapper.selectSuggestionItems(sugIds);
                        item.setSuggestions(suggestionItems);

                    }
                }
        );
        return messageTemplateDtoIPage;
    }

    @Override
    public ArrayNode getAuditStatusOfCard(Card card, String carrierIds) {
        Long thumbId = card.getThumbId();
        Long materialId = card.getMaterialId();
        List<Long> longs = Stream.of(thumbId, materialId).filter(a -> a > 0).collect(Collectors.toList());
        List<Long> collect = Arrays.stream(carrierIds.split(",")).map(Long::parseLong).collect(Collectors.toList());

        ArrayNode arrayNode = objectMapper.createArrayNode();
        List<Map<String, Object>> list = materialAuditRecordMapper.getAuditStatusByIds(longs, collect);
        list.forEach(map -> {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("status", Integer.parseInt(String.valueOf(map.get("status"))));
            objectNode.put("carrierId", Integer.parseInt(String.valueOf(map.get("carrier_id"))));
            objectNode.put("carrierName", String.valueOf(map.get("carrier_name")));
            arrayNode.add(objectNode);
        });
        return arrayNode;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCard(CardWrapperDto card) {
        Assert.notNull(card, new ServiceException("参数不允许为空"));
        Optional.ofNullable(card.getSugIds()).ifPresent(sugIds -> {
            if (StrUtil.isNotBlank(sugIds)) {
                suggestionItemService.deleteSug(sugIds);
            }
        });

        card.getSuggestions().forEach(action -> {
            action.setAppId(card.getAppId());
            suggestionItemService.modifySuggestionItem(action);
        });
        Object[] ids = card.getSuggestions().stream().map(MenuWrapperWithCtDTO.Action::getId).toArray();
        card.setSugIds(StrUtil.join(",", ids));
        this.saveOrUpdate(card);
        Long cardId = card.getId();

        List<Long> list = card.getSuggestions().stream().filter(item -> item.getType() == 1).map(MenuWrapperWithCtDTO::getId).collect(Collectors.toList());
        payloadDataService.updateCardIds(list, cardId);
    }

    @Override
    public Long updateOrSaveCard(Card card) {
        this.saveOrUpdate(card);
        return card.getId();
    }

    @Override
    public CardWrapperDto getCardById(Long id) {
        Assert.notNull(id, new ServiceException("参数ID不允许为空"));
        Card card = this.getById(id);
        Assert.notNull(card, new ServiceException("找到卡片相关配置信息"));

        CardWrapperDto wrapper = new CardWrapperDto();
        BeanUtil.copyProperties(card, wrapper);

        //load suggestion item
        List<SuggestionItem> items = suggestionItemMapper.selectBatchIds(Arrays.asList(card.getSugIds().split(",")));
        List<MenuWrapperWithCtDTO.Action> actions = new ArrayList<>(items.size());

        items.forEach(item -> {
            // 重组payload
            if (item.getType() == 1) {
                item.setPayload(payloadDataService.rebuildPayLoad(item.getPayload()));
            }
            MenuWrapperWithCtDTO.Entries entries = JSON.parseObject(item.getPayload(), MenuWrapperWithCtDTO.Entries.class);
            Optional.ofNullable(entries).ifPresent(en -> {
                MenuWrapperWithCtDTO.Action action = en.getAction();
                if (en.getReply() != null) {    //为了迎合前端 此处将 reply 转换为action返回 此处后期可重构
                    action = new MenuWrapperWithCtDTO.Action();
                    action.setPostback(en.getReply().getPostback());
                    action.setDisplayText(en.getReply().getDisplayText());
                    BeanUtil.copyProperties(en.getReply(), action);
                }
                BeanUtil.copyProperties(item, action);
                actions.add(action);
            });
        });
        wrapper.setSuggestions(actions);
        //load  material
        Material material = materialMapper.selectById(card.getMaterialId());
        Optional.ofNullable(material).ifPresent(mat -> {
            MaterialDTO dto = new MaterialDTO();
            BeanUtil.copyProperties(mat, dto);
            dto.setSourceUrl(UrlUtil.buildMinIoURL(material.getSourceUrl()));
            Optional.ofNullable(mat.getThumbId()).ifPresent(thumbId -> {
                Material thumb = materialMapper.selectById(thumbId);
                Optional.ofNullable(thumb).ifPresent(tm -> {
                    dto.setThumbnailUrl(UrlUtil.buildMinIoURL(thumb.getSourceUrl()));
                });
            });
            wrapper.setMaterial(dto);
        });

        return wrapper;
    }

    @Override
    public void deleteCard(Long id) {
        List<MessageTemplate> messageTemplates = messageTemplateService.list(new LambdaQueryWrapper<MessageTemplate>()
                .like(MessageTemplate::getPayload, String.valueOf(id))
                .in(MessageTemplate::getType, Arrays.asList(MessageTemplate.TYPE_SINGLE_CARD, MessageTemplate.TYPE_MANY_CARD))
                .select(MessageTemplate::getName, MessageTemplate::getId));

        if (messageTemplates != null && messageTemplates.size() > 0) {
            List<String> list = messageTemplates.stream().map(MessageTemplate::getName).collect(Collectors.toList());
            throw new ServiceException("当前卡片正在消息模板（" + list + "）中被使用到~");
        }

        Card card = this.getById(id);
        String sugIds = card.getSugIds();
        if (!StrUtil.isEmpty(sugIds)) {
            suggestionItemService.deleteSug(sugIds);
        }
        this.removeById(id);
    }

}
