package com.longmaster.rcs.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "lfasr")
public class Lfasr {

    @Value("${lfasr.tmpDir}")
    private String tmpDir;

    @Value("${lfasr.appId}")
    private String appId;

    @Value("${lfasr.secretKey}")
    private String secretKey;
}
