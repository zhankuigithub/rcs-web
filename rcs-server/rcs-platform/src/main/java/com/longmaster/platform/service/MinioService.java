package com.longmaster.platform.service;

import com.longmaster.platform.enums.UploadPathEnum;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * author zk
 * date 2021/11/22 13:55
 * description minio操作类
 */
public interface MinioService {

    // 上传
    String uploadFile(MultipartFile file, String[] fileType, UploadPathEnum pathEnum);

    // 批量上传
    Map<String, String> uploadFile(MultipartFile[] files, String[] fileType, UploadPathEnum pathEnum);

    // 上传，按字节
    String uploadFile(byte[] bytes, String fileName, String[] fileType, String contentType, UploadPathEnum pathEnum);

    // 下载（包含minio的url与普通开放访问的url）
    byte[] download(String sourceUrl);

    // 删除
    void remove(String sourceUrl);
}
