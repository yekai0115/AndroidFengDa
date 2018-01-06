package com.fengda.app.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 精确计算工具类，提供加减乘除的计算
 */
public class CompuUtils {
    /** 常量--0 */
    public static final int ZERO = 0;
    /** 常量--1 */
    public static final int ONE = 1;
    /** 常量-- -1 */
    public static final int MINUS_ONE = -1;
 
 
    /**
     * 大小比较
     *
     * @param a
     * @param b
     * @return 返回1， 表示a大于b <br/>
     *         返回0 ，表示a等于b <br/>
     *         返回-1，表示a小于b
     */
    public static int compareTo(String a, String b) {
        if(StringUtils.isBlank(a)){
                a="0";
        }
        if(StringUtils.isBlank(b)){
            b="0";
        }
        return new BigDecimal(a).compareTo(new BigDecimal(b));
    }
 
    /**
     * 大小比较
     *
     * @param a
     * @param b
     * @return 返回1， 表示a大于b <br/>
     *         返回0 ，表示a等于b <br/>
     *         返回-1，表示a小于b
     */
    public static int compareTo(long a, long b) {
        return new BigDecimal(a).compareTo(new BigDecimal(b));
    }
    
    
    
    
    /**
     * 加法运算
     *
     * @param a 被加数
     * @param b 加数
     */
    public static BigDecimal add(String a, String b) {
        if(StringUtils.isBlank(a)){
            a="0";
        }
        if(StringUtils.isBlank(b)){
            b="0";
        }
        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
        return new BigDecimal(a).add(new BigDecimal(b), mc);
    }
 
    /**
     * 累加运算
     *
     * @param vals
     * @return
     */
    public static int add(int... vals) {
        if (vals == null || vals.length <= 0) {
            return 0;
        }
 
        int sum = 0;
        for (int val : vals) {
            sum = sum + val;
        }
 
        return sum;
    }
 
    /**
     * 累加运算
     *
     * @param vals
     * @return
     */
    public static long add(long... vals) {
        if (vals == null || vals.length <= 0) {
            return 0L;
        }
 
        long sum = 0L;
        for (long val : vals) {
            sum = sum + val;
        }
 
        return sum;
    }
 
    /**
     * 减法运算
     *
     * @param a 被减数
     * @param b 减数
     */
    public static BigDecimal subtract(String a, String b) {
        if(StringUtils.isBlank(a)){
            a="0";
        }
        if(StringUtils.isBlank(b)){
            b="0";
        }
        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
        return new BigDecimal(a).subtract(new BigDecimal(b), mc);
    }
 
    /**
     * 乘法运算
     *
     * @param a 被乘数
     * @param b 乘数
     */
    public static BigDecimal multiply(String a, String b) {
        if(StringUtils.isBlank(a)){
            a="0";
        }
        if(StringUtils.isBlank(b)){
            b="0";
        }
        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
        return new BigDecimal(a).multiply(new BigDecimal(b), mc);
    }
 
    /**
     * 除法运算
     *保留两位
     * @param a 被除数
     * @param b 除数
     */
    public static BigDecimal divide(String a, String b) {
        if(StringUtils.isBlank(a)){
            a="0";
        }
        if(StringUtils.isBlank(b)){
            b="0";
        }
        return new BigDecimal(a).divide(new BigDecimal(b), 2, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal divideInt(long a, int b) {
        return new BigDecimal(a).divide(new BigDecimal(b), 2, BigDecimal.ROUND_HALF_UP);
    }

    public static String  percentage(String str) {
        if (str.contains("%")) {
            str = str.replaceAll("%", "");
        }
        return divide(str,"100").toString();
    }
}
