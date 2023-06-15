package com.obana.fydmod;


import android.os.Bundle;
import android.text.TextUtils;
import java.util.Collection;

/* loaded from: classes.dex */
public final class ObjectUtils {
    public static boolean toBoolean(int i) {
        return i == 1;
    }

    public static int toInt(boolean z) {
        return z ? 1 : 0;
    }

    private ObjectUtils() {
    }

    public static boolean isEmpty(Object obj) {
        if (obj instanceof CharSequence) {
            return isEmpty((CharSequence) obj);
        }
        if (obj instanceof Collection) {
            return isEmpty((Collection) obj);
        }
        return obj == null;
    }

    public static boolean isEmpty(Bundle bundle) {
        return bundle == null || bundle.isEmpty();
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isEmpty(T[] tArr) {
        return tArr == null || tArr.length == 0;
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return TextUtils.isEmpty(charSequence);
    }

    public static int toInt(CharSequence charSequence) {
        if (isEmpty(charSequence)) {
            return -1;
        }
        try {
            return Integer.parseInt(charSequence.toString().trim());
        } catch (Exception unused) {
            return -1;
        }
    }

    public static long toLong(CharSequence charSequence) {
        if (isEmpty(charSequence)) {
            return -1L;
        }
        try {
            return Long.parseLong(charSequence.toString().trim());
        } catch (Exception unused) {
            return -1L;
        }
    }

    public static double toDobule(CharSequence charSequence) {
        if (isEmpty(charSequence)) {
            return -1.0d;
        }
        try {
            return Double.parseDouble(charSequence.toString().trim());
        } catch (Exception unused) {
            return -1.0d;
        }
    }

    public static boolean equals(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str.toString().trim());
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static String intToIp(int i) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(String.valueOf(i & 255));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((i >> 8) & 255));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((i >> 16) & 255));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((i >> 24) & 255));
        return stringBuffer.toString();
    }

    public static int bytesToInt2(byte[] bArr) {
        int i = 0;
        for (byte b : bArr) {
            i = (i << 8) | (b & 255);
        }
        return i;
    }

    public static short byte2ToShort(byte[] bArr) {
        return (short) ((bArr[1] & 255) | ((bArr[0] & 255) << 8));
    }

    public static short byte2ToShort2(byte[] bArr) {
        return (short) ((bArr[0] & 255) | ((bArr[1] & 255) << 8));
    }

    public static String bytesToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder("");
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }
    
    public static String BytesToHexString(byte[] bArr, int i) {
        StringBuilder sb = new StringBuilder("");
        if (bArr == null || bArr.length <= 0) {
            return "";
        }
        for (int i2 = 0; i2 < bArr.length && i2 < i; i2++) {
            String hexString = Integer.toHexString(bArr[i2] & 255);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }


}
