package com.example.express.common.util;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class DoubleUtils {

    /**
     * 人民币/美元 返回4位
     * @param cnyValue
     * @return
     */
    public static double cny(double cnyValue) {
        return round(cnyValue , 4);
    }

    /**
     * 对double数据进行取精度. 返回最大的（最接近正无穷大）double 值，该值小于等于参数，并等于某个整数。
     *
     * @param value double数据.
     * @param scale 精度位数(保留的小数位数).
     * @return 精度计算后的数据.
     */
    public static double round(double value, int scale) {
        int n = (int)Math.pow(10, scale);
        return divide(Math.floor(multiply(value, n)), n, scale);
    }

    /**
     * double 相加
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double add(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.add(bd2).doubleValue();
    }

    /**
     * double 相加
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double add(double d1, double... d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        for (double d : d2) {
            bd1 = bd1.add(new BigDecimal(Double.toString(d)));
        }
        return bd1.doubleValue();
    }

    /**
     * double 相加
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double add(BigDecimal d1, BigDecimal... d2) {
        for (BigDecimal d : d2) {
            d1 = d1.add(d);
        }
        return d1.doubleValue();
    }

    /**
     * double 相减
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double subtract(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.subtract(bd2).doubleValue();
    }

    /**
     * double 乘法
     *
     * @param bd1
     * @param bd2
     * @return
     */
    public static double multiply(BigDecimal bd1, BigDecimal bd2) {
        return bd1.multiply(bd2).doubleValue();
    }

    /**
     * double 多数乘法
     *
     * @param bd1
     * @param bd2
     * @return
     */
    public static double multiply(BigDecimal bd1, BigDecimal... bd2) {
        for (BigDecimal d : bd2) {
            bd1 = bd1.multiply(d);
        }
        return bd1.doubleValue();
    }

    /**
     * double 乘法
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double multiply(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.multiply(bd2).doubleValue();
    }

    /**
     * double 多数乘法
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double multiply(double d1, double... d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        for (double d : d2) {
            bd1 = bd1.multiply(new BigDecimal(Double.toString(d)));
        }
        return bd1.doubleValue();
    }
    /**
     * double 除法
     *
     * @param d1
     * @param d2
     * @param scale 四舍五入 小数点位数
     * @return
     */
    public static double divide(double d1, double d2, int scale) {
        // 当然在此之前，你要判断分母是否为0，
        // 为0你可以根据实际需求做相应的处理

        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * double 除法 向正无穷取值
     *
     * @param d1
     * @param d2
     * @param scale 四舍五入 小数点位数
     * @return
     */
    public static double divideUp(double d1, double d2, int scale) {
        // 当然在此之前，你要判断分母是否为0，
        // 为0你可以根据实际需求做相应的处理

        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.divide(bd2, scale, BigDecimal.ROUND_CEILING).doubleValue();
    }

    /**
     * double 除法 向负无穷取值
     *
     * @param d1
     * @param d2
     * @param scale 四舍五入 小数点位数
     * @return
     */
    public static double divideDown(double d1, double d2, int scale) {
        // 当然在此之前，你要判断分母是否为0，
        // 为0你可以根据实际需求做相应的处理

        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.divide(bd2, scale, BigDecimal.ROUND_FLOOR).doubleValue();
    }

    /**
     * 小数向上进位
     *
     * @param value
     * @param scale
     * @return
     */
    public static double roundUp(double value, int scale) {
        int n = (int)Math.pow(10, scale);
        double result = (double)divide(Math.ceil(multiply(value, n)), n, scale);
        return result;
    }

    /**
     * 整除
     *
     * @param a
     * @param b
     * @param base
     * @return
     */
    public static boolean isDivide(double a, double b, int base) {
        int num = (int)Math.pow(10, base);
        double aa = a * num;
        double bb = b * num;
        int f = (int)DoubleUtils.divide(aa, bb, 1);
        if (DoubleUtils.multiply(f, bb) == aa) {
            return true;
        }
        return false;
    }

    public static int compare(double a, double b) {
        return BigDecimal.valueOf(a).compareTo(BigDecimal.valueOf(b));
    }


    public static final String DOT1 = ".";
    public static final String DOT2 = "\\.";
    public static final String E = "E";
    public static final String e = "e";
    public static final int DEFAULT_SCALE = 2;
    public static final String DEFAULT_DOUBLE = "0.00";
    // 8900000000.000000000
    public static final String DOUBLE_END1 = "0+?$";
    // 8900000000.
    public static final String DOUBLE_END2 = "[.]$";

    /**
     * 科学计数法: double 转 字符串:  -4.62E-6  ->  -0.00000462  / 8.9E9    -> 8900000000
     */
    public static String toString(Double arg) {
        return BigDecimal.valueOf(arg).stripTrailingZeros().toPlainString();
    }

    private static NumberFormat getNumberFormat(int scale) {
        NumberFormat format = NumberFormat.getInstance();
        // No comma
        format.setGroupingUsed(false);
        // Set the number of decimal places
        format.setMinimumFractionDigits(scale);
        return format;
    }

    /**
     * 小数位数，向上进位  -- 多仓：进位    空仓：截位
     * upCarry(5.345, 2) -> 5.35
     * upCarry(5.341, 2) -> 5.35
     */
    public static double upCarry(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, BigDecimal.ROUND_UP).doubleValue();
    }

    /**
     * 小数位数，向上进位  -- 多仓：进位    空仓：截位
     * downCut(5.341, 2) -> 5.34
     * downCut(5.349, 2) -> 5.34
     */
    public static double downCut(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, BigDecimal.ROUND_DOWN).doubleValue();
    }
}
