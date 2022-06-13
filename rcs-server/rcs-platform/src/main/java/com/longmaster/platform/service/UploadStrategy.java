package com.longmaster.platform.service;

import com.longmaster.platform.entity.Material;

import java.io.IOException;

public interface UploadStrategy {

    Long getCarrierId();

    void upload(Material material, int uploadMode) throws IOException;

    String buildFileName(String sourceUrl);

    byte[] getBytes(String sourceUrl) throws IOException;

}
