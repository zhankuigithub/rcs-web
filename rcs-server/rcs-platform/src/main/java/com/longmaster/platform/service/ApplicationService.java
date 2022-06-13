package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.application.ApplicationDTO;
import com.longmaster.platform.entity.Application;

import java.util.List;

/**
 * <p>
 * 应用信息表 服务类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
public interface ApplicationService extends IService<Application> {

    /**
     * 分页查询 应用信息 机器人信息  固定菜单信息
     *
     * @param pageParam 分页参数
     * @return
     */
    IPage<ApplicationDTO> pageQuery(PageParam<ApplicationDTO> pageParam, String carrierIds);

    /**
     * 审核应用
     *
     * @param application
     * @return
     */
    boolean auditApplication(Application application);

    /**
     * 查询客户开通应用列表
     *
     * @param customerId
     * @return
     */
    List<Application> getAppByCustomerId(Long customerId);

    /**
     * 验证是否存在app
     *
     * @param application
     */
    void validHasApp(Application application);

    /**
     * 获取应用详情
     * @param appId
     * @param carrierIds
     * @return
     */
    ApplicationDTO getInfo(Long appId, String carrierIds);
}
