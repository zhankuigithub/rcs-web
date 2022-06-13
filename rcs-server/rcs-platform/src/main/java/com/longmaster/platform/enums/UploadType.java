package com.longmaster.platform.enums;

import lombok.Getter;

/**
 * 0：客户图片
 * 1：身份证正反面
 * 2：合同
 */
public enum UploadType {
    CUSTOMER_IMAGE("0"),
    ID_CARD("1"),
    CONTRACT("2");

    @Getter
    private String type;

    UploadType(String type) {
        this.type = type;
    }
}
