package com.ihunuo.hnmjpeg.opengl2.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;
import java.io.InputStream;

/* loaded from: classes.dex */
public class ShaderUtils {
    private static final String TAG = "ShaderUtils";

    private ShaderUtils() {
    }

    public static void checkGLError(String str) {
        Log.e("wuwang", str);
    }

    public static int loadShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        if (glCreateShader != 0) {
            GLES20.glShaderSource(glCreateShader, str);
            GLES20.glCompileShader(glCreateShader);
            int[] iArr = new int[1];
            GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
            if (iArr[0] == 0) {
                Log.e(TAG, "Could not compile shader:" + i);
                Log.e(TAG, "GLES20 Error:" + GLES20.glGetShaderInfoLog(glCreateShader));
                GLES20.glDeleteShader(glCreateShader);
                return 0;
            }
            return glCreateShader;
        }
        return glCreateShader;
    }

    public static int loadShader(Resources resources, int i, String str) {
        return loadShader(i, loadFromAssetsFile(str, resources));
    }

    public static int createProgram(String str, String str2) {
        int loadShader;
        int loadShader2 = loadShader(35633, str);
        if (loadShader2 == 0 || (loadShader = loadShader(35632, str2)) == 0) {
            return 0;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        if (glCreateProgram != 0) {
            GLES20.glAttachShader(glCreateProgram, loadShader2);
            checkGLError("Attach Vertex Shader");
            GLES20.glAttachShader(glCreateProgram, loadShader);
            checkGLError("Attach Fragment Shader");
            GLES20.glLinkProgram(glCreateProgram);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
            if (iArr[0] != 1) {
                Log.e(TAG, "Could not link program:" + GLES20.glGetProgramInfoLog(glCreateProgram));
                GLES20.glDeleteProgram(glCreateProgram);
                return 0;
            }
            return glCreateProgram;
        }
        return glCreateProgram;
    }

    public static int createProgram(Resources resources, String str, String str2) {
        return createProgram(loadFromAssetsFile(str, resources), loadFromAssetsFile(str2, resources));
    }

    public static String loadFromAssetsFile(String str, Resources resources) {
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
}
