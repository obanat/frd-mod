package com.ihunuo.hnmjpeg.opengl2.utils;

import android.opengl.Matrix;
import java.util.Arrays;
import java.util.Stack;

/* loaded from: classes.dex */
public class VaryTools {
    private float[] mMatrixCamera = new float[16];
    private float[] mMatrixProjection = new float[16];
    private float[] mMatrixCurrent = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private Stack<float[]> mStack = new Stack<float[]>();

    public void pushMatrix() {
        this.mStack.push(Arrays.copyOf(this.mMatrixCurrent, 16));
    }

    public void popMatrix() {
        this.mMatrixCurrent = this.mStack.pop();
    }

    public void clearStack() {
        this.mStack.clear();
    }

    public void translate(float f, float f2, float f3) {
        Matrix.translateM(this.mMatrixCurrent, 0, f, f2, f3);
    }

    public void rotate(float f, float f2, float f3, float f4) {
        Matrix.rotateM(this.mMatrixCurrent, 0, f, f2, f3, f4);
    }

    public void scale(float f, float f2, float f3) {
        Matrix.scaleM(this.mMatrixCurrent, 0, f, f2, f3);
    }

    public void setCamera(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9) {
        Matrix.setLookAtM(this.mMatrixCamera, 0, f, f2, f3, f4, f5, f6, f7, f8, f9);
    }

    public void frustum(float f, float f2, float f3, float f4, float f5, float f6) {
        Matrix.frustumM(this.mMatrixProjection, 0, f, f2, f3, f4, f5, f6);
    }

    public void ortho(float f, float f2, float f3, float f4, float f5, float f6) {
        Matrix.orthoM(this.mMatrixProjection, 0, f, f2, f3, f4, f5, f6);
    }

    public float[] getFinalMatrix() {
        float[] fArr = new float[16];
        Matrix.multiplyMM(fArr, 0, this.mMatrixCamera, 0, this.mMatrixCurrent, 0);
        Matrix.multiplyMM(fArr, 0, this.mMatrixProjection, 0, fArr, 0);
        return fArr;
    }
}
