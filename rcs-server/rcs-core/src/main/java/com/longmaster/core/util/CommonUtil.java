package com.longmaster.core.util;

import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    private static final String enSpecailChar = "~`!@#$%^&*()_+|;\\-=:\"<>?,.'\\/\\\\\\{}\\[\\]";
    private static final String cnSpecailChar = "～\n｀\n！\n \n＠\n＃\n＄\n％\n＾\n＆\n＊\n（\n）\n＿\n＋\n｜\n－\n＝\n＼\n＜\n＞\n？\n，\n．\n／\n：\n＂\n＇\n；\n。\n、\n‘\n“\n’\n”\n【\n】\n……\n￥\n·\n《\n》\n—\n•\n〗\n〖\n×\n——";

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    /**
     * @param str   原始字符串
     * @param start 开始位置
     * @param end   结束位置
     * @return 返回结果字符串
     */
    public static String getStr(String str, int start, int end) {
        if (StringUtils.isEmpty(str) || str.isEmpty()) {
            return str;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > end) {
            String resultStr = str.substring(end, start);
            return resultStr;
        }
        String resultStr = str.substring(start, end);
        return resultStr;
    }

    public static String getStrWithTag(String str, String tag1, String tag2) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        int start = str.indexOf(tag1);
        int end = str.indexOf(tag2);
        String resultStr = str.substring(start, end);
        return resultStr;
    }

    /**
     * 是否是中文
     *
     * @param c
     * @return
     */

    public static boolean isChinese(char c) {

        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION

                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

            return true;

        }

        return false;

    }

    /**
     * 是否是英文
     *
     * @param charaString
     * @return
     */
    public static boolean isEnglish(String charaString) {

        return charaString.matches("^[a-zA-Z]*");

    }

    /**
     * 是否为中文
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {

        String regEx = "[\\u4e00-\\u9fa5]+";

        Pattern p = Pattern.compile(regEx);

        Matcher m = p.matcher(str);

        if (m.find())

            return true;

        else

            return false;

    }


    /**
     * @param map
     * @return
     * @description 根据key排序
     */
    public static String getSortJsonMap(Map map) {
        TreeMap sort = MapUtil.sort(map);
        Iterator iterator = sort.keySet().iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            Object o = sort.get(next);
            if (o instanceof Map) {
                sort.put(next, getSortJsonMap((Map) o));
            }
        }
        try {
            return sObjectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param paraMap
     * @param urlEncode
     * @param keyToLower
     * @return
     * @description 根据key排序
     */
    public static String formatUrlMap(Map<String, Object> paraMap, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        Map<String, Object> tmpMap = paraMap;
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, Comparator.comparing(Map.Entry::getKey));

            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
                if (StringUtils.isNotBlank(item.getKey())) {
                    String key = item.getKey();
                    String val = String.valueOf(item.getValue());
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower) {
                        buf.append(key.toLowerCase() + "=" + val);
                    } else {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }

            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 使用默认替换符替换为换行
     *
     * @param initStr
     * @return
     */
    public static String replaceStrByInit(String initStr) {
        String[] qus = initStr.split("##");
        String q = "";
        for (int i = 0; i < qus.length; i++) {
            q += qus[i];
            if (i != qus.length - 1) {
                q += "\r\n";
            }

        }
        return q;
    }

    public static int[] randomNumber(int num, int max) {
        Random random = new Random();
        int[] arr = new int[5];
        arr[0] = random.nextInt(29);
        int i = 1;
        while (i <= 4) {
            int x = random.nextInt(29);
            for (int j = 0; j <= i - 1; j++) {
                if (arr[j] == x) {
                    break;
                }
                if (j + 1 == i) {
                    arr[i] = x;
                    i++;
                }
            }
        }
        return arr;
    }

    /**
     * 过滤特殊词语
     *
     * @param word
     * @return
     */
    public static String filterSuperChar(String word) {
        word = word.replaceAll("\\s", "");
        word = word.replaceAll("[" + enSpecailChar + "]", "");
        for (String cnChar : cnSpecailChar.split("\n")) {
            word = word.replaceAll(cnChar.trim(), "");
        }
        return word;
    }

    /**
     * 取出11位手机号
     *
     * @param phone
     * @return
     */
    public static String filterPhone(String phone) {
        Pattern pattern = Pattern.compile("1[3456789]\\d{9}");
        //Pattern pattern = PatternPool.MOBILE;
        Matcher matcher = pattern.matcher(phone);
        boolean b = matcher.find(); //不需要循环
        if (b) {
            phone = matcher.group();
        }
        return phone;
    }
}
