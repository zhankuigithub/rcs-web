package com.longmaster.platform.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.platform.entity.LogSendMessage;
import com.longmaster.platform.mapper.LogSendMessageMapper;
import com.longmaster.platform.service.LogSendMessageService;
import org.springframework.stereotype.Service;

@Service
public class LogSendMessageServiceImpl extends ServiceImpl<LogSendMessageMapper, LogSendMessage> implements LogSendMessageService {

}
