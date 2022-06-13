package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.notice.NoticeQueryDTO;
import com.longmaster.platform.entity.Notice;
import com.longmaster.platform.mapper.NoticeMapper;
import com.longmaster.platform.service.NoticeService;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {


    @Override
    public IPage<Notice> pageQuery(PageParam<NoticeQueryDTO> pageParam) {
        IPage<NoticeQueryDTO> page = pageParam.getPage();
        NoticeQueryDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        return baseMapper.pageSelect(page, params);
    }

    @Override
    public List<Notice> queryList(Long customerId) {
        return baseMapper.queryList(String.valueOf(customerId));
    }

}
