package com.ihunuo.hnmjpeg.opengl2.filter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.ihunuo.hnmjpeg.opengl2.utils.EasyGlUtils;
import java.io.IOException;

/* loaded from: classes.dex */
public class LookupFilter extends AFilter {
    private float intensity;
    private Bitmap mBitmap;
    private int mHIntensity;
    private int mHMaskImage;
    private int[] mastTextures;

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    protected void onSizeChanged(int i, int i2) {
    }

    public LookupFilter(Resources resources) {
        super(resources);
        this.mastTextures = new int[1];
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    protected void onCreate() {
        createProgramByAssetsFile("lookup/lookup.vert", "lookup/lookup.frag");
        this.mHMaskImage = GLES20.glGetUniformLocation(this.mProgram, "maskTexture");
        this.mHIntensity = GLES20.glGetUniformLocation(this.mProgram, "intensity");
        EasyGlUtils.genTexturesWithParameter(1, this.mastTextures, 0, 6408, 512, 512);
    }

    public void setIntensity(float f) {
        this.intensity = f;
    }

    public void setMaskImage(String str) {
        try {
            this.mBitmap = BitmapFactory.decodeStream(this.mRes.getAssets().open(str));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMaskImage(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    public void onBindTexture() {
        super.onBindTexture();
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    public void onSetExpandData() {
        super.onSetExpandData();
        GLES20.glUniform1f(this.mHIntensity, this.intensity);
        if (this.mastTextures[0] != 0) {
            GLES20.glActiveTexture(getTextureType() + 33984 + 1);
            GLES20.glBindTexture(3553, this.mastTextures[0]);
            Bitmap bitmap = this.mBitmap;
            if (bitmap != null && !bitmap.isRecycled()) {
                GLUtils.texImage2D(3553, 0, this.mBitmap, 0);
                this.mBitmap.recycle();
            }
            GLES20.glUniform1i(this.mHMaskImage, getTextureType() + 1);
        }
    }
}
