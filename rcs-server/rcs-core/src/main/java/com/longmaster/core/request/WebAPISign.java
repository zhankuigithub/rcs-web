package com.longmaster.core.request;

import org.springframework.util.DigestUtils;

import java.util.*;

/**
 * webapi生成签名的实体类
 */
public class WebAPISign {

    private String appKey;

    public WebAPISign(String appKey) {
        this.appKey = appKey;
    }

    private String sign(Map<String, Object> params) {
        // 参数过滤
        this.emptyParamFiltering(params);
        // 字典排序
        List<String> sortKey = this.dictSort(params);
        // 字符串拼接
        String str = this.createLinkString(params, sortKey);
        str += "&key=" + this.appKey;
        // 生成签名
        String sign = DigestUtils.md5DigestAsHex(str.getBytes());
        // 所有字符转为大写并返回
        return sign.toUpperCase();
    }

    private void emptyParamFiltering(Map<String, Object> params) {
        params.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().toString().equals("") || entry.getKey().equals("sign"));
    }

    private List<String> dictSort(Map<String, Object> params) {
        Collection<String> keyset = params.keySet();
        List<String> list = new ArrayList<>(keyset);
        Collections.sort(list);
        return list;
    }

    private String createLinkString(Map<String, Object> params, List<String> sortKey) {
        StringBuilder linkString = new StringBuilder();
        for (int i = 0; i < sortKey.size(); i++) {
            if (i > 0) {
                linkString.append("&").append(sortKey.get(i)).append("=").append(params.get(sortKey.get(i)).toString());
            } else {
                linkString.append(sortKey.get(i)).append("=").append(params.get(sortKey.get(i)).toString());
            }
        }
        return linkString.toString();
    }

    public boolean verifySign(Map<String, Object> params) {
        String sign = params.get("sign").toString();
        return sign.equals(this.sign(params));
    }

    public String makeSign(Map<String, Object> params) {
        return this.sign(params);
    }
}
