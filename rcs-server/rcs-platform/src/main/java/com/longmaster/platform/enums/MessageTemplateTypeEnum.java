package com.longmaster.platform.enums;

public enum MessageTemplateTypeEnum {

    ONE(1, "文本消息"), TWO(2, "单卡片"),
    THREE(3, "多卡片"), FOUR(4, "地理消息"), FIVE(5, "图片"),
    SIX(6, "音频"), SEVEN(7, "视频");

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

    MessageTemplateTypeEnum(int type, String mode) {
        this.type = type;
        this.mode = mode;
    }

    public static String getModeByType(int type) {
        MessageTemplateTypeEnum[] modeEnums = MessageTemplateTypeEnum.values();

        for (MessageTemplateTypeEnum modeEnum : modeEnums) {
            if (type == modeEnum.type) {
                return modeEnum.mode;
            }
        }
        return null;
    }
}
