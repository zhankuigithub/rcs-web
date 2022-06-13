package com.longmaster.platform.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.platform.dto.statistical.MessageEventDTO;
import com.longmaster.platform.entity.LogTaskSendEvent;
import com.longmaster.platform.mapper.LogTaskSendEventMapper;
import com.longmaster.platform.service.LogTaskSendEventService;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LogTaskSendEventServiceImpl extends ServiceImpl<LogTaskSendEventMapper, LogTaskSendEvent> implements LogTaskSendEventService {


    @Override
    public IPage<LogTaskSendEvent> pageQuery(PageParam<MessageEventDTO> pageParam) {
        IPage<MessageEventDTO> page = pageParam.getPage();
        MessageEventDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        return baseMapper.pageSelect(page, params);
    }

    @Override
    public PageResult<Map> detailPage(PageParam<Map> pageParam) {
        IPage iPage = baseMapper.detailPage(pageParam.getPage(), pageParam.getParams());
        return new PageResult(iPage.getTotal(), iPage.getRecords());
    }
}
