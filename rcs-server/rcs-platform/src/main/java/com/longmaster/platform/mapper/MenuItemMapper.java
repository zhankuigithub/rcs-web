package com.longmaster.platform.mapper;

import com.longmaster.platform.entity.MenuItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 固定菜单项表 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface MenuItemMapper extends BaseMapper<MenuItem> {

    List<MenuItem> getMenusByStr(@Param("idStr") String idStr);


    int updatePayload(@Param("payload") String payload, @Param("id") Long id);


}
