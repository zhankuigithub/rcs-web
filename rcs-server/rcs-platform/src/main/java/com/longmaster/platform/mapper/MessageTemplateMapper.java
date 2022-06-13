package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.messageTemplate.MessageTemplateDTO;
import com.longmaster.platform.dto.messageTemplate.MessageTemplateQueryDTO;
import com.longmaster.platform.entity.MessageTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 * 消息模板 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate> {

    IPage<MessageTemplateDTO> pageSelect(IPage<MessageTemplateDTO> page, @Param("ew") MessageTemplateQueryDTO params);

    MessageTemplateDTO getOne(Long id);
}
