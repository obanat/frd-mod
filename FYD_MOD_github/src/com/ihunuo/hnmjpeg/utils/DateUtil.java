package com.ihunuo.hnmjpeg.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* loaded from: classes.dex */
public class DateUtil {
    public static String pattern1 = "yyyy-MM-dd HH:mm:ss";
    public static String pattern2 = "yyyy-MM-dd";
    public static String pattern3 = "yyyy-MM-dd HH:mm:ss";
    public static String pattern4 = "yyyy-MM-dd HH:mm:ss";

    public long getCurTimeLong() {
        return System.currentTimeMillis();
    }

    public static String getCurDate(String str) {
        return new SimpleDateFormat(str).format(new Date());
    }

    public static String getDateToString(long j, String str) {
        return new SimpleDateFormat(str).format(new Date(j));
    }

    public static long getStringToDate(String str, String str2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str2);
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String convertSecToTimeString(long j) {
        long j2 = j / 3600;
        long j3 = j % 3600;
        return String.format("%02d:%02d:%02d", Long.valueOf(j2), Long.valueOf(j3 / 60), Long.valueOf(j3 % 60));
    }
}
