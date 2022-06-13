package com.longmaster.core.original.event;

import lombok.Data;

/**
 * author zk
 * date 2021/3/16 10:21
 * description 任务事件
 */
@Data
public class CreateCalendarEvent {

    private String startTime;
    private String endTime;
    private String title;
    private String description;

}
