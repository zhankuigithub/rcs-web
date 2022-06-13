package com.longmaster.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.application.ApplicationDTO;
import com.longmaster.platform.entity.Application;
import com.longmaster.platform.mapper.ApplicationMapper;
import com.longmaster.platform.mapper.ChatbotMapper;
import com.longmaster.platform.mapper.MenuGroupsMapper;
import com.longmaster.platform.service.ApplicationService;
import com.longmaster.platform.util.UrlUtil;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 应用信息表 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    @Resource
    private ChatbotMapper chatbotMapper;

    @Resource
    private MenuGroupsMapper menuGroupsMapper;

    /**
     * 分页查询 应用信息 机器人信息  固定菜单信息
     *
     * @param pageParam 分页参数
     * @return
     */
    @Override
    public IPage<ApplicationDTO> pageQuery(PageParam<ApplicationDTO> pageParam, String carrierIds) {
        IPage<ApplicationDTO> page = pageParam.getPage(ApplicationDTO.class);
        ApplicationDTO params = pageParam.getParams();
        IPage<ApplicationDTO> pageSelect = baseMapper.pageSelect(page, params);
        pageSelect.getRecords().forEach(item -> {
            item.setChatbotItems(chatbotMapper.selectChatbotList(item.getId(), carrierIds));
            item.setMenuAuditTag(menuGroupsMapper.isSubmit(item.getId()));
        });
        return pageSelect;
    }

    @Override
    public boolean auditApplication(Application application) {
        Assert.notNull(application.getId(), new ServerException("应用ID不允许为空"));
        Assert.notNull(application.getStatus(), new ServerException("应用ID不允许为空"));
        updateById(application);
        return false;
    }

    /**
     * 获取客户应用开通机器人列表
     *
     * @param customerId
     * @return
     */
    @Override
    public List<Application> getAppByCustomerId(Long customerId) {
        Assert.notNull(customerId, new ServiceException("客户ID不允许为空~"));
        return baseMapper.selectAppsByCustomerId(customerId);
    }

    @Override
    public void validHasApp(Application application) {
        List<Application> list = this.list(new LambdaQueryWrapper<Application>()
                .eq(Application::getCustomerId, application.getCustomerId())
                .eq(Application::getName, application.getName())
                .ne(application.getId() != null, Application::getId, application.getId())
        );
        Assert.isTrue(CollectionUtil.isEmpty(list), new ServiceException("当前客户已存在'%s'应用名", application.getName()));
        application.setStatus(0);   //重置审核状态
        application.setRemark("");
    }

    @Override
    public ApplicationDTO getInfo(Long appId, String carrierIds) {
        ApplicationDTO application = baseMapper.getInfo(appId);
        application.setLogoUrl(UrlUtil.buildMinIoURL(application.getLogoUrl()));
        ApplicationDTO applicationDto = new ApplicationDTO();
        BeanUtil.copyProperties(application, applicationDto);
        if (!StrUtil.isEmpty(carrierIds)) {
            applicationDto.setChatbotItems(chatbotMapper.selectChatbotList(appId, carrierIds));
        }
        return applicationDto;
    }
}
