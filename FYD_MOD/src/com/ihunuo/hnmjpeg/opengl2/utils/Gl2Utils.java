package com.ihunuo.hnmjpeg.opengl2.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import java.io.InputStream;

/* loaded from: classes.dex */
public class Gl2Utils {
    public static boolean DEBUG = true;
    public static final String TAG = "GLUtils";
    public static final int TYPE_CENTERCROP = 1;
    public static final int TYPE_CENTERINSIDE = 2;
    public static final int TYPE_FITEND = 4;
    public static final int TYPE_FITSTART = 3;
    public static final int TYPE_FITXY = 0;

    private Gl2Utils() {
    }

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

    public static float[] getOriginalMatrix() {
        return new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }

    public static String uRes(Resources resources, String str) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream open = resources.getAssets().open(str);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = open.read(bArr);
                if (-1 != read) {
                    sb.append(new String(bArr, 0, read));
                } else {
                    return sb.toString().replaceAll("\\r\\n", "\n");
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static int createGlProgramByRes(Resources resources, String str, String str2) {
        return createGlProgram(uRes(resources, str), uRes(resources, str2));
    }

    public static int createGlProgram(String str, String str2) {
        int loadShader;
        int loadShader2 = loadShader(35633, str);
        if (loadShader2 == 0 || (loadShader = loadShader(35632, str2)) == 0) {
            return 0;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        if (glCreateProgram != 0) {
            GLES20.glAttachShader(glCreateProgram, loadShader2);
            GLES20.glAttachShader(glCreateProgram, loadShader);
            GLES20.glLinkProgram(glCreateProgram);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
            if (iArr[0] != 1) {
                glError(1, "Could not link program:" + GLES20.glGetProgramInfoLog(glCreateProgram));
                GLES20.glDeleteProgram(glCreateProgram);
                return 0;
            }
            return glCreateProgram;
        }
        return glCreateProgram;
    }

    public static int loadShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        if (glCreateShader != 0) {
            GLES20.glShaderSource(glCreateShader, str);
            GLES20.glCompileShader(glCreateShader);
            int[] iArr = new int[1];
            GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
            if (iArr[0] == 0) {
                glError(1, "Could not compile shader:" + i);
                glError(1, "GLES20 Error:" + GLES20.glGetShaderInfoLog(glCreateShader));
                GLES20.glDeleteShader(glCreateShader);
                return 0;
            }
            return glCreateShader;
        }
        return glCreateShader;
    }

    public static void glError(int i, Object obj) {
        if (DEBUG && i != 0) {
            Log.e(TAG, "glError:" + i + "---" + obj);
        }
    }
}
