package com.longmaster.core.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextUtil {

    private static final String enSpecailChar = "~`!@#$%^&*()_+|;\\-=:\"<>?,.'\\/\\\\\\{}\\[\\]";
    private static final String cnSpecailChar = "～\n｀\n！\n \n＠\n＃\n＄\n％\n＾\n＆\n＊\n（\n）\n＿\n＋\n｜\n－\n＝\n＼\n＜\n＞\n？\n，\n．\n／\n：\n＂\n＇\n；\n。\n、\n‘\n“\n’\n”\n【\n】\n……\n￥\n·\n《\n》\n—\n•\n〗\n〖\n×\n——";

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    /**
     * 连接字符串，类似PHP的implode方法
     *
     * @param delimit 分隔符
     * @param array
     * @return
     */
    public static String join(String delimit, Object[] array) {
        if (array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                builder.append(delimit);
            }
            builder.append(array[i]);
        }
        return builder.toString();
    }

    public static String join(String delimit, List list) {
        if (list.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                builder.append(delimit);
            }
            builder.append(list.get(i));
        }
        return builder.toString();
    }

    public static int[] splitToIntArray(String src, String delimit) {
        String[] strings = src.split(delimit);
        int[] numbers = new int[strings.length];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Integer.parseInt(strings[i]);
        }
        return numbers;
    }

    /**
     * 将驼峰式命名的字符串转换为下划线大写方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。
     * 例如：HelloWorld->HELLO_WORLD
     *
     * @param name       转换前的驼峰式命名的字符串
     * @param suffixList 避免类型xxxID,xxxDT类似字符串被添加下划线
     * @return 转换后下划线大写方式命名的字符串
     */
    public static String underlineName(String name, List<String> suffixList) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {

            String nameSuffix = "";
            if (suffixList != null) {
                for (String suffix : suffixList) {
                    if (name.endsWith(suffix)) {
                        nameSuffix = suffix;
                        break;
                    }
                }
            }

            // 将第一个字符处理成大写
            result.append(name.substring(0, 1).toUpperCase());
            // 循环处理其余字符
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);

                if (nameSuffix.length() > 0 && (i > (name.length() - nameSuffix.length()))) {
                    // 其他字符直接转成大写
                    result.append(s.toUpperCase());
                    continue;
                }
                // 在大写字母前添加下划线
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // 其他字符直接转成大写
                result.append(s.toUpperCase());
            }
        }
        return result.toString();
    }

    public static String escapeSql(String str) {
        if (str == null) {
            return null;
        }

        str = str.replaceAll("\\\\", "\\\\\\\\");
        str = str.replaceAll("\\n", "\\\\n");
        str = str.replaceAll("\\r", "\\\\r");
        str = str.replaceAll("\\t", "\\\\t");
        str = str.replaceAll("\\00", "\\\\0");
        str = str.replaceAll("'", "\\\\'");
        str = str.replaceAll("\\\"", "\\\\\"");
        return str;
    }

    public static String unescapeSql(String str) {
        if (str == null) {
            return null;
        }

        str = str.replaceAll("\\\\\\\\", "\\\\");
        str = str.replaceAll("\\\\n", "\n");
        str = str.replaceAll("\\\\r", "\r");
        str = str.replaceAll("\\\\t", "\t");
        str = str.replaceAll("\\\\0", "\00");
        str = str.replaceAll("\\\\'", "'");
        str = str.replaceAll("\\\\\"", "\"");
        return str;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    public static String filterSpecialChar(String content) {
        content = content.replaceAll("\\s", "");
        content = content.replaceAll("[" + enSpecailChar + "]", "");
        for (String cnChar : cnSpecailChar.split("\n")) {
            content = content.replaceAll(cnChar.trim(), "");
        }
        return content;
    }

}
