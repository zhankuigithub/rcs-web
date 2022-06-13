package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.dto.dynamicTemplate.DynamicTemplateDTO;
import com.longmaster.platform.dto.dynamicTemplate.DynamicTemplateQueryDTO;
import com.longmaster.platform.entity.DynamicTemplate;
import org.apache.ibatis.annotations.Param;

public interface DynamicTemplateMapper extends BaseMapper<DynamicTemplate> {

    IPage<DynamicTemplateDTO> pageSelect(IPage<DynamicTemplateQueryDTO> page, @Param("ew") DynamicTemplateQueryDTO params);

    DynamicTemplateDTO item(Long id);
}
