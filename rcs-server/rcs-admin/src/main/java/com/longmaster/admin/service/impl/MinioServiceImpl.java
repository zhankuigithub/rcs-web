package com.longmaster.admin.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.ArrayUtil;
import com.longmaster.admin.enums.UploadPathEnum;
import com.longmaster.admin.service.MinioService;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MinioServiceImpl implements MinioService {

    @Resource
    private MinioClient minioClient;

    @Value("${system.minio.bucket}")
    private String bucketName;

    @Value("${system.minio.endpoint}")
    private String endpoint;

    @Value("${system.minio.enableExit}")
    private Boolean enableExit;

    @Override
    public String getURI(String resource) {
        try {
            URL url = new URL(resource);
            if (url.getQuery() != null) {
                return url.getPath() + "?" + url.getQuery();
            }
            return url.getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return resource;
        }
    }

    @Override
    public String buildMinIoURL(String uri) {
        return endpoint + uri;
    }

    @Override
    public String uploadFile(MultipartFile file, String[] fileType, UploadPathEnum pathEnum) {
        String fileUrl = "";
        String filename = file.getOriginalFilename();
        Assert.isFalse(Arrays.stream(fileType).noneMatch(filename::endsWith), new ServerException("非法文件类型，建议使用'%s'格式文件!", ArrayUtil.join(fileType, "、")));

        String objectName = pathEnum.getPath() + "/" + System.nanoTime() + "." + FileUtil.getSuffix(filename);

        try (InputStream stream = file.getInputStream()) {
            Assert.isTrue(bucketExists(), new ServerException(bucketName + "未创建，无法上传"));
            Map<String, String> map = new HashMap<>();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(stream, file.getSize(), file.getSize() + 10 * 1024 * 1024)
                    .contentType(file.getContentType())
                    .build()
            );

            String searchKey = Base64Utils.encodeToUrlSafeString(objectName.getBytes()); //方便后期查找文件信息
            fileUrl = UrlBuilder.of(new URL(endpoint), StandardCharsets.UTF_8)
                    .addPath(bucketName)
                    .addPath(objectName)
                    .addQuery("key", searchKey)
                    .toString();
            log.info("[uploadFile] file {} visit url is  {}, upload file finished!", filename, fileUrl);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServerException("文件上传失败！");
        }
        return fileUrl;
    }

    public boolean bucketExists() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, io.minio.errors.ServerException, InternalException, XmlParserException, ErrorResponseException, InternalException {
        // 检查存储桶是否已经存在
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (enableExit) {
            // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            return true;
        }
        return isExist;
    }
}
