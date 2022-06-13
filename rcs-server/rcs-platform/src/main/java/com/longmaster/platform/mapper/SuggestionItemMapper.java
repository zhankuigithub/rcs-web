package com.longmaster.platform.mapper;

import com.longmaster.platform.entity.SuggestionItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 建议浮动菜单项表 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface SuggestionItemMapper extends BaseMapper<SuggestionItem> {

    List<SuggestionItem> selectSuggestionItems(@Param("idStr") String idStr);

    void deleteByIds(@Param("sugIds") String sugIds);

    int updatePayload(@Param("payload") String payload, @Param("id") Long id);

}
