package com.longmaster.core.http;

/**
 * author zk
 * date 2021/2/7 15:59
 * description 文件实体
 */
public class FileBody {

    private String fileName;
    private byte[] source;
    private String mediaType = "application/octet-stream"; // 默认

    public FileBody(String fileName, byte[] source) {
        this.fileName = fileName;
        this.source = source;
    }

    public FileBody(String fileName, String mediaType, byte[] source) {
        this.fileName = fileName;
        this.mediaType = mediaType;
        this.source = source;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getSource() {
        return source;
    }

    public void setSource(byte[] source) {
        this.source = source;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
