package com.longmaster.core.http;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpRequestImpl implements IHttpRequest {

    private OkHttpClient mClient;

    public OkHttpRequestImpl(HttpConfig config) {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(config.getMaxRequest());
        dispatcher.setMaxRequestsPerHost(config.getMaxRequestsPerHost());

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS);
        builder.readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS);
        builder.writeTimeout(config.getWriteTimeout(), TimeUnit.MILLISECONDS);
        builder.dispatcher(dispatcher);

        mClient = builder.build();
    }

    @Override
    public Response GET(String url) throws IOException {
        return GET(url, null, null);
    }

    @Override
    public Response GET(String url, Map<String, String> queryParams, Map headers) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (queryParams != null) {
            for (String key : queryParams.keySet()) {
                urlBuilder.addQueryParameter(key, queryParams.get(key));
            }
        }

        return requestWithGet(urlBuilder.build(), headers);
    }

    @Override
    public Response POST(String url, Map headers) throws IOException {
        return requestWithPost(HttpUrl.parse(url).newBuilder().build(), null, headers);
    }

    @Override
    public Response POST(String url, HttpRequestBody body, Map headers) throws IOException {
        return requestWithPost(HttpUrl.parse(url).newBuilder().build(), body, headers);
    }

    @Override
    public Response DELETE(String url, Map headers) throws IOException {
        return requestWithDelete(HttpUrl.parse(url).newBuilder().build(), null, headers);
    }

    @Override
    public Response DELETE(String url, HttpRequestBody body, Map headers) throws IOException {
        return requestWithDelete(HttpUrl.parse(url).newBuilder().build(), body, headers);
    }

    private Response requestWithGet(HttpUrl url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.get();

        if (headers != null) {
            builder.headers(Headers.of(headers));
        }

        Request request = builder.build();
        return mClient.newCall(request).execute();
    }


    private Response requestWithPost(HttpUrl url, HttpRequestBody body, Map headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(adapterRequestBody(body));

        if (headers != null) {
            builder.headers(Headers.of(headers));
        }

        Request request = builder.build();
        return mClient.newCall(request).execute();
    }

    private Response requestWithDelete(HttpUrl url, HttpRequestBody body, Map headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.delete(adapterRequestBody(body));

        if (headers != null) {
            builder.headers(Headers.of(headers));
        }

        Request request = builder.build();
        return mClient.newCall(request).execute();
    }

    private RequestBody adapterRequestBody(HttpRequestBody body) {

        if (body == null) {
            return RequestBody.create(null, "");
        }

        int bodyType = body.getBodyType();

        switch (bodyType) {

            case HttpRequestBody.BODY_TYPE_FORM: {
                FormBody.Builder builder = new FormBody.Builder();
                for (Map.Entry<String, String> entry : body.getBodyForm().entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
                return builder.build();
            }

            case HttpRequestBody.BODY_TYPE_MULTIPART_FORM: {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                if (body.getBodyFileForm() != null) {
                    for (Map.Entry<String, FileBody> entry : body.getBodyFileForm().entrySet()) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse(entry.getValue().getMediaType()), entry.getValue().getSource());
                        builder.addFormDataPart(entry.getKey(), entry.getValue().getFileName(), requestBody);
                    }
                }

                if (body.getBodyForm() != null) {
                    for (Map.Entry<String, String> entry : body.getBodyForm().entrySet()) {
                        builder.addFormDataPart(entry.getKey(), entry.getValue());
                    }
                }
                return builder.build();
            }

            default:
                return RequestBody.create(MediaType.get("application/json; charset=utf-8"), body.getJsonString());
        }

    }

}
