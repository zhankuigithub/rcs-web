package com.longmaster.rcs.service.csp;

import com.longmaster.rcs.service.csp.impl.CtAuthServiceStrategy;
import com.longmaster.rcs.service.csp.impl.CuAuthServiceStrategy;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CommonService {

    @Resource
    protected Environment env;

    @Resource
    private ApplicationContext applicationContext;

    /**
     * cache key
     */
    public static final String CSP_ACCESS_TOKEN = "RCS:CSP:ACCESS_TOKEN";

    public static final String ACCESS_TOKEN_KEY = "accessToken";

    /**
     * token 过期时间配置
     */
    public static final int CSP_TOKEN_EXPIRE_IN_S = 7200;

    protected static final String RANDOM_CODE_SOURCE = "qwertyuiopasdfghjklzxcvbnm1234567890";

    private static final Long CARRIER_CUCC = 2L;
    private static final Long CARRIER_CTCC = 3L;


    public StringBuilder baseUrl(long carrierId) {
        if (carrierId == CARRIER_CUCC) {
            return baseUrl(env.getProperty("cuccLmtxCsp.serverRoot"), env.getProperty("cuccLmtxCsp.apiVersion"), "/cspApi/");
        }
        if (carrierId == CARRIER_CTCC) {
            return baseUrl(env.getProperty("ctccLmCsp.serverRoot"), env.getProperty("ctccLmCsp.apiVersion"), "/ispApi/");
        }
        return null;
    }

    public StringBuilder baseUrl(String serverRoot, String apiVersion, String api) {
        return new StringBuilder(serverRoot)
                .append(api)
                .append(apiVersion);
    }


    /**
     * 公共请求头
     *
     * @return
     */
    public HttpHeaders baseHeaders(long carrierId) {
        return baseHeaders(MediaType.APPLICATION_JSON, true, carrierId);
    }

    /**
     * 公共请求头
     *
     * @return
     */
    public HttpHeaders baseHeaders(MediaType mediaType, long carrierId) {
        return baseHeaders(mediaType, true, carrierId);
    }

    /**
     * @param takeToken
     * @return
     */
    public HttpHeaders baseHeaders(MediaType mediaType, boolean takeToken, long carrierId) {
        String timestamp = LocalDateTime.now(ZoneOffset.ofHours(8)).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomString = RandomStringUtils.random(8, RANDOM_CODE_SOURCE);

        String signStr = "";
        if (carrierId == CARRIER_CUCC) {
            signStr = env.getProperty("cuccLmtxCsp.accessKey") + timestamp + randomString + timestamp;
        }
        if (carrierId == CARRIER_CTCC) {
            signStr = env.getProperty("ctccLmCsp.accessKey") + timestamp + randomString + timestamp;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("timestamp", timestamp);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("nonce", timestamp + randomString);
        headers.set("signature", DigestUtils.md5DigestAsHex(signStr.getBytes()));
        if (mediaType != null) {
            headers.setContentType(mediaType);
        }
        if (takeToken) {
            if (carrierId == CARRIER_CUCC) {
                headers.set("authorization", applicationContext.getBean(CuAuthServiceStrategy.class).getAccessToken());
            }
            if (carrierId == CARRIER_CTCC) {
                headers.set("authorization", applicationContext.getBean(CtAuthServiceStrategy.class).getAccessToken());
            }
        }

        return headers;
    }
}
