package com.longmaster.platform.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.longmaster.core.http.HttpConfig;
import com.longmaster.core.http.OkHttpRequestImpl;
import com.longmaster.platform.enums.UploadPathEnum;
import com.longmaster.platform.service.MinioService;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.valid.Assert;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class MinioServiceImpl implements MinioService {

    @Resource
    private MinioClient minioClient;

    @Value("${system.minio.bucket:''}")
    private String bucketName;

    @Value("${system.minio.endpoint:''}")
    private String endpoint;

    @Value("${system.minio.enableExit}")
    private Boolean enableExit;

    private final OkHttpRequestImpl okHttpRequest = new OkHttpRequestImpl(HttpConfig.defaultConfig());

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

    @Override
    public Map<String, String> uploadFile(MultipartFile[] files, String[] fileType, UploadPathEnum pathEnum) {
        Map<String, String> map = new HashMap<>(16);
        for (MultipartFile file : files) {
            String fileUrl = uploadFile(file, fileType, pathEnum);
            map.put(file.getOriginalFilename(), fileUrl);
        }
        return map;
    }

    @Override
    public String uploadFile(byte[] bytes, String fileName, String[] fileType, String contentType, UploadPathEnum pathEnum) {
        MultipartFile file = new MockMultipartFile(fileName, bytes);

        String fileUrl = "";
        Assert.isFalse(Arrays.stream(fileType).noneMatch(fileName::endsWith), new ServerException("非法文件类型，建议使用'%s'格式文件!", ArrayUtil.join(fileType, "、")));
        String objectName = pathEnum.getPath() + "/" + System.nanoTime() + "." + fileName;

        try (InputStream stream = file.getInputStream()) {
            Assert.isTrue(bucketExists(), new ServerException(bucketName + "未创建，无法上传"));
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(stream, file.getSize(), file.getSize() + 10 * 1024 * 1024)
                    .contentType(contentType)
                    .build()
            );

            String searchKey = Base64Utils.encodeToUrlSafeString(objectName.getBytes()); //方便后期查找文件信息
            fileUrl = UrlBuilder.of(new URL(endpoint), StandardCharsets.UTF_8)
                    .addPath(bucketName)
                    .addPath(objectName)
                    .addQuery("key", searchKey)
                    .toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException("文件上传失败！");
        }
        return fileUrl;
    }

    @Override
    public byte[] download(String sourceUrl) {
        if (!StrUtil.isEmpty(sourceUrl)) {
            byte[] bytes = new byte[0];
            if (belongMinioFile(sourceUrl)) { // 判断是否为自己后台的数据
                String key = sourceUrl.substring(sourceUrl.lastIndexOf("key=") + 4); // 直接截取，防止密文存在等号取错
                try {
                    String objectName = new String(Base64Utils.decodeFromUrlSafeString(URLDecoder.decode(key, "UTF-8")));
                    GetObjectResponse stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
                    bytes = toByteArray(stream);
                } catch (Exception e) {
                    return null;
                }
            } else {
                try {
                    bytes = Objects.requireNonNull(okHttpRequest.GET(sourceUrl, null, null).body()).bytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bytes;
        }
        return null;
    }

    @Override
    public void remove(String sourceUrl) {
        if (!StrUtil.isEmpty(sourceUrl)) {
            String key = sourceUrl.substring(sourceUrl.lastIndexOf("key=") + 4);
            try {
                String objectName = new String(Base64Utils.decodeFromUrlSafeString(URLDecoder.decode(key, "UTF-8")));
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean bucketExists() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, io.minio.errors.ServerException, InternalException, XmlParserException, ErrorResponseException {
        // 检查存储桶是否已经存在
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (enableExit) {
            // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            return true;
        }
        return isExist;
    }

    public byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    private boolean belongMinioFile(String sourceUrl) {
        try {
            URL url = new URL(sourceUrl);
            return endpoint.contains(url.getHost());
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
