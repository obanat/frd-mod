package com.ihunuo.hnmjpeg.opengl2.imgvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import com.ihunuo.hnmjpeg.opengl2.egl.HsnEGLSurfaceView;
import com.ihunuo.hnmjpeg.opengl2.imgvideo.HsnImgVideoRender;

/* loaded from: classes.dex */
public class HsnImgVideoView extends HsnEGLSurfaceView {
    private int fbotextureid;
    public HsnImgVideoRender hsnImgVideoRender;

    public HsnImgVideoView(Context context) {
        this(context, null);
    }

    public HsnImgVideoView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HsnImgVideoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        HsnImgVideoRender hsnImgVideoRender = new HsnImgVideoRender(context);
        this.hsnImgVideoRender = hsnImgVideoRender;
        setRender(hsnImgVideoRender);
        setRenderMode(0);
        this.hsnImgVideoRender.setOnRenderCreateListener(new HsnImgVideoRender.OnRenderCreateListener() { // from class: com.ihunuo.hnmjpeg.opengl2.imgvideo.HsnImgVideoView.1
            @Override // com.ihunuo.hnmjpeg.opengl2.imgvideo.HsnImgVideoRender.OnRenderCreateListener
            public void onCreate(int i2) {
                HsnImgVideoView.this.fbotextureid = i2;
            }
        });
    }

    public void setCurrentImg(int i) {
        HsnImgVideoRender hsnImgVideoRender = this.hsnImgVideoRender;
        if (hsnImgVideoRender != null) {
            hsnImgVideoRender.setCurrentImgSrc(i);
            requestRender();
        }
    }

    public void setCurrentBitmap(Bitmap bitmap) {
        HsnImgVideoRender hsnImgVideoRender = this.hsnImgVideoRender;
        if (hsnImgVideoRender != null) {
            hsnImgVideoRender.setCurrentBitmap(bitmap);
            requestRender();
            setRenderMode(1);
        }
    }

    public void setRGB(byte[] bArr, int i, int i2) {
        HsnImgVideoRender hsnImgVideoRender = this.hsnImgVideoRender;
        if (hsnImgVideoRender != null) {
            hsnImgVideoRender.setCurrentRGB(bArr, i, i2);
            requestRender();
            setRenderMode(1);
        }
    }

    public int getFbotextureid() {
        return this.fbotextureid;
    }

    public void setRotate(int i, int i2, int i3, int i4) {
        this.hsnImgVideoRender.setRotate(i, i2, i3, i4);
    }

    public void setIsVR(boolean z) {
        this.hsnImgVideoRender.setIsVR(z);
    }

    public boolean getIsVR() {
        return this.hsnImgVideoRender.isVR;
    }

    public HsnImgVideoRender getHsnRender() {
        return this.hsnImgVideoRender;
    }
}
