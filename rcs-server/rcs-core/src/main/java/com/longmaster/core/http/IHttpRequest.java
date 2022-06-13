package com.longmaster.core.http;

import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * author zk
 * date 2021/2/7 15:14
 * description 专门处理电信和移动的上传
 */
public interface IHttpRequest {

    Response GET(String url) throws IOException;

    Response GET(String url, Map<String, String> queryParams, Map headers) throws IOException;

    Response POST(String url, Map headers) throws IOException;

    Response POST(String url, HttpRequestBody body, Map headers) throws IOException;

    Response DELETE(String url, Map headers) throws IOException;

    Response DELETE(String url, HttpRequestBody body, Map headers) throws IOException;

}
