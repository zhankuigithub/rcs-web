package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.card.CardDTO;
import com.longmaster.platform.dto.card.CardQueryDTO;
import com.longmaster.platform.dto.card.CardWrapperDto;
import com.longmaster.platform.entity.Card;

/**
 * <p>
 * 卡片信息表 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface CardService extends IService<Card> {

    IPage<CardDTO> pageQuery(PageParam<CardQueryDTO> pageParam, String carrierIds);

    ArrayNode getAuditStatusOfCard(Card card, String carrierIds);

    void saveCard(CardWrapperDto card);

    Long updateOrSaveCard(Card card);

    CardWrapperDto getCardById(Long id);

    void deleteCard(Long id);
}
