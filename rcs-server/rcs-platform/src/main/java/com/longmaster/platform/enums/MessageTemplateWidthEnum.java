package com.longmaster.platform.enums;

public enum MessageTemplateWidthEnum {

    SMALL_WIDTH(1, "SMALL_WIDTH"), MEDIUM_WIDTH(2, "MEDIUM_WIDTH");

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

    MessageTemplateWidthEnum(int type, String mode) {
        this.type = type;
        this.mode = mode;
    }

    public static String geModeByType(int type) {
        MessageTemplateWidthEnum[] modeEnums = MessageTemplateWidthEnum.values();
        for (MessageTemplateWidthEnum modeEnum : modeEnums) {
            if (type == modeEnum.type) {
                return modeEnum.mode;
            }
        }
        return null;
    }

}
