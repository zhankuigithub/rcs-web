package com.longmaster.core.enums;

public enum ContentType {

    TEXT("text", "text/plain"),

    FILE("file", "application/vnd.gsma.rcs-ft-http"),

    ANSWER("answer", "application/vnd.gsma.botsuggestion.response.v1.0+json"),

    SHARE("share", "application/vnd.gsma.botsharedclientdata.v1.0+json");

    private String type;

    private String value;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    ContentType(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getTypeValue(String type) {
        try {
            return ContentType.valueOf(type).getValue();
        } catch (Exception ex) {
            return "";
        }
    }
}
