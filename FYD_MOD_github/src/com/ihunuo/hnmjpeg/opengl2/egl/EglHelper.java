package com.ihunuo.hnmjpeg.opengl2.egl;

import android.view.Surface;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/* loaded from: classes.dex */
public class EglHelper {
    private EGL10 mEgl;
    private EGLContext mEglContext;
    private EGLDisplay mEglDisplay;
    private EGLSurface mEglSurface;

    public void initEgl(Surface surface, EGLContext eGLContext) {
        EGL10 egl10 = (EGL10) EGLContext.getEGL();
        this.mEgl = egl10;
        EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        this.mEglDisplay = eglGetDisplay;
        if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }
        if (!this.mEgl.eglInitialize(this.mEglDisplay, new int[2])) {
            throw new RuntimeException("eglInitialize failed");
        }
        int[] iArr = {12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 8, 12326, 8, 12352, 4, 12344};
        int[] iArr2 = new int[1];
        if (!this.mEgl.eglChooseConfig(this.mEglDisplay, iArr, null, 1, iArr2)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        int i = iArr2[0];
        if (i <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        }
        EGLConfig[] eGLConfigArr = new EGLConfig[i];
        if (!this.mEgl.eglChooseConfig(this.mEglDisplay, iArr, eGLConfigArr, i, iArr2)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }
        int[] iArr3 = {12440, 2, 12344};
        if (eGLContext != null) {
            this.mEglContext = this.mEgl.eglCreateContext(this.mEglDisplay, eGLConfigArr[0], eGLContext, iArr3);
        } else {
            this.mEglContext = this.mEgl.eglCreateContext(this.mEglDisplay, eGLConfigArr[0], EGL10.EGL_NO_CONTEXT, iArr3);
        }
        EGLSurface eglCreateWindowSurface = this.mEgl.eglCreateWindowSurface(this.mEglDisplay, eGLConfigArr[0], surface, null);
        this.mEglSurface = eglCreateWindowSurface;
        if (!this.mEgl.eglMakeCurrent(this.mEglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.mEglContext)) {
            throw new RuntimeException("eglMakeCurrent fail");
        }
    }

    public boolean swapBuffers() {
        EGL10 egl10 = this.mEgl;
        if (egl10 != null) {
            return egl10.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
        }
        throw new RuntimeException("egl is null");
    }

    public EGLContext getmEglContext() {
        return this.mEglContext;
    }

    public void destoryEgl() {
        EGL10 egl10 = this.mEgl;
        if (egl10 != null) {
            egl10.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            this.mEgl.eglDestroySurface(this.mEglDisplay, this.mEglSurface);
            this.mEglSurface = null;
            this.mEgl.eglDestroyContext(this.mEglDisplay, this.mEglContext);
            this.mEglContext = null;
            this.mEgl.eglTerminate(this.mEglDisplay);
            this.mEglDisplay = null;
            this.mEgl = null;
        }
    }
}
