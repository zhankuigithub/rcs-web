package com.longmaster.platform.enums;

import lombok.Getter;

/**
 * minio上传路径
 */
public enum UploadPathEnum {
    CUSTOMER("customer"),
    MATERIAL("material"),
    UNIAPP("uniapp");

    @Getter
    private String path;

    UploadPathEnum(String path) {
        this.path = path;
    }
}
