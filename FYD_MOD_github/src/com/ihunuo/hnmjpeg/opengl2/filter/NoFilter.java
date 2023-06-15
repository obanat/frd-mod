package com.ihunuo.hnmjpeg.opengl2.filter;

import android.content.res.Resources;
import android.opengl.GLES20;

/* loaded from: classes.dex */
public class NoFilter extends AFilter {
    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    protected void onSizeChanged(int i, int i2) {
    }

    public NoFilter(Resources resources) {
        super(resources);
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    protected void onCreate() {
        createProgramByAssetsFile("shader/base_vertex.sh", "shader/base_fragment.sh");
    }

    public void unBindFrame() {
        GLES20.glBindRenderbuffer(36161, 0);
        GLES20.glBindFramebuffer(36160, 0);
    }
}
