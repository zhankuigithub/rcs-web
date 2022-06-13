package com.longmaster.platform.enums;

public enum CardHeightEnum {

    SHORT_HEIGHT(1, "SHORT_HEIGHT"), MEDIUM_HEIGHT(2, "MEDIUM_HEIGHT"), TALL_HEIGHT(3, "TALL_HEIGHT");

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

    CardHeightEnum(int type, String mode) {
        this.type = type;
        this.mode = mode;
    }

    public static String geModeByType(int type) {
        CardHeightEnum[] modeEnums = CardHeightEnum.values();
        for (CardHeightEnum modeEnum : modeEnums) {
            if (type == modeEnum.type) {
                return modeEnum.mode;
            }
        }
        return null;
    }

}
