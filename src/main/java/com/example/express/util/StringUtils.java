package com.example.express.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jitwxs
 * @date 2019年04月17日 0:32
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static boolean isValidTel(String tel) {
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
