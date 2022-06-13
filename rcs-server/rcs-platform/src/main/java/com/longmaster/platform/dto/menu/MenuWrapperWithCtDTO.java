package com.longmaster.platform.dto.menu;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import com.longmaster.platform.entity.MenuItem;
import com.longmaster.platform.entity.SuggestionItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 菜单封装类
 */
@Data
public class MenuWrapperWithCtDTO {

    @ApiModelProperty("ID，唯一标识")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private transient Long id;

    @ApiModelProperty("显示文本")
//    @NotNull(message = "请填写配置菜单显示文本")
    private String displayText;

    @Valid
    @ApiModelProperty("固定菜单")
//    @NotNull(message = "请配置固定菜单")
    private Menu menu;

    @ApiModelProperty("应用ID")
//    @NotNull(message = "请填写应用ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private transient Long appId;

    @ApiModelProperty("排序")
    private transient Integer weight;

    @ApiModelProperty("类型")
    private transient Integer type;

    /**
     * 菜单
     */
    @Data
    static class Menu extends MenuWrapperWithCtDTO {

        @ApiModelProperty("菜单配置")
        @NotEmpty(message = "请填写菜单配置")
        @Size(min = 1, max = 3, message = "仅支持1~3个菜单配置")
        private List<Entries> entries;
    }

    /**
     * 回复
     */
    @Data
    public static class Reply extends MenuWrapperWithCtDTO {
        private Postback postback;
    }

    /**
     * 事件
     */
    @Data
    public static class Action extends MenuWrapperWithCtDTO {

        @ApiModelProperty("回复")
        private Postback postback;

        @ApiModelProperty("拨号动作")
        private DialerAction dialerAction;

        @ApiModelProperty("地图动作")
        private MapAction mapAction;

        @ApiModelProperty("日历动作")
        private CalendarAction calendarAction;

        @ApiModelProperty("打开链接")
        private UrlAction urlAction;

        @Data
        static class DialerAction {

            @ApiModelProperty("拨号配置")
            private DialPhoneNumber dialPhoneNumber;

            @Data
            static class DialPhoneNumber {

                @ApiModelProperty("电话号码")
                private String phoneNumber;
            }

        }

        @Data
        public static class MapAction {

            @ApiModelProperty("位置显示")
            private ShowLocation showLocation;

            @Data
            static class ShowLocation {

                @ApiModelProperty("位置信息")
                private Location location;

                @ApiModelProperty("回调地址")
                private String fallbackUrl;

                @Data
                static class Location {

                    @ApiModelProperty("维度")
                    private Double latitude;

                    @ApiModelProperty("经度")
                    private Double longitude;

                    @ApiModelProperty("标签")
                    private String label;

                    public void setLabel(String label) {
                        this.label = "Googleplex";
                    }
                }
            }
        }

        @Data
        public static class CalendarAction {

            @ApiModelProperty("日历动作")
            private CreateCalendarEvent createCalendarEvent;

            @Data
            static class CreateCalendarEvent {
                @ApiModelProperty("开始时间")
                @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
                private LocalDateTime startTime;

                @ApiModelProperty("结束时间")
                @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
                private LocalDateTime endTime;

                @ApiModelProperty("标题")
                private String title;

                @ApiModelProperty("描述")
                private String description;
            }
        }

        @Data
        public static class UrlAction {

            @ApiModelProperty("打开配置")
            private OpenUrl openUrl;

            @Data
            static class OpenUrl {
                @ApiModelProperty("地址")
                private String url;

                @ApiModelProperty("打开方式 webview内部 browser外部")
                private String application;

            }
        }
    }

    /**
     * 固定菜单
     */
    @Data
    public static class Entries {
        @ApiModelProperty("回复")
        private Reply reply;

        @ApiModelProperty("菜单")
        private Menu menu;

        @ApiModelProperty("事件")
        private Action action;
    }

    /**
     * 建议回复
     */
    @Data
    public static class Postback {
        @ApiModelProperty("建议回复内容")
        private String data;
    }

    /**
     * wrapper 转 menuItem 列表
     *
     * @param items      固定菜单列表
     * @param menuTarget 菜单配置
     * @param appId      appId
     */
    public void getMenuItem(List<MenuItem> items, Menu menuTarget, Long appId) {
        Assert.notNull(menuTarget, new ServiceException("请配置菜单~"));
        menuTarget.getEntries().forEach(obj -> {
            Optional.ofNullable(obj.getReply()).ifPresent(reply -> {
                Assert.isTrue(StrUtil.isNotBlank(reply.getDisplayText()), new ServiceException("请填写建议回复菜单名称"));
                MenuItem item = new MenuItem();
                item.setId(reply.getId() != null ? reply.getId() : System.nanoTime());
                item.setPid(menuTarget.getId());
                item.setAppId(appId);
                item.setName(reply.getDisplayText());
                item.setPayload(JSONUtil.createObj().set("reply", JSON.parseObject(JSON.toJSONString(reply))).toString());
                item.setType(1);    //建议回复
                items.add(item);
            });

            Optional.ofNullable(obj.getAction()).ifPresent(action -> {
                Assert.isTrue(StrUtil.isNotBlank(action.getDisplayText()), new ServiceException("请填写建议事件菜单名称"));
                MenuItem item = new MenuItem();
                item.setId(action.getId() != null ? action.getId() : System.nanoTime());
                item.setPid(menuTarget.getId());
                item.setAppId(appId);
                item.setName(action.getDisplayText());
                item.setPayload(JSONUtil.createObj().set("action", JSON.parseObject(JSON.toJSONString(action))).toString());
                item.setType(2);    //建议操作
                items.add(item);
            });

            Optional.ofNullable(obj.getMenu()).ifPresent(menu -> {
                Assert.isTrue(StrUtil.isNotBlank(menu.getDisplayText()), new ServiceException("请填写目录菜单名称"));
                menu.setId(menu.getId() != null ? menu.getId() : System.nanoTime());
                if (CollectionUtil.isNotEmpty(menu.getEntries())) {
                    getMenuItem(items, menu, appId);
                }
                MenuItem item = new MenuItem();
                item.setId(menu.getId() != null ? menu.getId() : System.nanoTime());
                item.setPid(menuTarget.getId());
                item.setAppId(appId);
                item.setName(menu.getDisplayText());
                item.setWeight(menu.getWeight());
                item.setType(0);    //目录
                items.add(item);
            });
        });
    }


    @JsonIgnore
    public SuggestionItem transformSuggestionItem(Action action) {
        SuggestionItem item = new SuggestionItem();
        item.setDisplayText(this.getDisplayText());
        item.setWeight(this.getWeight());
        //菜单项类型（1建议回复，2跳转链接，3拨打电话，4地理位置，5任务事件
        switch (action.getType()) {
            case 1:
                action.setCalendarAction(null);
                action.setMapAction(null);
                action.setUrlAction(null);
                action.setDialerAction(null);
                break;
            case 2:
                action.setCalendarAction(null);
                action.setMapAction(null);
                action.setDialerAction(null);
                break;
            case 3:
                action.setCalendarAction(null);
                action.setMapAction(null);
                action.setUrlAction(null);
                break;
            case 4:
                action.setCalendarAction(null);
                action.setUrlAction(null);
                action.setDialerAction(null);
                break;
            case 5:
                action.setMapAction(null);
                action.setUrlAction(null);
                action.setDialerAction(null);
                break;
        }
        if (action.getType() != 1) {
            Postback postback = new Postback();
            postback.setData(this.getDisplayText());
            action.setPostback(postback);
        }
        String key = action.getType() != 1 ? "action" : "reply";
        item.setPayload(JSONUtil.createObj().set(key, JSON.parseObject(JSON.toJSONString(action))).toString());
        item.setAppId(this.getAppId());
        item.setType(this.getType());
        item.setId(this.getId());
        return item;
    }
}
