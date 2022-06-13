package com.longmaster.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
public class JsonParam {

    private String json;

    public void setJson(String json) {
        this.json = json;
    }

    public <T> T convertTo(Class<T> cls) {
        try {
            return (new ObjectMapper()).readValue(this.json, cls);
        } catch (IOException var3) {
            var3.printStackTrace();
            return null;
        }
    }

}
