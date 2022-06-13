package com.longmaster.platform.enums;

public enum MessageStatusEnum {

    ZERO(0, "未发送"), ONE(1, "已发送"), TWO(2, "发送失败"),
    THREE(3, "消息已送达"), FOUR(4, "已阅读"), FIVE(5, "已转短"),
    SIX(6, "已撤回"), SEVEN(7, "机器人未开通"), EIGHT(8, "机器人未审核"), NINE(9, "素材未审核"),
    TEN(10, "用户未登录"), NOTFIND(404, "未知错误");

    private int type;
    private String mode;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    MessageStatusEnum(int type, String mode) {
        this.type = type;
        this.mode = mode;
    }

    public static String getModeByType(int type) {
        MessageStatusEnum[] modeEnums = MessageStatusEnum.values();

        for (MessageStatusEnum modeEnum : modeEnums) {
            if (type == modeEnum.type) {
                return modeEnum.mode;
            }
        }
        return null;
    }
}
