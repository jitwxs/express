package com.example.express.common.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jitwxs
 * @date 2019年04月17日 0:32
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static final String EMPTY = "";

    public static Double toDouble(String s) {
        return toDouble(s, null);
    }

    /***
     * 转换object类型参数
     */
    public static Double toDouble(Object s, Double defaultValue) {
        if (s == null) {
            return defaultValue;
        }
        return toDouble(s.toString(), defaultValue);
    }

    public static Double toDouble(String s, Double defaultValue) {

        if (s == null || "null".equalsIgnoreCase(s.toString().trim()) || "".equals(s.trim())) { return defaultValue; }
        try {
            Double d = Double.parseDouble(s.trim());
            if (Objects.equals(d, Double.POSITIVE_INFINITY)) {
                return defaultValue;
            }
            if (Objects.equals(d, Double.NEGATIVE_INFINITY)) {
                return defaultValue;
            }
            if (Objects.equals(d, Double.NaN)) {
                return defaultValue;
            }
            return d;
        } catch (Exception e) {
            //log.error("", e);
            return defaultValue;
        }
    }

    public static Integer toInteger(String s) {
        return toInteger(s, -1);
    }

    public static Integer toInteger(String s, Integer defaultValue) {
        if (s == null || "".equals(s.trim()) || !s.matches("^[-+]?[0-9]+$")) { return defaultValue; }
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            //log.error("", e);
            return defaultValue;
        }
    }

    public static Long toLong(String s) {
        return toLong(s, null);
    }

    public static Long toLong(String s, Long defaultValue) {
        if (s == null || "null".equals(s) || "".equals(s.trim())) { return defaultValue; }
        try {
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            //log.error("long parse exception", e);
            return defaultValue;
        }
    }

    public static boolean containsSpecial(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static boolean containsNumber(String str) {
        if(isBlank(str)) {
            return false;
        }
        char[] chars = str.toCharArray();
        for(char i : chars) {
            if(Character.isDigit(i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidTel(String tel) {
        if(isBlank(tel)) {
            return false;
        }

        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (tel.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(tel);
            return m.matches();
        }
    }
}
