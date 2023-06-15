package com.ihunuo.hnmjpeg.opengl2.utils;

import android.app.Activity;

/* loaded from: classes.dex */
public class PermissionUtils {
    public static void askPermission(Activity activity, String[] strArr, int i, Runnable runnable) {
        runnable.run();
    }

    public static void onRequestPermissionsResult(boolean z, int[] iArr, Runnable runnable, Runnable runnable2) {
        if (z) {
            if (iArr.length > 0 && iArr[0] == 0) {
                runnable.run();
            } else {
                runnable2.run();
            }
        }
    }
}
