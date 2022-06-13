package com.longmaster.platform.enums;

public enum UploadModeEnum {

    PERM(1, "perm"), TEMP(2, "temp");

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

    UploadModeEnum(int type, String mode) {
        this.type = type;
        this.mode = mode;
    }

    public static String getModeByType(int type) {
        UploadModeEnum[] modeEnums = UploadModeEnum.values();

        for (UploadModeEnum modeEnum : modeEnums) {
            if (type == modeEnum.type) {
                return modeEnum.mode;
            }
        }
        return null;
    }


}
