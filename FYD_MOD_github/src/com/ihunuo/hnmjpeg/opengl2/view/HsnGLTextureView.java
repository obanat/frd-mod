package com.ihunuo.hnmjpeg.opengl2.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import com.ihunuo.hnmjpeg.opengl2.egl.HsnEGLSurfaceView;

/* loaded from: classes.dex */
public class HsnGLTextureView extends HsnEGLSurfaceView {
    private final HsnTextureRender hsnTextureRender;

    public HsnGLTextureView(Context context) {
        this(context, null);
    }

    public HsnGLTextureView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HsnGLTextureView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        HsnTextureRender hsnTextureRender = new HsnTextureRender(context);
        this.hsnTextureRender = hsnTextureRender;
        setRender(hsnTextureRender);
        setRenderMode(0);
    }

    public void setBitmap(Bitmap bitmap) {
        this.hsnTextureRender.bitmap = bitmap;
        if (this.mRenderMode == 0) {
            requestRender();
        }
    }

    public void setResourceBg(int i) {
        this.hsnTextureRender.bitmap = BitmapFactory.decodeResource(getResources(), i);
        if (this.mRenderMode == 0) {
            requestRender();
        }
    }

    public int getTextureid() {
        return this.hsnTextureRender.textureid;
    }
}
