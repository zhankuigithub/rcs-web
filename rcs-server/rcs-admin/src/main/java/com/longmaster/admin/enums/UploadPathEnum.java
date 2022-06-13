package com.longmaster.admin.enums;

import lombok.Getter;

/**
 * minio上传路径
 */
public enum UploadPathEnum {
    ADMIN("admin"),
    CUSTOMER("customer"),
    MATERIAL("material"),
    UNIAPP("uniapp");

    @Getter
    private String path;

    UploadPathEnum(String path) {
        this.path = path;
    }
}
