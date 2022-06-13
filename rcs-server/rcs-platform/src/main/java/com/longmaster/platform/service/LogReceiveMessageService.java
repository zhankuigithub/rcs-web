package com.longmaster.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.statistical.StatisticalDTO;
import com.longmaster.platform.entity.LogReceiveMessage;

public interface LogReceiveMessageService extends IService<LogReceiveMessage> {


    IPage<LogReceiveMessage> pageQuery(PageParam<StatisticalDTO> pageParam);


}
