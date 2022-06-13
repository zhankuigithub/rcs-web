package com.longmaster.core.original.file;

import lombok.Data;

/**
 * author zk
 * date 2021/3/16 11:20
 * description 素材封面
 */
@Data
public class MaterialThumb {

    private String url;
    private String contentType = "image/png";
    private Integer fileSize;
    private String until;
    private String fileName;


    public MaterialThumb() {
        this.fileSize = 1024;
    }

    public MaterialThumb(String url) {
        this.url = url;
        this.fileSize = 1024;
    }

}
