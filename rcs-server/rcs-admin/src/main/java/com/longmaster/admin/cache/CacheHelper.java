package com.longmaster.admin.cache;

public class CacheHelper {

    public static String buildAppCacheKey(String key, String appId) {
        return key + "_" + appId;
    }

    public static String buildChatBotCacheKey(String key, String chatBotId) {
        return key + "_" + chatBotId;
    }

    public static String buildUserIdPhoneCacheKey(String key, String phone) {
        return key + "_" + phone;
    }

    public static String buildSceneCacheKey(String key, String phone) {
        return key + "_" + phone;
    }

    public static String buildNodeCacheKey(String key, String phone) {
        return key + "_" + phone;
    }

}
