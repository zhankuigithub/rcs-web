package com.longmaster.platform.mapper;

import com.longmaster.platform.dto.application.ApplicationDTO;
import com.longmaster.platform.entity.Application;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用信息表 Mapper 接口
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface ApplicationMapper extends BaseMapper<Application> {

    /**
     * 分页查询 应用信息 机器人信息  固定菜单信息
     * @return
     */
    IPage<ApplicationDTO> pageSelect(IPage<ApplicationDTO> page, @Param("ew") ApplicationDTO params);

    /**
     * 获取客户应用列表
     * @param customerId
     * @return
     */
    List<Application> selectAppsByCustomerId(Long customerId);

    /**
     *
     * @param customerId
     * @return
     */
    List<Application> selectAppList(Long customerId);


    /**
     * 获取详情
     * @param appId
     * @return
     */
    ApplicationDTO getInfo(Long appId);
}
