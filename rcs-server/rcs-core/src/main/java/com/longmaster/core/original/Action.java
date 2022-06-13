package com.longmaster.core.original;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.longmaster.core.original.call.DialerAction;
import com.longmaster.core.original.device.DeviceAction;
import com.longmaster.core.original.event.CalendarAction;
import com.longmaster.core.original.map.MapAction;
import com.longmaster.core.original.reply.Reply;
import com.longmaster.core.original.url.UrlAction;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Action extends Reply {
    /**
     * 打开URL操作
     */
    private UrlAction urlAction;
    /**
     * 拨号操作
     */
    private DialerAction dialerAction;
    /**
     * 地图操作
     */
    private MapAction mapAction;

    /**
     * 任务事件
     */
    private CalendarAction calendarAction;

    /**
     * 终端共享
     */
    private DeviceAction deviceAction;

}
