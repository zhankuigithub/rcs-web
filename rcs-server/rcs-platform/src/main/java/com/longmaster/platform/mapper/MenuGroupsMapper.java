package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.menu.MenuGroupsDTO;
import com.longmaster.platform.entity.MenuGroups;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 固定菜单组 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface MenuGroupsMapper extends BaseMapper<MenuGroups> {

    /**
     * 分页查询
     *
     * @param page
     * @param params
     * @return
     */
    IPage<MenuGroupsDTO> pageSelect(IPage<MenuGroupsDTO> page, @Param("ew") MenuGroupsDTO params, @Param("carrierIds") String carrierIds);


    MenuGroups selectLastNewOne(Long appId);


    boolean isSubmit(Long appId);
}
