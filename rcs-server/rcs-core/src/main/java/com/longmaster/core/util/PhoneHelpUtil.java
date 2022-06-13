package com.longmaster.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * author zk
 * date 2021/2/24 17:51
 * description 电话号码帮助类
 */
public class PhoneHelpUtil {

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    /**
     * 中国电信号码格式验证 手机段： 133,153,180,181,189,177,1700,173,199
     **/
    private static final String CHINA_TELECOM_PATTERN = "(^1(33|53|77|73|99|8[019])\\d{8}$)|(^1700\\d{7}$)";

    /**
     * 中国联通号码格式验证 手机段：130,131,132,155,156,185,186,145,176,1709,166
     **/
    private static final String CHINA_UNICOM_PATTERN = "(^1(3[0-2]|4[5]|5[5-6]|6[6]|7[6]|8[56])\\d{8}$)|(^1709\\d{7}$)";

    /**
     * 中国移动号码格式验证
     * 手机段：134,135,136,137,138,139,150,151,152,157,158,159,182,183,184,187,188,147,178,1705
     **/
    private static final String CHINA_MOBILE_PATTERN = "(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478]|9[5])\\d{8}$)|(^1705\\d{7}$)";


    /**
     * 查询电话属于哪个运营商
     *
     * @param tel 手机号码
     * @return 0：不属于任何一个运营商，1:移动，2：联通，3：电信
     */
    public static Integer isChinaMobilePhoneNum(String tel) {
        boolean b1 = tel == null || tel.trim().equals("") ? false : match(CHINA_MOBILE_PATTERN, tel);
        if (b1) {
            return 1;
        }
        b1 = tel == null || tel.trim().equals("") ? false : match(CHINA_UNICOM_PATTERN, tel);
        if (b1) {
            return 2;
        }
        b1 = tel == null || tel.trim().equals("") ? false : match(CHINA_TELECOM_PATTERN, tel);
        if (b1) {
            return 3;
        }
        return 0;
    }

    /**
     * 匹配函数
     *
     * @param regex
     * @param tel
     * @return
     */
    private static boolean match(String regex, String tel) {
        return Pattern.matches(regex, tel);
    }

    /**
     * author zk
     * date 2021/2/24 18:35
     * description 过滤号码
     */
    public static ArrayNode filter(ArrayNode phones, int channel) {
        ArrayNode arrayNode = sObjectMapper.createArrayNode();
        phones.forEach(item -> {
            if (isChinaMobilePhoneNum(item.asText()) == channel) {
                arrayNode.add("tel:+86" + item.asText());
            }
        });
        return arrayNode;
    }

    /**
     * author zk
     * date 2021/2/24 18:35
     * description 过滤号码
     */
    public static List<String> filter(List<String> phones, int channel) {
        channel = (channel == 5) ? 1 : channel;
        int finalChannel = channel;
        return phones.stream().filter(item -> isChinaMobilePhoneNum(item) == finalChannel).collect(Collectors.toList());
    }
//
//    public static ArrayNode filter(ArrayNode phones, int channel) {
//        ArrayNode arrayNode = sObjectMapper.createArrayNode();
//        phones.forEach(item -> {
//            if (isChinaMobilePhoneNum(item.asText()) == channel) {
//                arrayNode.add(item.asText());
//            }
//        });
//        return arrayNode;
//    }
//
//    /**
//     * author zk
//     * date 2021/2/24 18:35
//     * description 过滤号码
//     */
//    public static List<String> filter(List<String> phones, int channel) {
//        channel = (channel == 5) ? 1 : channel;
//        int finalChannel = channel;
//        return phones.stream().filter(item -> isChinaMobilePhoneNum(item) == finalChannel).collect(Collectors.toList());
//    }

}
