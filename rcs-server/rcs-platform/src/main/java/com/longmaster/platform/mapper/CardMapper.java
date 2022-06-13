package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.card.CardDTO;
import com.longmaster.platform.dto.card.CardQueryDTO;
import com.longmaster.platform.entity.Card;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 卡片信息表 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface CardMapper extends BaseMapper<Card> {

    List<CardDTO> selectCardDto(@Param("idStr") String idStr);

    IPage<CardDTO> pageSelect(IPage<CardDTO> page, @Param("ew") CardQueryDTO params);

}
