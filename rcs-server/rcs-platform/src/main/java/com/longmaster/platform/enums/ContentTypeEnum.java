package com.longmaster.platform.enums;

public enum ContentTypeEnum {

    TEXT("text/plain;charset=UTF-8", 1),
    JSON("application/vnd.gsma.botsuggestion.response.v1.0+json", 2),
    FILE("application/vnd.gsma.rcs-ft-http+xml", 3),
    CLIENT("application/vnd.gsma.botsharedclientdata.v1.0+json", 4);

    private String mode;
    private int type;

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

    ContentTypeEnum(String mode, int type) {
        this.mode = mode;
        this.type = type;
    }

    public static Integer getTypeByMode(String mode) {
        ContentTypeEnum[] modeEnums = ContentTypeEnum.values();
        for (ContentTypeEnum modeEnum : modeEnums) {
            if (modeEnum.getMode().equalsIgnoreCase(mode)) {
                return modeEnum.getType();
            }
        }
        return null;
    }
}
