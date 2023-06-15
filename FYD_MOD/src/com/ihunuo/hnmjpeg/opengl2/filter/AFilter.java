package com.ihunuo.hnmjpeg.opengl2.filter;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseArray;
import com.ihunuo.hnmjpeg.opengl2.utils.MatrixUtils;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

/* loaded from: classes.dex */
public abstract class AFilter {
    public static final int KEY_IN = 258;
    public static final int KEY_INDEX = 513;
    public static final int KEY_OUT = 257;
    private static final String TAG = "Filter";
    private SparseArray<boolean[]> mBools;
    private SparseArray<float[]> mFloats;
    protected int mHCoord;
    protected int mHMatrix;
    protected int mHPosition;
    protected int mHTexture;
    private SparseArray<int[]> mInts;
    protected int mProgram;
    protected Resources mRes;
    protected FloatBuffer mTexBuffer;
    protected FloatBuffer mVerBuffer;
    protected ShortBuffer mindexBuffer;
    public static boolean DEBUG = true;
    public static final float[] f130OM = MatrixUtils.getOriginalMatrix();
    protected int mFlag = 0;
    private float[] matrix = Arrays.copyOf(f130OM, 16);
    private int textureType = 0;
    private int textureId = 0;
    private float[] pos = {-1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f};
    private float[] coord = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};

    protected abstract void onCreate();

    protected abstract void onSizeChanged(int i, int i2);

    public int getOutputTexture() {
        return -1;
    }

    public AFilter(Resources resources) {
        this.mRes = resources;
        initBuffer();
    }

    public final void create() {
        onCreate();
    }

    public final void setSize(int i, int i2) {
        onSizeChanged(i, i2);
    }

    public void draw() {
        onClear();
        onUseProgram();
        onSetExpandData();
        onBindTexture();
        onDraw();
    }

    public void setMatrix(float[] fArr) {
        this.matrix = fArr;
    }

    public float[] getMatrix() {
        return this.matrix;
    }

    public final void setTextureType(int i) {
        this.textureType = i;
    }

    public final int getTextureType() {
        return this.textureType;
    }

    public final int getTextureId() {
        return this.textureId;
    }

    public final void setTextureId(int i) {
        this.textureId = i;
    }

    public void setFlag(int i) {
        this.mFlag = i;
    }

    public int getFlag() {
        return this.mFlag;
    }

    public void setFloat(int i, float... fArr) {
        if (this.mFloats == null) {
            this.mFloats = new SparseArray<float[]>();
        }
        this.mFloats.put(i, fArr);
    }

    public void setInt(int i, int... iArr) {
        if (this.mInts == null) {
            this.mInts = new SparseArray<int[]>();
        }
        this.mInts.put(i, iArr);
    }

    public void setBool(int i, boolean... zArr) {
        if (this.mBools == null) {
            this.mBools = new SparseArray<boolean[]>();
        }
        this.mBools.put(i, zArr);
    }

    public boolean getBool(int i, int i2) {
        boolean[] zArr;
        SparseArray<boolean[]> sparseArray = this.mBools;
        return sparseArray != null && (zArr = sparseArray.get(i)) != null && zArr.length > i2 && zArr[i2];
    }

    public int getInt(int i, int i2) {
        int[] iArr;
        SparseArray<int[]> sparseArray = this.mInts;
        if (sparseArray == null || (iArr = sparseArray.get(i)) == null || iArr.length <= i2) {
            return 0;
        }
        return iArr[i2];
    }

    public float getFloat(int i, int i2) {
        float[] fArr;
        SparseArray<float[]> sparseArray = this.mFloats;
        if (sparseArray == null || (fArr = sparseArray.get(i)) == null || fArr.length <= i2) {
            return 0.0f;
        }
        return fArr[i2];
    }

    protected final void createProgram(String str, String str2) {
        int uCreateGlProgram = uCreateGlProgram(str, str2);
        this.mProgram = uCreateGlProgram;
        this.mHPosition = GLES20.glGetAttribLocation(uCreateGlProgram, "vPosition");
        this.mHCoord = GLES20.glGetAttribLocation(this.mProgram, "vCoord");
        this.mHMatrix = GLES20.glGetUniformLocation(this.mProgram, "vMatrix");
        this.mHTexture = GLES20.glGetUniformLocation(this.mProgram, "vTexture");
    }

    public final void createProgramByAssetsFile(String str, String str2) {
        createProgram(uRes(this.mRes, str), uRes(this.mRes, str2));
    }

    protected void initBuffer() {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(32);
        allocateDirect.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
        this.mVerBuffer = asFloatBuffer;
        asFloatBuffer.put(this.pos);
        this.mVerBuffer.position(0);
        ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(32);
        allocateDirect2.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer2 = allocateDirect2.asFloatBuffer();
        this.mTexBuffer = asFloatBuffer2;
        asFloatBuffer2.put(this.coord);
        this.mTexBuffer.position(0);
    }

    protected void onUseProgram() {
        GLES20.glUseProgram(this.mProgram);
    }

    protected void onDraw() {
        GLES20.glEnableVertexAttribArray(this.mHPosition);
        GLES20.glVertexAttribPointer(this.mHPosition, 2, 5126, false, 0, (Buffer) this.mVerBuffer);
        GLES20.glEnableVertexAttribArray(this.mHCoord);
        GLES20.glVertexAttribPointer(this.mHCoord, 2, 5126, false, 0, (Buffer) this.mTexBuffer);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glDisableVertexAttribArray(this.mHPosition);
        GLES20.glDisableVertexAttribArray(this.mHCoord);
    }

    protected void onClear() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(16640);
    }

    public void onSetExpandData() {
        GLES20.glUniformMatrix4fv(this.mHMatrix, 1, false, this.matrix, 0);
    }

    public void onBindTexture() {
        GLES20.glActiveTexture(this.textureType + 33984);
        GLES20.glBindTexture(3553, getTextureId());
        GLES20.glUniform1i(this.mHTexture, this.textureType);
    }

    public static void glError(int i, Object obj) {
        if (DEBUG && i != 0) {
            Log.e(TAG, "glError:" + i + "---" + obj);
        }
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

    public static int uCreateGlProgram(String str, String str2) {
        int uLoadShader;
        int uLoadShader2 = uLoadShader(35633, str);
        if (uLoadShader2 == 0 || (uLoadShader = uLoadShader(35632, str2)) == 0) {
            return 0;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        if (glCreateProgram != 0) {
            GLES20.glAttachShader(glCreateProgram, uLoadShader2);
            GLES20.glAttachShader(glCreateProgram, uLoadShader);
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

    public static int uLoadShader(int i, String str) {
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
}
