package com.longmaster.core.original;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.longmaster.core.original.call.DialPhoneNumber;
import com.longmaster.core.original.call.DialerAction;
import com.longmaster.core.original.device.DeviceAction;
import com.longmaster.core.original.device.RequestDeviceSpecifics;
import com.longmaster.core.original.event.CalendarAction;
import com.longmaster.core.original.event.CreateCalendarEvent;
import com.longmaster.core.original.map.Location;
import com.longmaster.core.original.map.MapAction;
import com.longmaster.core.original.map.ShowLocation;
import com.longmaster.core.original.reply.PostBack;
import com.longmaster.core.original.reply.Reply;
import com.longmaster.core.original.url.OpenUrl;
import com.longmaster.core.original.url.UrlAction;
import lombok.Data;

import java.util.Iterator;
import java.util.Map;

/**
 * author zk
 * date 2021/3/16 14:27
 * description 悬浮菜单实体类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Suggestion {

    public static final int REPLY = 1; // 回复
    public static final int ACTION = 2; // 跳转
    public static final int PHONE = 3; // 电话
    public static final int MAP = 4; // 地图
    public static final int EVENT = 5; // 任务
    public static final int SHARE = 6; // 终端共享

    private Reply reply;
    private Action action;

    public Suggestion() {

    }

    public Suggestion(String displayText, String payLoad, Map<String, Object> params) {
        StringBuilder stringBuilder = new StringBuilder("{\"type\":\"sug\",\"action\":\"" + payLoad + "\"");
        Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            stringBuilder.append(",")
                    .append("\"" + entry.getKey() + "\"")
                    .append(":")
                    .append("\"" + entry.getValue() + "\"");
        }
        stringBuilder.append("}");
        Reply reply = new Reply();
        PostBack postBack = new PostBack();
        postBack.setData(stringBuilder.toString());
        reply.setDisplayText(displayText);
        reply.setPostback(postBack);
        this.reply = reply;
    }

    public Suggestion(int type, String displayText, String payLoad) {

        if (type == REPLY) {
            payLoad = "{\"type\":\"sug\",\"action\":\"" + payLoad + "\"}";
            Reply reply = new Reply();
            PostBack postBack = new PostBack();
            postBack.setData(payLoad);
            reply.setDisplayText(displayText);
            reply.setPostback(postBack);
            this.reply = reply;
        }

        if (type == ACTION) {
            Action action = new Action();
            action.setDisplayText(displayText);
            PostBack postBack = new PostBack();

            postBack.setData("payload");
            action.setPostback(postBack);

            UrlAction urlAction = new UrlAction();
            OpenUrl openUrl = new OpenUrl();
            openUrl.setUrl(payLoad);
            urlAction.setOpenUrl(openUrl);
            urlAction.setOpenUrl(openUrl);
            action.setUrlAction(urlAction);
            this.action = action;
        }

        if (type == PHONE) {
            Action action = new Action();
            action.setDisplayText(displayText);
            PostBack postBack = new PostBack();
            action.setPostback(postBack);
            postBack.setData(payLoad);

            DialerAction dialerAction = new DialerAction();
            DialPhoneNumber dialPhoneNumber = new DialPhoneNumber();
            dialPhoneNumber.setPhoneNumber(payLoad);
            dialerAction.setDialPhoneNumber(dialPhoneNumber);
            action.setDialerAction(dialerAction);
            this.action = action;
        }

        if (type == SHARE) {
            Action action = new Action();
            action.setDisplayText(displayText);

            DeviceAction deviceAction = new DeviceAction();
            deviceAction.setRequestDeviceSpecifics(new RequestDeviceSpecifics());
            action.setDeviceAction(deviceAction);
            this.action = action;
        }
    }


    public Suggestion(String displayText, String startTime, String endTime, String title, String description) {
        Action action = new Action();
        action.setDisplayText(displayText);

        CalendarAction calendarAction = new CalendarAction();
        CreateCalendarEvent event = new CreateCalendarEvent();
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setTitle(title);
        event.setDescription(description);
        calendarAction.setCreateCalendarEvent(event);
        action.setCalendarAction(calendarAction);
        this.action = action;
    }


    public Suggestion(String displayText, Double latitude, Double longitude) {
        Action action = new Action();
        action.setDisplayText(displayText);

        MapAction mapAction = new MapAction();

        ShowLocation showLocation = new ShowLocation();

        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        showLocation.setLocation(location);
        showLocation.setFallbackUrl("https://www.google.com/maps/@" + latitude + "," + longitude);

        mapAction.setShowLocation(showLocation);

        action.setMapAction(mapAction);

        this.action = action;
    }


}
