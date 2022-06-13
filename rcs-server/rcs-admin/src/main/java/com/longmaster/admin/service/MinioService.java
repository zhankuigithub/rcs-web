package com.longmaster.admin.service;

import com.longmaster.admin.enums.UploadPathEnum;
import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

    String getURI(String resource);

    String buildMinIoURL(String uri);

    String uploadFile(MultipartFile file, String[] fileType, UploadPathEnum pathEnum);
}
