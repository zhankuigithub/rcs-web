package com.longmaster.platform.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.statistical.StatisticalDTO;
import com.longmaster.platform.entity.LogReceiveMessage;
import com.longmaster.platform.mapper.LogReceiveMessageMapper;
import com.longmaster.platform.service.LogReceiveMessageService;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Service;

@Service
public class LogReceiveMessageServiceImpl extends ServiceImpl<LogReceiveMessageMapper, LogReceiveMessage> implements LogReceiveMessageService {

    @Override
    public IPage<LogReceiveMessage> pageQuery(PageParam<StatisticalDTO> pageParam) {
        IPage<StatisticalDTO> page = pageParam.getPage();
        StatisticalDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        return baseMapper.pageSelect(page, params);
    }

}
