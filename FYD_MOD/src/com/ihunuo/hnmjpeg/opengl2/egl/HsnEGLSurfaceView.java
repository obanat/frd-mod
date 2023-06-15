package com.ihunuo.hnmjpeg.opengl2.egl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.lang.ref.WeakReference;
import javax.microedition.khronos.egl.EGLContext;

/* loaded from: classes.dex */
public abstract class HsnEGLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    private EGLContext eglContext;
    private HsnEGLThread hsnEGLThread;
    private HsnGLRender hsnGLRender;
    public int mRenderMode;
    private Surface surface;

    /* loaded from: classes.dex */
    public interface HsnGLRender {
        void onDrawFrame();

        void onSurfaceChanged(int i, int i2);

        void onSurfaceCreated();
    }

    public HsnEGLSurfaceView(Context context) {
        this(context, null);
    }

    public HsnEGLSurfaceView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HsnEGLSurfaceView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mRenderMode = 1;
        getHolder().addCallback(this);
    }

    public void setRender(HsnGLRender hsnGLRender) {
        this.hsnGLRender = hsnGLRender;
    }

    public void setRenderMode(int i) {
        if (this.hsnGLRender == null) {
            throw new RuntimeException("must set render before");
        }
        this.mRenderMode = i;
    }

    public void setSurfaceAndEglContext(Surface surface, EGLContext eGLContext) {
        this.surface = surface;
        this.eglContext = eGLContext;
    }

    public EGLContext getEglContext() {
        HsnEGLThread hsnEGLThread = this.hsnEGLThread;
        if (hsnEGLThread != null) {
            return hsnEGLThread.getEglContext();
        }
        return null;
    }

    public void requestRender() {
        HsnEGLThread hsnEGLThread = this.hsnEGLThread;
        if (hsnEGLThread != null) {
            hsnEGLThread.requestRender();
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (this.surface == null) {
            this.surface = surfaceHolder.getSurface();
        }
        HsnEGLThread hsnEGLThread = new HsnEGLThread(new WeakReference(this));
        this.hsnEGLThread = hsnEGLThread;
        hsnEGLThread.isCreate = true;
        this.hsnEGLThread.start();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        this.hsnEGLThread.width = i2;
        this.hsnEGLThread.height = i3;
        this.hsnEGLThread.isChange = true;
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.hsnEGLThread.onDestory();
        this.hsnEGLThread = null;
        this.surface = null;
        this.eglContext = null;
    }

    /* loaded from: classes.dex */
    public static class HsnEGLThread extends Thread {
        private int height;
        private WeakReference<HsnEGLSurfaceView> hsnEGLSurfaceViewWeakReference;
        private int width;
        private EglHelper eglHelper = null;
        private Object object = null;
        private boolean isExit = false;
        private boolean isCreate = false;
        private boolean isChange = false;
        private boolean isStart = false;

        public HsnEGLThread(WeakReference<HsnEGLSurfaceView> weakReference) {
            this.hsnEGLSurfaceViewWeakReference = weakReference;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            this.isExit = false;
            this.isStart = false;
            this.object = new Object();
            EglHelper eglHelper = new EglHelper();
            this.eglHelper = eglHelper;
            eglHelper.initEgl(this.hsnEGLSurfaceViewWeakReference.get().surface, this.hsnEGLSurfaceViewWeakReference.get().eglContext);
            while (!this.isExit) {
                if (this.isStart) {
                    if (this.hsnEGLSurfaceViewWeakReference.get().mRenderMode == 0) {
                        synchronized (this.object) {
                            try {
                                this.object.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (this.hsnEGLSurfaceViewWeakReference.get().mRenderMode == 1) {
                        try {
                            Thread.sleep(40L);
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        throw new RuntimeException("mRenderMode is wrong value");
                    }
                }
                onCreate();
                onChange(this.width, this.height);
                onDraw();
                this.isStart = true;
            }
            release();
        }

        private void onCreate() {
            if (this.isCreate && this.hsnEGLSurfaceViewWeakReference.get().hsnGLRender != null) {
                this.isCreate = false;
                this.hsnEGLSurfaceViewWeakReference.get().hsnGLRender.onSurfaceCreated();
            }
        }

        private void onChange(int i, int i2) {
            if (this.isChange && this.hsnEGLSurfaceViewWeakReference.get().hsnGLRender != null) {
                this.isChange = false;
                this.hsnEGLSurfaceViewWeakReference.get().hsnGLRender.onSurfaceChanged(i, i2);
            }
        }

        private void onDraw() {
            if (this.hsnEGLSurfaceViewWeakReference.get().hsnGLRender != null && this.eglHelper != null) {
                this.hsnEGLSurfaceViewWeakReference.get().hsnGLRender.onDrawFrame();
                if (!this.isStart) {
                    this.hsnEGLSurfaceViewWeakReference.get().hsnGLRender.onDrawFrame();
                }
                this.eglHelper.swapBuffers();
            }
        }

        public void requestRender() {
            Object obj = this.object;
            if (obj != null) {
                synchronized (obj) {
                    this.object.notifyAll();
                }
            }
        }

        public void onDestory() {
            this.isExit = true;
            requestRender();
        }

        public void release() {
            EglHelper eglHelper = this.eglHelper;
            if (eglHelper != null) {
                eglHelper.destoryEgl();
                this.eglHelper = null;
                this.object = null;
                this.hsnEGLSurfaceViewWeakReference = null;
            }
        }

        public EGLContext getEglContext() {
            EglHelper eglHelper = this.eglHelper;
            if (eglHelper != null) {
                return eglHelper.getmEglContext();
            }
            return null;
        }
    }
}
