package com.longmaster.platform.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.platform.entity.LogMessageTemplateEvent;
import com.longmaster.platform.mapper.LogMessageTemplateEventMapper;
import com.longmaster.platform.service.LogMessageTemplateEventService;
import org.springframework.stereotype.Service;

@Service
public class LogMessageTemplateEventServiceImpl extends ServiceImpl<LogMessageTemplateEventMapper, LogMessageTemplateEvent> implements LogMessageTemplateEventService {

}

