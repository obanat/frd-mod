package com.ihunuo.hnmjpeg.opengl2.utils;

import android.opengl.GLES20;

/* loaded from: classes.dex */
public enum EasyGlUtils {
    ;

    /* renamed from: values  reason: to resolve conflict with enum method */
    public static EasyGlUtils[] valuesCustom() {
        EasyGlUtils[] valuesCustom = values();
        int length = valuesCustom.length;
        EasyGlUtils[] easyGlUtilsArr = new EasyGlUtils[length];
        System.arraycopy(valuesCustom, 0, easyGlUtilsArr, 0, length);
        return easyGlUtilsArr;
    }

    public static void useTexParameter() {
        GLES20.glTexParameterf(3553, 10241, 9728.0f);
        GLES20.glTexParameterf(3553, 10240, 9729.0f);
        GLES20.glTexParameterf(3553, 10242, 33071.0f);
        GLES20.glTexParameterf(3553, 10243, 33071.0f);
    }

    public static void useTexParameter(int i, int i2, int i3, int i4) {
        GLES20.glTexParameterf(3553, 10242, i);
        GLES20.glTexParameterf(3553, 10243, i2);
        GLES20.glTexParameterf(3553, 10241, i3);
        GLES20.glTexParameterf(3553, 10240, i4);
    }

    public static void genTexturesWithParameter(int i, int[] iArr, int i2, int i3, int i4, int i5) {
        GLES20.glGenTextures(i, iArr, i2);
        for (int i6 = 0; i6 < i; i6++) {
            GLES20.glBindTexture(3553, iArr[i6]);
            GLES20.glTexImage2D(3553, 0, i3, i4, i5, 0, i3, 5121, null);
            useTexParameter();
        }
        GLES20.glBindTexture(3553, 0);
    }

    public static void bindFrameTexture(int i, int i2) {
        GLES20.glBindFramebuffer(36160, i);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, i2, 0);
    }

    public static void unBindFrameBuffer() {
        GLES20.glBindFramebuffer(36160, 0);
    }
}
