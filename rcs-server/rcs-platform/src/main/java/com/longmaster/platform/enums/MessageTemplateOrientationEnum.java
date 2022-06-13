package com.longmaster.platform.enums;

public enum MessageTemplateOrientationEnum {

    VERTICAL(1, "VERTICAL"), HORIZONTAL(2, "HORIZONTAL");

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

    MessageTemplateOrientationEnum(int type, String mode) {
        this.type = type;
        this.mode = mode;
    }

    public static String geModeByType(int type) {
        MessageTemplateOrientationEnum[] modeEnums = MessageTemplateOrientationEnum.values();
        for (MessageTemplateOrientationEnum modeEnum : modeEnums) {
            if (type == modeEnum.type) {
                return modeEnum.mode;
            }
        }
        return null;
    }


}
