package com.longmaster.platform.enums;

/**
 * author zk
 * date 2021/3/9 9:37
 * description 运营商枚举
 */
public enum CarrierEnum {

    CMCC(1L, "中国移动"), CUCC(2L, "中国联通"), CTCC(3L, "中国电信"), LONGMASTER(4L, "朗玛信息"), GXCMCC(5L, "广西移动");

    private Long type;
    private String mode;

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    CarrierEnum(Long type, String mode) {
        this.type = type;
        this.mode = mode;
    }

    public static String getNameByType(Long type) {
        CarrierEnum[] modeEnums = CarrierEnum.values();
        for (CarrierEnum modeEnum : modeEnums) {
            if (type == modeEnum.type) {
                return modeEnum.mode;
            }
        }
        return null;
    }
}
