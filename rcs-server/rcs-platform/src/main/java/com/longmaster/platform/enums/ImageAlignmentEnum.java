package com.longmaster.platform.enums;

public enum ImageAlignmentEnum {

    LEFT(1, "LEFT"), RIGHT(2, "RIGHT");

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

    ImageAlignmentEnum(int type, String mode) {
        this.type = type;
        this.mode = mode;
    }

    public static String geModeByType(int type) {
        ImageAlignmentEnum[] modeEnums = ImageAlignmentEnum.values();
        for (ImageAlignmentEnum modeEnum : modeEnums) {
            if (type == modeEnum.type) {
                return modeEnum.mode;
            }
        }
        return null;
    }
}
