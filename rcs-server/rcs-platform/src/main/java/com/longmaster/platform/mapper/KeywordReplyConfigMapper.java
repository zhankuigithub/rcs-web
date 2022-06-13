package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.keyword.KeywordReplyConfigDTO;
import com.longmaster.platform.dto.keyword.KeywordReplyConfigQueryDTO;
import com.longmaster.platform.entity.KeywordReplyConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface KeywordReplyConfigMapper extends BaseMapper<KeywordReplyConfig> {

    IPage<KeywordReplyConfigDTO> pageSelect(IPage<KeywordReplyConfigDTO> page, @Param("ew") KeywordReplyConfigQueryDTO params);

    KeywordReplyConfigDTO getOne(Long id);
}
