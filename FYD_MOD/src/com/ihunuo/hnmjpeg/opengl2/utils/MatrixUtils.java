package com.ihunuo.hnmjpeg.opengl2.utils;

import android.opengl.Matrix;

/* loaded from: classes.dex */
public enum MatrixUtils {
    ;
    
    public static final int TYPE_CENTERCROP = 1;
    public static final int TYPE_CENTERINSIDE = 2;
    public static final int TYPE_FITEND = 4;
    public static final int TYPE_FITSTART = 3;
    public static final int TYPE_FITXY = 0;

    /* renamed from: values  reason: to resolve conflict with enum method */
    public static MatrixUtils[] valuesCustom() {
        MatrixUtils[] valuesCustom = values();
        int length = valuesCustom.length;
        MatrixUtils[] matrixUtilsArr = new MatrixUtils[length];
        System.arraycopy(valuesCustom, 0, matrixUtilsArr, 0, length);
        return matrixUtilsArr;
    }

    @Deprecated
    public static void getShowMatrix(float[] fArr, int i, int i2, int i3, int i4) {
        if (i2 > 0 && i > 0 && i3 > 0 && i4 > 0) {
            float f = i3 / i4;
            float f2 = i / i2;
            float[] fArr2 = new float[16];
            float[] fArr3 = new float[16];
            if (f2 > f) {
                Matrix.orthoM(fArr2, 0, (-f) / f2, f / f2, -1.0f, 1.0f, 1.0f, 3.0f);
            } else {
                Matrix.orthoM(fArr2, 0, -1.0f, 1.0f, (-f2) / f, f2 / f, 1.0f, 3.0f);
            }
            Matrix.setLookAtM(fArr3, 0, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(fArr, 0, fArr2, 0, fArr3, 0);
        }
    }

    public static void getMatrix(float[] fArr, int i, int i2, int i3, int i4, int i5) {
        if (i3 > 0 && i2 > 0 && i4 > 0 && i5 > 0) {
            float[] fArr2 = new float[16];
            float[] fArr3 = new float[16];
            if (i == 0) {
                Matrix.orthoM(fArr2, 0, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 3.0f);
                Matrix.setLookAtM(fArr3, 0, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
                Matrix.multiplyMM(fArr, 0, fArr2, 0, fArr3, 0);
            }
            float f = i4 / i5;
            float f2 = i2 / i3;
            if (f2 > f) {
                if (i == 1) {
                    Matrix.orthoM(fArr2, 0, (-f) / f2, f / f2, -1.0f, 1.0f, 1.0f, 3.0f);
                } else if (i == 2) {
                    Matrix.orthoM(fArr2, 0, -1.0f, 1.0f, (-f2) / f, f2 / f, 1.0f, 3.0f);
                } else if (i == 3) {
                    Matrix.orthoM(fArr2, 0, -1.0f, 1.0f, 1.0f - ((2.0f * f2) / f), 1.0f, 1.0f, 3.0f);
                } else if (i == 4) {
                    Matrix.orthoM(fArr2, 0, -1.0f, 1.0f, -1.0f, ((2.0f * f2) / f) - 1.0f, 1.0f, 3.0f);
                }
            } else if (i == 1) {
                Matrix.orthoM(fArr2, 0, -1.0f, 1.0f, (-f2) / f, f2 / f, 1.0f, 3.0f);
            } else if (i == 2) {
                Matrix.orthoM(fArr2, 0, (-f) / f2, f / f2, -1.0f, 1.0f, 1.0f, 3.0f);
            } else if (i == 3) {
                Matrix.orthoM(fArr2, 0, -1.0f, ((2.0f * f) / f2) - 1.0f, -1.0f, 1.0f, 1.0f, 3.0f);
            } else if (i == 4) {
                Matrix.orthoM(fArr2, 0, 1.0f - ((2.0f * f) / f2), 1.0f, -1.0f, 1.0f, 1.0f, 3.0f);
            }
            Matrix.setLookAtM(fArr3, 0, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(fArr, 0, fArr2, 0, fArr3, 0);
        }
    }

    public static void getCenterInsideMatrix(float[] fArr, int i, int i2, int i3, int i4) {
        if (i2 > 0 && i > 0 && i3 > 0 && i4 > 0) {
            float f = i3 / i4;
            float f2 = i / i2;
            float[] fArr2 = new float[16];
            float[] fArr3 = new float[16];
            if (f2 > f) {
                Matrix.orthoM(fArr2, 0, -1.0f, 1.0f, (-f2) / f, f2 / f, 1.0f, 3.0f);
            } else {
                Matrix.orthoM(fArr2, 0, (-f) / f2, f / f2, -1.0f, 1.0f, 1.0f, 3.0f);
            }
            Matrix.setLookAtM(fArr3, 0, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMM(fArr, 0, fArr2, 0, fArr3, 0);
        }
    }

    public static float[] rotate(float[] fArr, float f) {
        Matrix.rotateM(fArr, 0, f, 0.0f, 0.0f, 1.0f);
        return fArr;
    }

    public static float[] flip(float[] fArr, boolean z, boolean z2) {
        if (z || z2) {
            Matrix.scaleM(fArr, 0, z ? -1.0f : 1.0f, z2 ? -1.0f : 1.0f, 1.0f);
        }
        return fArr;
    }

    public static float[] scale(float[] fArr, float f, float f2) {
        Matrix.scaleM(fArr, 0, f, f2, 1.0f);
        return fArr;
    }

    public static float[] getOriginalMatrix() {
        return new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }
}
