package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.platform.dto.statistical.MessageEventDTO;
import com.longmaster.platform.entity.LogTaskSendEvent;

import java.util.Map;

public interface LogTaskSendEventService extends IService<LogTaskSendEvent> {


    IPage<LogTaskSendEvent> pageQuery(PageParam<MessageEventDTO> pageParam);


    PageResult<Map> detailPage(PageParam<Map> pageParam);
}
