package com.example.express.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 *
 * 身份证18位分别代表的含义，身份证15位升级到18位，原来年用2位且没有最后一位，从左到右方分别表示
 * ①1-2 升级行政区代码
 * ②3-4 地级行政区划分代码
 * ③5-6 县区行政区分代码
 * ④7-10 11-12 13-14 出生年、月、日
 * ⑤15-17 顺序码，同一地区同年、同月、同日出生人的编号，奇数是男性，偶数是女性
 * ⑥18 校验码，如果是0-9则用0-9表示，如果是10则用X（罗马数字10）表示(最后一位可能出现的X并不是英文字母Ｘ，而是希腊数字10的缩写X)
 *
 * 只要将每位的对应权重乘以每个位上的数值，然后求和，最后与11求余数，得到的结果对比找到尾数即可。
 *
 * 原有15位身份证是没有校验位的，同时采用的是2位数字来表示出生年份
 *
 * 第一步：
 * 先对前17位数字的权求和 ,使用十七位数字本体码加权求和公式   S = Sum(Ai * Wi), i = 0, ... , 16
 * Ai:表示第i位置上的身份证号码数字值(0~9)
 * Wi:7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2 （固定的，表示第i位置上的加权因子）
 * 将身份证号码的第1位数字与7相乘；
 * 将身份证号码的第2位数字与9相乘；
 * 将身份证号码的第3位数字与10相乘；
 * 将身份证号码的第4位数字与5相乘；
 * 将身份证号码的第5位数字与8相乘；
 * 将身份证号码的第6位数字与4相乘；
 * 将身份证号码的第7位数字与2相乘；
 * 将身份证号码的第8位数字与1相乘；
 * 将身份证号码的第9位数字与6相乘；
 * 将身份证号码的第10位数字与3相乘；
 * 将身份证号码的第11位数字与7相乘；
 * 将身份证号码的第12位数字与9相乘；
 * 将身份证号码的第13位数字与10相乘；
 * 将身份证号码的第14位数字与5相乘；
 * 将身份证号码的第15位数字与8相乘；
 * 将身份证号码的第16位数字与4相乘；
 * 将身份证号码的第17位数字与2相乘。
 *
 * 第二步：
 * 将第一步身份证号码1~17位相乘的结果求和，全部加起来。
 *
 * 第三步：
 * 计算模  Y = mod(S, 11)
 * 用第二步计算出来的结果除以11，这样就会出现
 * 余数为0，
 * 余数为1，
 * 余数为2，
 * 余数为3，
 * 余数为4，
 * 余数为5，
 * 余数为6，
 * 余数为7，
 * 余数为8，
 * 余数为9，
 * 余数为10
 * 共11种可能性。
 *
 * 第四步：
 * 根据模，查找得到对应的校验码
 * Y:    0 1 2 3 4 5 6 7 8 9 10
 * 校验码: 1 0 X 9 8 7 6 5 4 3 2
 * 如果余数为0，那对应的最后一位身份证的号码为1；
 * 如果余数为1，那对应的最后一位身份证的号码为0；
 * 如果余数为2，那对应的最后一位身份证的号码为X；
 * 如果余数为3，那对应的最后一位身份证的号码为9；
 * 如果余数为4，那对应的最后一位身份证的号码为8；
 * 如果余数为5，那对应的最后一位身份证的号码为7；
 * 如果余数为6，那对应的最后一位身份证的号码为6；
 * 如果余数为7，那对应的最后一位身份证的号码为5；
 * 如果余数为8，那对应的最后一位身份证的号码为4；
 * 如果余数为9，那对应的最后一位身份证的号码为3；
 * 如果余数为10，那对应的最后一位身份证的号码为2。
 *
 */
public class IDValidateUtils {

    static String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};//十七位数字本体码权重

    static String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2"}; //最后一位的校验码字符值

    public static void main(String[] args) {
        System.out.println(check("41272519910506****"));
    }

    /***
     * 验证身份证
     * @param idStr
     * @return
     */
    public static boolean check(String idStr) {
        try {
            if (null == idStr) {// 验证非空
//                System.out.println("身份证号码不能为空");
                return false;
            }
            if (idStr.length() != 15 && idStr.length() != 18) {// 只能是15位或者18位
//                System.out.println("身份证号码长度只能是15位或者18位");
                return false;
            }

            String Ai = "";
            if (idStr.length() == 18) {
                Ai = idStr.substring(0, 17);
            }

            if (idStr.length() == 15) {//  将15位身份证转换为 17位身份证，最后加上最后一位，转换为18位身份证
                // 15位身份证是没有校验位的，同时采用的是2位数字来表示出生年份，并且 15位的身份证号码确定都是19**年的
                Ai = idStr.substring(0, 6) + "19" + idStr.substring(6, 15);
            }
            if (!isNumber(Ai)) { // 验证身份证前17位是否都是数字
//                System.out.println("身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。");
                return false;
            }

            int year = Integer.parseInt(Ai.substring(6, 10));// 出生年份
            int month = Integer.parseInt(Ai.substring(10, 12));// 出生月份
            int day = Integer.parseInt(Ai.substring(12, 14));// 出生日期
            String birthDay = year + "-" + month + "-" + day;

            Date birthdate = null;
            try {// 将出生日期转换为Date类型
                birthdate = new SimpleDateFormat("yyyyMMdd").parse(birthDay);
            } catch (ParseException e) {
                e.printStackTrace();
//                System.out.println("身份证生日无效。");
                return false;
            }
            if (birthdate == null || new Date().before(birthdate)) {
//                System.out.println("身份证生日无效。");
                return false;
            }

            GregorianCalendar gc = new GregorianCalendar();//GregorianCalendar 是 Calendar 的一个具体子类，提供了世界上大多数国家/地区使用的标准日历系统。
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
            // 验证生日年份是否在范围之内
            if ((gc.get(Calendar.YEAR) - year) > 150 || (gc.getTime().getTime() - s.parse(birthDay).getTime()) < 0) {
//                System.out.println("身份证生日不在有效范围。");
                return false;
            }

            //验证月份
            if (month > 12 || month <= 0) {
//                System.out.println("身份证号中的月份无效");
                return false;
            }

            //验证日期
            gc.setTime(birthdate);
            boolean mflag = false;
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    mflag = (day >= 1 && day <= 31);
                    break;
                case 2: // 公历的2月非闰年有28天,闰年的2月是29天。
                    if (gc.isLeapYear(gc.get(Calendar.YEAR))) {
                        mflag = (day >= 1 && day <= 29);
                    } else {
                        mflag = (day >= 1 && day <= 28);
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    mflag = (day >= 1 && day <= 30);
                    break;
            }
            if (!mflag) {// 日期不对
//                System.out.println("省份证号中的出生日期不对");
                return false;
            }
            // 验证 开头两位数是否是真实有效的地区编码
            if (cityCodeMap.get(Ai.substring(0, 2)) == null) {
//                System.out.println("身份证地区编码错误。");
                return false;
            }

            int TotalmulAiWi = 0;
            for (int i = 0; i < 17; i++) {//先对前17位数字的权求和 ,使用十七位数字本体码加权求和公式   S = Sum(Ai * Wi)
                TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
            }
            int modValue = TotalmulAiWi % 11;//计算模  Y = mod(S, 11) 用计算出来的结果除以11，这样就会出现
            String strVerifyCode = ValCodeArr[modValue]; // 获取最后一位的校验码字符值
            Ai = Ai + strVerifyCode; // 17位身份证 加上最后以为验证数字   得到18位有效的身份证号

            if (!idStr.toUpperCase().equals(Ai.toUpperCase())) {// 判断传过来的身份证号 和 计算得到的身份证号是否相同
//                System.out.println("身份证号码不对");
                return false;
            }
//            System.out.println("正确");
            return true;
        } catch (Exception e) {
//            System.out.println("验证出错");
            e.printStackTrace();
            return false;
        }


    }

    /***
     * 地区编码
     */
    private static Map<String, String> cityCodeMap = new HashMap<String, String>() {
        {
            this.put("11", "北京");
            this.put("12", "天津");
            this.put("13", "河北");
            this.put("14", "山西");
            this.put("15", "内蒙古");
            this.put("21", "辽宁");
            this.put("22", "吉林");
            this.put("23", "黑龙江");
            this.put("31", "上海");
            this.put("32", "江苏");
            this.put("33", "浙江");
            this.put("34", "安徽");
            this.put("35", "福建");
            this.put("36", "江西");
            this.put("37", "山东");
            this.put("41", "河南");
            this.put("42", "湖北");
            this.put("43", "湖南");
            this.put("44", "广东");
            this.put("45", "广西");
            this.put("46", "海南");
            this.put("50", "重庆");
            this.put("51", "四川");
            this.put("52", "贵州");
            this.put("53", "云南");
            this.put("54", "西藏");
            this.put("61", "陕西");
            this.put("62", "甘肃");
            this.put("63", "青海");
            this.put("64", "宁夏");
            this.put("65", "新疆");
            this.put("71", "台湾");
            this.put("81", "香港");
            this.put("82", "澳门");
            this.put("91", "国外");
        }
    };

    /***
     * 判断str是否为纯数字组成
     * @param str
     * @return
     */
    private static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
}