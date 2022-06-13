package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.platform.dto.event.PushEventDTO;
import com.longmaster.platform.entity.PushEvent;
import com.longmaster.platform.enums.CarrierEnum;
import com.longmaster.platform.mapper.PushEventMapper;
import com.longmaster.platform.service.PushEventService;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * author zk
 * date 2021/3/2 16:48
 */
@Component
public class PushEventServiceImpl extends ServiceImpl<PushEventMapper, PushEvent> implements PushEventService {

    @Override
    public PageResult<PushEventDTO> pageQuery(PageParam<PushEvent> pageParam) {
        IPage<PushEvent> page = pageParam.getPage();
        PushEvent params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));

        IPage<PushEventDTO> list = baseMapper.pageSelect(page, params);

        // 运营商
        for (PushEventDTO record : list.getRecords()) {
            String carrierIds = record.getCarrierIds();
            List<String> strings = Arrays.asList(carrierIds.split(","));

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < strings.size(); i++) {
                String name = CarrierEnum.getNameByType((long) Integer.parseInt(strings.get(i)));
                builder.append(name);
                if (i != strings.size() - 1) {
                    builder.append(",");
                }
            }
            record.setCnt(record.getPhoneNums().split(",").length);
            record.setCarrierName(builder.toString());
        }
        return new PageResult<>(list.getTotal(), list.getRecords());
    }

    @Override
    public boolean activateOrSuspend(String taskId) {
        Long id = Long.parseLong(taskId);
        PushEvent event = this.getById(id);

        Integer status = event.getSendStatus();
        Assert.isFalse(status == PushEvent.SEND_STATUS_CANCEL, new ServiceException("已取消的任务不可二次操作"));
        Assert.isFalse(status == PushEvent.SEND_STATUS_COMPLETE, new ServiceException("已完成的任务不可二次操作"));

        switch (status) {
            case PushEvent.SEND_STATUS_INIT:
                event.setSendStatus(PushEvent.SEND_STATUS_STOP);
                break;
            case PushEvent.SEND_STATUS_STOP:
                event.setSendStatus(PushEvent.SEND_STATUS_INIT);
                break;
        }
        return this.updateById(event);
    }

    @Override
    public boolean cancel(String taskId) {
        Long id = Long.parseLong(taskId);
        PushEvent event = this.getById(id);

        Integer status = event.getSendStatus();
        Assert.isFalse(status == PushEvent.SEND_STATUS_CANCEL, new ServiceException("当前任务已处于取消状态"));
        Assert.isFalse(status == PushEvent.SEND_STATUS_COMPLETE, new ServiceException("已完成的任务不可二次操作"));

        // 设置为取消
        event.setSendStatus(PushEvent.SEND_STATUS_CANCEL);
        return this.updateById(event);
    }
}
