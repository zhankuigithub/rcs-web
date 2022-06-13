package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.menu.MenuGroupsDTO;
import com.longmaster.platform.dto.menu.MenuWrapperWithCtDTO;
import com.longmaster.platform.entity.MenuGroups;
import com.longmaster.platform.entity.MenuItem;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 固定菜单组 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface MenuGroupsService extends IService<MenuGroups> {

    /**
     * 获取某应用下的固定菜单配置（运营商格式）
     *
     * @param appId
     * @return
     * @throws JsonProcessingException
     */
    JsonNode getMenu(Long appId, boolean isSubmit) throws JsonProcessingException;

    /**
     * 获取某应用下，所有运营商当前使用的菜单
     *
     * @param appId
     * @return
     * @throws JsonProcessingException
     */
    ArrayNode getCurrentUsed(Long appId, String carrierIds) throws JsonProcessingException;

    /**
     * 提交菜单审核
     *
     * @param appId
     * @param carrierIds
     * @return
     * @throws JsonProcessingException
     */
    Map<Long, String> submitAudit(Long appId, List<Long> carrierIds) throws JsonProcessingException;

    /**
     * 分页查询
     *
     * @param pageParam 分页参数
     * @return
     */
    IPage<MenuGroupsDTO> pageQuery(PageParam<MenuGroupsDTO> pageParam, String carrierIds);

    /**
     * 保存菜单配置
     *
     * @param wrapper 菜单配置数据包装类
     */
    void saveMenuOfCt(MenuWrapperWithCtDTO wrapper);

    /**
     * 删除固定菜单配置
     *
     * @param id
     * @return
     */
    Boolean delMenuConfig(String id);


    /**
     * @param menuItem
     */
    void updatePayload(MenuItem menuItem);

}
