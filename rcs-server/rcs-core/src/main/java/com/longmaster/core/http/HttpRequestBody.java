package com.longmaster.core.http;

import java.util.Map;

public class HttpRequestBody {

    public static final int BODY_TYPE_FORM = 1; // 普通post
    public static final int BODY_TYPE_MULTIPART_FORM = 2; // 文件的post

    private int mBodyType;

    private String mJsonString;
    private Map<String, String> mBodyForm;
    private Map<String, FileBody> mBodyFileForm;

    public HttpRequestBody() {
    }

    public HttpRequestBody(Map<String, FileBody> mBodyFileForm) {
        this.mBodyType = BODY_TYPE_MULTIPART_FORM;
        this.mBodyFileForm = mBodyFileForm;
    }

    public String getJsonString() {
        return mJsonString;
    }

    public void setJsonString(String mJsonString) {
        this.mJsonString = mJsonString;
    }

    public int getBodyType() {
        return mBodyType;
    }

    public void setBodyType(int mBodyType) {
        this.mBodyType = mBodyType;
    }

    public Map<String, String> getBodyForm() {
        return mBodyForm;
    }

    public void setBodyForm(Map<String, String> mBodyForm) {
        this.mBodyForm = mBodyForm;
    }

    public Map<String, FileBody> getBodyFileForm() {
        return mBodyFileForm;
    }

    public void setBodyFileForm(Map<String, FileBody> mBodyFileForm) {
        this.mBodyFileForm = mBodyFileForm;
    }


}
