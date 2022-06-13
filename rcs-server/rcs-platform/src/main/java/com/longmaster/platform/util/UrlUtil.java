package com.longmaster.platform.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class UrlUtil {

    public static String minIoUrl;

    @Value("${system.minio.endpoint:''}")
    private String endpoint;

    @PostConstruct
    public void init() {
        minIoUrl = endpoint;
    }

    /**
     * @param resource (完整的文件url)
     * @return
     */
    public static String getURI(String resource) {
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

    /**
     * @param ipHost
     * @param uri    (指path加上参数)
     * @return
     */
    public static String buildURL(String ipHost, String uri) {
        return ipHost + uri;
    }

    public static String buildMinIoURL(String uri) {
        return minIoUrl + uri;
    }

}
