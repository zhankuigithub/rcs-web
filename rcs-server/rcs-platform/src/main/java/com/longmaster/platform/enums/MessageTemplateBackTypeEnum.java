package com.longmaster.platform.enums;

public enum MessageTemplateBackTypeEnum {

    ZERO(0, "不回落"), ONE(1, "Chatbot H5"), TWO(2, "文本短信"), THREE(3, "UP 1.0");

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

    MessageTemplateBackTypeEnum(int type, String mode) {
        this.type = type;
        this.mode = mode;
    }

    public static String getModeByType(int type) {
        MessageTemplateBackTypeEnum[] modeEnums = MessageTemplateBackTypeEnum.values();

        for (MessageTemplateBackTypeEnum modeEnum : modeEnums) {
            if (type == modeEnum.type) {
                return modeEnum.mode;
            }
        }
        return null;
    }
}
