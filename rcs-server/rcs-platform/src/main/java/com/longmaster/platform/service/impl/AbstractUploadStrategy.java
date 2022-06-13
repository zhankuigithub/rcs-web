package com.longmaster.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.longmaster.platform.service.MinioService;
import com.longmaster.platform.service.UploadStrategy;
import com.longmaster.platform.util.UrlUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

public abstract class AbstractUploadStrategy implements UploadStrategy {

    @Resource
    private MinioService minioService;

    @Override
    public String buildFileName(String sourceUrl) {
        try {
            if (StrUtil.isEmpty(sourceUrl)) {
                return null;
            }
            return UUID.randomUUID().toString().substring(8) + sourceUrl.substring(sourceUrl.lastIndexOf("."), sourceUrl.indexOf("?"));
        } catch (Exception ex) {
            return UUID.randomUUID().toString().substring(8) + ".png";
        }
    }

    @Override
    public byte[] getBytes(String sourceUrl) throws IOException {
        return minioService.download(UrlUtil.buildMinIoURL(sourceUrl));
    }

}
