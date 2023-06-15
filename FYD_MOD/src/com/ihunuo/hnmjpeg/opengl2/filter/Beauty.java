package com.ihunuo.hnmjpeg.opengl2.filter;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

/* loaded from: classes.dex */
public class Beauty extends AFilter {
    private float aaCoef;
    private int gHHeight;
    private int gHWidth;
    private int gHaaCoef;
    private int gHiternum;
    private int gHmixCoef;
    private int iternum;
    private int mHeight;
    private int mWidth;
    private float mixCoef;

    public Beauty(Resources resources) {
        super(resources);
        this.mWidth = 1280;
        this.mHeight = 720;
        setFlag(0);
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    protected void onCreate() {
        createProgramByAssetsFile("shader/beauty/beauty.vert", "shader/beauty/beauty.frag");
        this.gHaaCoef = GLES20.glGetUniformLocation(this.mProgram, "aaCoef");
        this.gHmixCoef = GLES20.glGetUniformLocation(this.mProgram, "mixCoef");
        this.gHiternum = GLES20.glGetUniformLocation(this.mProgram, "iternum");
        this.gHWidth = GLES20.glGetUniformLocation(this.mProgram, "mWidth");
        this.gHHeight = GLES20.glGetUniformLocation(this.mProgram, "mHeight");
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    public void setFlag(int i) {
        super.setFlag(i);
        switch (i) {
            case 1:
                m162a(1, 0.19f, 0.54f);
                return;
            case 2:
                m162a(2, 0.29f, 0.54f);
                return;
            case 3:
                m162a(3, 0.17f, 0.39f);
                return;
            case 4:
                m162a(3, 0.25f, 0.54f);
                return;
            case 5:
                m162a(4, 0.13f, 0.54f);
                return;
            case 6:
                m162a(4, 0.19f, 0.69f);
                return;
            default:
                m162a(0, 0.0f, 0.0f);
                return;
        }
    }

    private void m162a(int i, float f, float f2) {
        this.iternum = i;
        this.aaCoef = f;
        this.mixCoef = f2;
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    protected void onSizeChanged(int i, int i2) {
        this.mWidth = i;
        this.mHeight = i2;
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    public void onSetExpandData() {
        super.onSetExpandData();
        GLES20.glUniform1i(this.gHWidth, this.mWidth);
        GLES20.glUniform1i(this.gHHeight, this.mHeight);
        GLES20.glUniform1f(this.gHaaCoef, this.aaCoef);
        GLES20.glUniform1f(this.gHmixCoef, this.mixCoef);
        GLES20.glUniform1i(this.gHiternum, this.iternum);
        Log.d("ccc", "onSetExpandData: mWidth=" + this.mWidth + " mHeight" + this.mHeight + " aaCoef=" + this.aaCoef + " mixCoef" + this.mixCoef + " iternum=" + this.iternum);
    }
}
