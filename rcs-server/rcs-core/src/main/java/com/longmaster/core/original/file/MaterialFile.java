package com.longmaster.core.original.file;

import lombok.Data;

/**
 * author zk
 * date 2021/3/16 11:21
 * description 素材文件
 */
@Data
public class MaterialFile {
    private String url;
    private String contentType;
    private Integer fileSize;
    private String until;
    private String fileName;

    public MaterialFile() {
        this.contentType = "image/png";
        this.fileSize = 1024;
    }

    public MaterialFile(String url) {
        this.url = url;
        this.contentType = "image/png";
        this.fileSize = 1024;
    }

}
