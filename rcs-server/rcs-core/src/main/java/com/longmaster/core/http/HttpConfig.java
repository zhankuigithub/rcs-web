package com.longmaster.core.http;


public class HttpConfig {
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 10000;
    private static final int DEFAULT_WRITE_TIMEOUT = 10000;
    private static final int DEFAULT_MAX_REQUESTS = 64;
    private static final int DEFAULT_MAX_REQUESTS_PER_HOST = 5;

    private int mConnectTimeout;
    private int mReadTimeout;
    private int mWriteTimeout;
    private int mMaxRequests;
    private int mMaxRequestsPerHost;

    public int getConnectTimeout() {
        return mConnectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        mConnectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return mReadTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        mReadTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return mWriteTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        mWriteTimeout = writeTimeout;
    }

    public void setMaxRequests(int maxRequests) {
        mMaxRequests = maxRequests;
    }

    public int getMaxRequest() {
        return mMaxRequests;
    }

    public void setMaxRequestsPerHost(int maxRequestsPerHost) {
        mMaxRequestsPerHost = maxRequestsPerHost;
    }

    public int getMaxRequestsPerHost() {
        return mMaxRequestsPerHost;
    }

    public static HttpConfig defaultConfig() {
        HttpConfig config = new HttpConfig();
        config.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        config.setReadTimeout(DEFAULT_READ_TIMEOUT);
        config.setWriteTimeout(DEFAULT_WRITE_TIMEOUT);
        config.setMaxRequests(DEFAULT_MAX_REQUESTS);
        config.setMaxRequestsPerHost(DEFAULT_MAX_REQUESTS_PER_HOST);

        return config;
    }
}
