package com.obana.fydmod;

import android.util.Log;

/* loaded from: classes.dex */
public class LogUtils {
    private static final String TAG = "FYD-MOD";
    private static final boolean showLog = true;

    /* renamed from: e */
    public static void e(Object obj) {
        dispose(6, obj);
    }

    /* renamed from: i */
    public static void i(Object obj) {
        dispose(4, obj);
    }

    /* renamed from: i */
    public static void i(Object... objArr) {
        dispose(4, objArr);
    }

    /* renamed from: w */
    public static void w(Object obj) {
        dispose(5, obj);
    }

    /* renamed from: d */
    public static void d(Object obj) {
        dispose(3, obj);
    }

    /* renamed from: d */
    public static void d(Object... objArr) {
        dispose(3, objArr);
    }

    private static void dispose(int i, Object obj) {
        printLog(i, obj != null ? obj.toString() : "[null]");
    }

    public static void dispose(int i, Object... objArr) {
        StringBuilder sb = null;
        for (Object obj : objArr) {
            if (sb == null) {
                sb = new StringBuilder("[" + obj + "]");
            } else {
                sb.append(" [");
                sb.append(obj);
                sb.append("]");
            }
        }
        printLog(i, sb.toString());
    }

    private static void printLog(int i, String str) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[5];
        String str2 = "\n" + str;
        if (i == 3) {
            Log.d(TAG, stackTraceElement.toString() + str2);
            return;
        }
        switch (i) {
            case 5:
                Log.w(TAG, str);
                return;
            case 6:
                Log.e(TAG, str);
                return;
            default:
                Log.i(TAG, str);
                return;
        }
    }
}