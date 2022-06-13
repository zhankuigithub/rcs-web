package com.longmaster.core.util;

import com.longmaster.core.http.HttpConfig;
import com.longmaster.core.http.HttpRequestBody;
import com.longmaster.core.http.OkHttpRequestImpl;
import okhttp3.Response;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * author zk
 * date 2022/1/6 10:45
 * description 发送短信帮助类
 */
public class SmsTemplateUtil {

    private static final OkHttpRequestImpl okHttpRequest = new OkHttpRequestImpl(HttpConfig.defaultConfig());

    public static String getSign(String appId, String clientId, String clientKey, String mobile, String templateId) {
        String _str = "app_id=" + appId + "&client_id=" + clientId + "&client_key=" + clientKey + "&mobile=" + mobile + "&template_id=" + templateId;
        return DigestUtils.md5DigestAsHex(_str.getBytes());
    }

    public static String getReqUri(String apiUrl, String appId, String clientId, String clientKey, String mobile, String templateId) {
        String sign = SmsTemplateUtil.getSign(appId, clientId, clientKey, mobile, templateId);
        return apiUrl + "?client_id=" + clientId + "&sign=" + sign;
    }

    /**
     * 接口请求方法
     *
     * @param uriWithSign 带sign的请求地址
     * @param smsInfo     信息结构字符串
     * @return
     */
    public static Response apiRequest(String uriWithSign, String smsInfo) {
        try {
            HttpRequestBody body = new HttpRequestBody();
            Map postData = new HashMap<>();
            postData.put("sms_info", smsInfo);
            body.setBodyForm(postData);
            body.setBodyType(HttpRequestBody.BODY_TYPE_FORM);
            return okHttpRequest.POST(uriWithSign, body, null);
        } catch (Exception e) {
            return null;
        }
    }
}
