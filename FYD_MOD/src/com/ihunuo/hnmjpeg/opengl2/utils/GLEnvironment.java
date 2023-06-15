package com.ihunuo.hnmjpeg.opengl2.utils;

import android.content.Context;
import android.opengl.GLDebugHelper;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

/* loaded from: classes.dex */
public class GLEnvironment implements SurfaceHolder.Callback2 {
    public static final int DEBUG_CHECK_GL_ERROR = 1;
    public static final int DEBUG_LOG_GL_CALLS = 2;
    private static final boolean LOG_ATTACH_DETACH = false;
    private static final boolean LOG_EGL = false;
    private static final boolean LOG_PAUSE_RESUME = false;
    private static final boolean LOG_RENDERER = false;
    private static final boolean LOG_RENDERER_DRAW_FRAME = false;
    private static final boolean LOG_SURFACE = false;
    private static final boolean LOG_THREADS = false;
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    private static final String TAG = "GLSurfaceView";
    private static final GLThreadManager sGLThreadManager = new GLThreadManager(null);
    private int mDebugFlags;
    private boolean mDetached;
    private EGLConfigChooser mEGLConfigChooser;
    private int mEGLContextClientVersion;
    private EGLContextFactory mEGLContextFactory;
    private EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private GLThread mGLThread;
    private boolean mPreserveEGLContextOnPause;
    private GLSurfaceView.Renderer mRenderer;
    private final WeakReference<GLEnvironment> mThisWeakRef = new WeakReference<GLEnvironment>(this);
    public Object nativeWindow;

    /* loaded from: classes.dex */
    public interface EGLConfigChooser {
        EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay);
    }

    /* loaded from: classes.dex */
    public interface EGLContextFactory {
        EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig);

        void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext);
    }

    /* loaded from: classes.dex */
    public interface EGLWindowSurfaceFactory {
        EGLSurface createSurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj);

        void destroySurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface);
    }

    private void init() {
    }

    public GLEnvironment(Context context) {
        init();
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mGLThread != null) {
                this.mGLThread.requestExitAndWait();
            }
        } finally {
            super.finalize();
        }
    }

    public int getDebugFlags() {
        return this.mDebugFlags;
    }

    public void setPreserveEGLContextOnPause(boolean z) {
        this.mPreserveEGLContextOnPause = z;
    }

    public boolean getPreserveEGLContextOnPause() {
        return this.mPreserveEGLContextOnPause;
    }

    public void setRenderer(GLSurfaceView.Renderer renderer) {
        checkRenderThreadState();
        if (this.mEGLConfigChooser == null) {
            this.mEGLConfigChooser = new SimpleEGLConfigChooser(true);
        }
        if (this.mEGLContextFactory == null) {
            this.mEGLContextFactory = new DefaultContextFactory(this, null);
        }
        if (this.mEGLWindowSurfaceFactory == null) {
            this.mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory(null);
        }
        this.mRenderer = renderer;
        GLThread gLThread = new GLThread(this.mThisWeakRef);
        this.mGLThread = gLThread;
        gLThread.start();
    }

    public void setEGLContextFactory(EGLContextFactory eGLContextFactory) {
        checkRenderThreadState();
        this.mEGLContextFactory = eGLContextFactory;
    }

    public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory eGLWindowSurfaceFactory) {
        checkRenderThreadState();
        this.mEGLWindowSurfaceFactory = eGLWindowSurfaceFactory;
    }

    public void setEGLConfigChooser(EGLConfigChooser eGLConfigChooser) {
        checkRenderThreadState();
        this.mEGLConfigChooser = eGLConfigChooser;
    }

    public void setEGLConfigChooser(boolean z) {
        setEGLConfigChooser(new SimpleEGLConfigChooser(z));
    }

    public void setEGLConfigChooser(int i, int i2, int i3, int i4, int i5, int i6) {
        setEGLConfigChooser(new ComponentSizeChooser(i, i2, i3, i4, i5, i6));
    }

    public void setEGLContextClientVersion(int i) {
        checkRenderThreadState();
        this.mEGLContextClientVersion = i;
    }

    public void setRenderMode(int i) {
        this.mGLThread.setRenderMode(i);
    }

    public int getRenderMode() {
        return this.mGLThread.getRenderMode();
    }

    public void requestRender() {
        this.mGLThread.requestRender();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mGLThread.surfaceCreated();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mGLThread.surfaceDestroyed();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        this.mGLThread.onWindowResize(i2, i3);
    }

    @Override // android.view.SurfaceHolder.Callback2
    public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
        GLThread gLThread = this.mGLThread;
        if (gLThread != null) {
            gLThread.requestRenderAndWait();
        }
    }

    public void onPause() {
        this.mGLThread.onPause();
    }

    public void onResume() {
        this.mGLThread.onResume();
    }

    public void queueEvent(Runnable runnable) {
        this.mGLThread.queueEvent(runnable);
    }

    protected void onAttachedToWindow() {
        if (this.mDetached && this.mRenderer != null) {
            GLThread gLThread = this.mGLThread;
            int renderMode = gLThread != null ? gLThread.getRenderMode() : 1;
            GLThread gLThread2 = new GLThread(this.mThisWeakRef);
            this.mGLThread = gLThread2;
            if (renderMode != 1) {
                gLThread2.setRenderMode(renderMode);
            }
            this.mGLThread.start();
        }
        this.mDetached = false;
    }

    protected void onDetachedFromWindow() {
        GLThread gLThread = this.mGLThread;
        if (gLThread != null) {
            gLThread.requestExitAndWait();
        }
        this.mDetached = true;
    }

    /* loaded from: classes.dex */
    private class DefaultContextFactory implements EGLContextFactory {
        private int EGL_CONTEXT_CLIENT_VERSION;

        private DefaultContextFactory() {
            this.EGL_CONTEXT_CLIENT_VERSION = 12440;
        }

        /* synthetic */ DefaultContextFactory(GLEnvironment gLEnvironment, DefaultContextFactory defaultContextFactory) {
            this();
        }

        @Override // com.ihunuo.hnmjpeg.opengl2.utils.GLEnvironment.EGLContextFactory
        public EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
            int[] iArr = {this.EGL_CONTEXT_CLIENT_VERSION, GLEnvironment.this.mEGLContextClientVersion, 12344};
            EGLContext eGLContext = EGL10.EGL_NO_CONTEXT;
            if (GLEnvironment.this.mEGLContextClientVersion == 0) {
                iArr = null;
            }
            return egl10.eglCreateContext(eGLDisplay, eGLConfig, eGLContext, iArr);
        }

        @Override // com.ihunuo.hnmjpeg.opengl2.utils.GLEnvironment.EGLContextFactory
        public void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
            if (!egl10.eglDestroyContext(eGLDisplay, eGLContext)) {
                Log.e("DefaultContextFactory", "display:" + eGLDisplay + " context: " + eGLContext);
                EglHelper.throwEglException("eglDestroyContex", egl10.eglGetError());
            }
        }
    }

    /* loaded from: classes.dex */
    private static class DefaultWindowSurfaceFactory implements EGLWindowSurfaceFactory {
        private DefaultWindowSurfaceFactory() {
        }

        /* synthetic */ DefaultWindowSurfaceFactory(DefaultWindowSurfaceFactory defaultWindowSurfaceFactory) {
            this();
        }

        @Override // com.ihunuo.hnmjpeg.opengl2.utils.GLEnvironment.EGLWindowSurfaceFactory
        public EGLSurface createSurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj) {
            try {
                return egl10.eglCreateWindowSurface(eGLDisplay, eGLConfig, obj, null);
            } catch (IllegalArgumentException e) {
                Log.e(GLEnvironment.TAG, "eglCreateWindowSurface", e);
                return null;
            }
        }

        @Override // com.ihunuo.hnmjpeg.opengl2.utils.GLEnvironment.EGLWindowSurfaceFactory
        public void destroySurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface) {
            egl10.eglDestroySurface(eGLDisplay, eGLSurface);
        }
    }

    /* loaded from: classes.dex */
    private abstract class BaseConfigChooser implements EGLConfigChooser {
        protected int[] mConfigSpec;

        abstract EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr);

        public BaseConfigChooser(int[] iArr) {
            this.mConfigSpec = filterConfigSpec(iArr);
        }

        @Override // com.ihunuo.hnmjpeg.opengl2.utils.GLEnvironment.EGLConfigChooser
        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay) {
            int[] iArr = new int[1];
            if (!egl10.eglChooseConfig(eGLDisplay, this.mConfigSpec, null, 0, iArr)) {
                throw new IllegalArgumentException("eglChooseConfig failed");
            }
            int i = iArr[0];
            if (i <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            EGLConfig[] eGLConfigArr = new EGLConfig[i];
            if (!egl10.eglChooseConfig(eGLDisplay, this.mConfigSpec, eGLConfigArr, i, iArr)) {
                throw new IllegalArgumentException("eglChooseConfig#2 failed");
            }
            EGLConfig chooseConfig = chooseConfig(egl10, eGLDisplay, eGLConfigArr);
            if (chooseConfig != null) {
                return chooseConfig;
            }
            throw new IllegalArgumentException("No config chosen");
        }

        private int[] filterConfigSpec(int[] iArr) {
            if (GLEnvironment.this.mEGLContextClientVersion == 2 || GLEnvironment.this.mEGLContextClientVersion == 3) {
                int length = iArr.length;
                int[] iArr2 = new int[length + 2];
                int i = length - 1;
                System.arraycopy(iArr, 0, iArr2, 0, i);
                iArr2[i] = 12352;
                if (GLEnvironment.this.mEGLContextClientVersion == 2) {
                    iArr2[length] = 4;
                } else {
                    iArr2[length] = 64;
                }
                iArr2[length + 1] = 12344;
                return iArr2;
            }
            return iArr;
        }
    }

    /* loaded from: classes.dex */
    private class ComponentSizeChooser extends BaseConfigChooser {
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue;

        public ComponentSizeChooser(int i, int i2, int i3, int i4, int i5, int i6) {
            super(new int[]{12324, i, 12323, i2, 12322, i3, 12321, i4, 12325, i5, 12326, i6, 12344});
            this.mValue = new int[1];
            this.mRedSize = i;
            this.mGreenSize = i2;
            this.mBlueSize = i3;
            this.mAlphaSize = i4;
            this.mDepthSize = i5;
            this.mStencilSize = i6;
        }

        @Override // com.ihunuo.hnmjpeg.opengl2.utils.GLEnvironment.BaseConfigChooser
        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
            for (EGLConfig eGLConfig : eGLConfigArr) {
                int findConfigAttrib = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12325, 0);
                int findConfigAttrib2 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12326, 0);
                if (findConfigAttrib >= this.mDepthSize && findConfigAttrib2 >= this.mStencilSize) {
                    int findConfigAttrib3 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12324, 0);
                    int findConfigAttrib4 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12323, 0);
                    int findConfigAttrib5 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12322, 0);
                    int findConfigAttrib6 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12321, 0);
                    if (findConfigAttrib3 == this.mRedSize && findConfigAttrib4 == this.mGreenSize && findConfigAttrib5 == this.mBlueSize && findConfigAttrib6 == this.mAlphaSize) {
                        return eGLConfig;
                    }
                }
            }
            return null;
        }

        private int findConfigAttrib(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i, int i2) {
            if (egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.mValue)) {
                int i22 = this.mValue[0];
                return i22;
            }
            return i2;
        }
    }

    /* loaded from: classes.dex */
    private class SimpleEGLConfigChooser extends ComponentSizeChooser {
        public SimpleEGLConfigChooser(boolean z) {
            super(8, 8, 8, 0, z ? 16 : 0, 0);
        }
    }

    public void setNativeWindow(Object obj) {
        this.nativeWindow = obj;
    }

    /* loaded from: classes.dex */
    public static class EglHelper {
        EGL10 mEgl;
        EGLConfig mEglConfig;
        EGLContext mEglContext;
        EGLDisplay mEglDisplay;
        EGLSurface mEglSurface;
        private WeakReference<GLEnvironment> mGLSurfaceViewWeakRef;

        public EglHelper(WeakReference<GLEnvironment> weakReference) {
            this.mGLSurfaceViewWeakRef = weakReference;
        }

        public void start() {
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
            GLEnvironment gLEnvironment = this.mGLSurfaceViewWeakRef.get();
            if (gLEnvironment != null) {
                this.mEglConfig = gLEnvironment.mEGLConfigChooser.chooseConfig(this.mEgl, this.mEglDisplay);
                this.mEglContext = gLEnvironment.mEGLContextFactory.createContext(this.mEgl, this.mEglDisplay, this.mEglConfig);
            } else {
                this.mEglConfig = null;
                this.mEglContext = null;
            }
            EGLContext eGLContext = this.mEglContext;
            if (eGLContext == null || eGLContext == EGL10.EGL_NO_CONTEXT) {
                this.mEglContext = null;
                throwEglException("createContext");
            }
            this.mEglSurface = null;
        }

        public boolean createSurface() {
            if (this.mEgl == null) {
                throw new RuntimeException("egl not initialized");
            }
            if (this.mEglDisplay == null) {
                throw new RuntimeException("eglDisplay not initialized");
            }
            if (this.mEglConfig == null) {
                throw new RuntimeException("mEglConfig not initialized");
            }
            destroySurfaceImp();
            GLEnvironment gLEnvironment = this.mGLSurfaceViewWeakRef.get();
            if (gLEnvironment != null) {
                this.mEglSurface = gLEnvironment.mEGLWindowSurfaceFactory.createSurface(this.mEgl, this.mEglDisplay, this.mEglConfig, gLEnvironment.nativeWindow);
            } else {
                this.mEglSurface = null;
            }
            EGLSurface eGLSurface = this.mEglSurface;
            if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
                if (this.mEgl.eglGetError() == 12299) {
                    Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                    return false;
                }
                return false;
            }
            EGL10 egl10 = this.mEgl;
            EGLDisplay eGLDisplay = this.mEglDisplay;
            EGLSurface eGLSurface2 = this.mEglSurface;
            if (egl10.eglMakeCurrent(eGLDisplay, eGLSurface2, eGLSurface2, this.mEglContext)) {
                return true;
            }
            logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", this.mEgl.eglGetError());
            return false;
        }

        GL createGL() {
            GL gl = this.mEglContext.getGL();
            GLEnvironment gLEnvironment = this.mGLSurfaceViewWeakRef.get();
            if (gLEnvironment == null || (gLEnvironment.mDebugFlags & 3) == 0) {
                return gl;
            }
            return GLDebugHelper.wrap(gl, (gLEnvironment.mDebugFlags & 1) != 0 ? 1 : 0, (gLEnvironment.mDebugFlags & 2) != 0 ? new LogWriter() : null);
        }

        public int swap() {
            if (this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface)) {
                return 12288;
            }
            return this.mEgl.eglGetError();
        }

        public void destroySurface() {
            destroySurfaceImp();
        }

        private void destroySurfaceImp() {
            EGLSurface eGLSurface = this.mEglSurface;
            if (eGLSurface != null && eGLSurface != EGL10.EGL_NO_SURFACE) {
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                GLEnvironment gLEnvironment = this.mGLSurfaceViewWeakRef.get();
                if (gLEnvironment != null) {
                    gLEnvironment.mEGLWindowSurfaceFactory.destroySurface(this.mEgl, this.mEglDisplay, this.mEglSurface);
                }
                this.mEglSurface = null;
            }
        }

        public void finish() {
            if (this.mEglContext != null) {
                GLEnvironment gLEnvironment = this.mGLSurfaceViewWeakRef.get();
                if (gLEnvironment != null) {
                    gLEnvironment.mEGLContextFactory.destroyContext(this.mEgl, this.mEglDisplay, this.mEglContext);
                }
                this.mEglContext = null;
            }
            EGLDisplay eGLDisplay = this.mEglDisplay;
            if (eGLDisplay != null) {
                this.mEgl.eglTerminate(eGLDisplay);
                this.mEglDisplay = null;
            }
        }

        private void throwEglException(String str) {
            throwEglException(str, this.mEgl.eglGetError());
        }

        public static void throwEglException(String str, int i) {
            throw new RuntimeException(formatEglError(str, i));
        }

        public static void logEglErrorAsWarning(String str, String str2, int i) {
            Log.w(str, formatEglError(str2, i));
        }

        public static String formatEglError(String str, int i) {
            return String.valueOf(str) + " failed: " + i;
        }
    }

    /* loaded from: classes.dex */
    public static class GLThread extends Thread {
        private EglHelper mEglHelper;
        private boolean mExited;
        private boolean mFinishedCreatingEglSurface;
        private WeakReference<GLEnvironment> mGLSurfaceViewWeakRef;
        private boolean mHasSurface;
        private boolean mHaveEglContext;
        private boolean mHaveEglSurface;
        private boolean mPaused;
        private boolean mRenderComplete;
        private boolean mRequestPaused;
        private boolean mShouldExit;
        private boolean mShouldReleaseEglContext;
        private boolean mSurfaceIsBad;
        private boolean mWaitingForSurface;
        private ArrayList<Runnable> mEventQueue = new ArrayList<Runnable>();
        private boolean mSizeChanged = true;
        private int mWidth = 0;
        private int mHeight = 0;
        private boolean mRequestRender = true;
        private int mRenderMode = 1;
        private boolean mWantRenderNotification = false;

        GLThread(WeakReference<GLEnvironment> weakReference) {
            this.mGLSurfaceViewWeakRef = weakReference;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            setName("GLThread " + getId());
            try {
                guardedRun();
            } catch (InterruptedException e) {
            } catch (Throwable th) {
                GLEnvironment.sGLThreadManager.threadExiting(this);
            }
            GLEnvironment.sGLThreadManager.threadExiting(this);
        }

        private void stopEglSurfaceLocked() {
            if (this.mHaveEglSurface) {
                this.mHaveEglSurface = false;
                this.mEglHelper.destroySurface();
            }
        }

        private void stopEglContextLocked() {
            if (this.mHaveEglContext) {
                this.mEglHelper.finish();
                this.mHaveEglContext = false;
                GLEnvironment.sGLThreadManager.releaseEglContextLocked(this);
            }
        }

        private void guardedRun() throws InterruptedException {
            boolean z;
            boolean z2;
            GLEnvironment gLEnvironment;
            this.mEglHelper = new EglHelper(this.mGLSurfaceViewWeakRef);
            this.mHaveEglContext = false;
            this.mHaveEglSurface = false;
            this.mWantRenderNotification = false;
            boolean z3 = false;
            boolean z4 = false;
            boolean z5 = false;
            while (true) {
                try {
                    break;
                } catch (Throwable th) {
                    synchronized (GLEnvironment.sGLThreadManager) {
                        stopEglSurfaceLocked();
                        stopEglContextLocked();
                    }
                }
            }
            synchronized (GLEnvironment.sGLThreadManager) {
                while (!this.mShouldExit) {
                    if (!this.mEventQueue.isEmpty()) {
                        this.mEventQueue.remove(0);
                    } else {
                        if (this.mPaused != this.mRequestPaused) {
                            z = this.mRequestPaused;
                            this.mPaused = this.mRequestPaused;
                            GLEnvironment.sGLThreadManager.notifyAll();
                        } else {
                            z = false;
                        }
                        if (this.mShouldReleaseEglContext) {
                            stopEglSurfaceLocked();
                            stopEglContextLocked();
                            this.mShouldReleaseEglContext = false;
                            z5 = true;
                        }
                        if (z3) {
                            stopEglSurfaceLocked();
                            stopEglContextLocked();
                            z3 = false;
                        }
                        if (z && this.mHaveEglSurface) {
                            stopEglSurfaceLocked();
                        }
                        if (z && this.mHaveEglContext && ((gLEnvironment = this.mGLSurfaceViewWeakRef.get()) == null || !gLEnvironment.mPreserveEGLContextOnPause)) {
                            stopEglContextLocked();
                        }
                        if (!this.mHasSurface && !this.mWaitingForSurface) {
                            if (this.mHaveEglSurface) {
                                stopEglSurfaceLocked();
                            }
                            this.mWaitingForSurface = true;
                            this.mSurfaceIsBad = false;
                            GLEnvironment.sGLThreadManager.notifyAll();
                        }
                        if (this.mHasSurface && this.mWaitingForSurface) {
                            this.mWaitingForSurface = false;
                            GLEnvironment.sGLThreadManager.notifyAll();
                        }
                        if (z4) {
                            this.mWantRenderNotification = false;
                            this.mRenderComplete = true;
                            GLEnvironment.sGLThreadManager.notifyAll();
                            z4 = false;
                        }
                        if (readyToDraw()) {
                            if (!this.mHaveEglContext) {
                                if (z5) {
                                    z5 = false;
                                } else {
                                    try {
                                        this.mEglHelper.start();
                                        this.mHaveEglContext = true;
                                        GLEnvironment.sGLThreadManager.notifyAll();
                                    } catch (RuntimeException e) {
                                        GLEnvironment.sGLThreadManager.releaseEglContextLocked(this);
                                        throw e;
                                    }
                                }
                            }
                            if (this.mHaveEglContext && !this.mHaveEglSurface) {
                                this.mHaveEglSurface = true;
                            }
                            if (this.mHaveEglSurface) {
                                if (this.mSizeChanged) {
                                    int i = this.mWidth;
                                    int i2 = this.mHeight;
                                    this.mWantRenderNotification = true;
                                    this.mSizeChanged = false;
                                    z2 = false;
                                } else {
                                    z2 = false;
                                }
                                this.mRequestRender = z2;
                                GLEnvironment.sGLThreadManager.notifyAll();
                                if (this.mWantRenderNotification) {
                                }
                            }
                        }
                        GLEnvironment.sGLThreadManager.wait();
                    }
                }
                synchronized (GLEnvironment.sGLThreadManager) {
                    stopEglSurfaceLocked();
                    stopEglContextLocked();
                }
            }
        }

        public boolean ableToDraw() {
            return this.mHaveEglContext && this.mHaveEglSurface && readyToDraw();
        }

        private boolean readyToDraw() {
            return !this.mPaused && this.mHasSurface && !this.mSurfaceIsBad && this.mWidth > 0 && this.mHeight > 0 && (this.mRequestRender || this.mRenderMode == 1);
        }

        public void setRenderMode(int i) {
            if (i >= 0 && i <= 1) {
                synchronized (GLEnvironment.sGLThreadManager) {
                    this.mRenderMode = i;
                    GLEnvironment.sGLThreadManager.notifyAll();
                }
                return;
            }
            throw new IllegalArgumentException("renderMode");
        }

        public int getRenderMode() {
            int i;
            synchronized (GLEnvironment.sGLThreadManager) {
                i = this.mRenderMode;
            }
            return i;
        }

        public void requestRender() {
            synchronized (GLEnvironment.sGLThreadManager) {
                this.mRequestRender = true;
                GLEnvironment.sGLThreadManager.notifyAll();
            }
        }

        public void requestRenderAndWait() {
            synchronized (GLEnvironment.sGLThreadManager) {
                if (Thread.currentThread() != this) {
                    this.mWantRenderNotification = true;
                    this.mRequestRender = true;
                    this.mRenderComplete = false;
                    GLEnvironment.sGLThreadManager.notifyAll();
                    while (!this.mExited && !this.mPaused && !this.mRenderComplete && ableToDraw()) {
                        try {
                            GLEnvironment.sGLThreadManager.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }

        public void surfaceCreated() {
            synchronized (GLEnvironment.sGLThreadManager) {
                this.mHasSurface = true;
                this.mFinishedCreatingEglSurface = false;
                GLEnvironment.sGLThreadManager.notifyAll();
                while (this.mWaitingForSurface && !this.mFinishedCreatingEglSurface && !this.mExited) {
                    try {
                        GLEnvironment.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void surfaceDestroyed() {
            synchronized (GLEnvironment.sGLThreadManager) {
                this.mHasSurface = false;
                GLEnvironment.sGLThreadManager.notifyAll();
                while (!this.mWaitingForSurface && !this.mExited) {
                    try {
                        GLEnvironment.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onPause() {
            synchronized (GLEnvironment.sGLThreadManager) {
                this.mRequestPaused = true;
                GLEnvironment.sGLThreadManager.notifyAll();
                while (!this.mExited && !this.mPaused) {
                    try {
                        GLEnvironment.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onResume() {
            synchronized (GLEnvironment.sGLThreadManager) {
                this.mRequestPaused = false;
                this.mRequestRender = true;
                this.mRenderComplete = false;
                GLEnvironment.sGLThreadManager.notifyAll();
                while (!this.mExited && this.mPaused && !this.mRenderComplete) {
                    try {
                        GLEnvironment.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onWindowResize(int i, int i2) {
            synchronized (GLEnvironment.sGLThreadManager) {
                this.mWidth = i;
                this.mHeight = i2;
                this.mSizeChanged = true;
                this.mRequestRender = true;
                this.mRenderComplete = false;
                if (Thread.currentThread() != this) {
                    GLEnvironment.sGLThreadManager.notifyAll();
                    while (!this.mExited && !this.mPaused && !this.mRenderComplete && ableToDraw()) {
                        try {
                            GLEnvironment.sGLThreadManager.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }

        public void requestExitAndWait() {
            synchronized (GLEnvironment.sGLThreadManager) {
                this.mShouldExit = true;
                GLEnvironment.sGLThreadManager.notifyAll();
                while (!this.mExited) {
                    try {
                        GLEnvironment.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void requestReleaseEglContextLocked() {
            this.mShouldReleaseEglContext = true;
            GLEnvironment.sGLThreadManager.notifyAll();
        }

        public void queueEvent(Runnable runnable) {
            if (runnable != null) {
                synchronized (GLEnvironment.sGLThreadManager) {
                    this.mEventQueue.add(runnable);
                    GLEnvironment.sGLThreadManager.notifyAll();
                }
                return;
            }
            throw new IllegalArgumentException("r must not be null");
        }
    }

    /* loaded from: classes.dex */
    public static class LogWriter extends Writer {
        private StringBuilder mBuilder = new StringBuilder();

        LogWriter() {
        }

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            flushBuilder();
        }

        @Override // java.io.Writer, java.io.Flushable
        public void flush() {
            flushBuilder();
        }

        @Override // java.io.Writer
        public void write(char[] cArr, int i, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                char c = cArr[i + i3];
                if (c == '\n') {
                    flushBuilder();
                } else {
                    this.mBuilder.append(c);
                }
            }
        }

        private void flushBuilder() {
            if (this.mBuilder.length() > 0) {
                Log.v(GLEnvironment.TAG, this.mBuilder.toString());
                StringBuilder sb = this.mBuilder;
                sb.delete(0, sb.length());
            }
        }
    }

    private void checkRenderThreadState() {
        if (this.mGLThread != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }

    /* loaded from: classes.dex */
    public static class GLThreadManager {
        private static String TAG = "GLThreadManager";

        private GLThreadManager() {
        }

        /* synthetic */ GLThreadManager(GLThreadManager gLThreadManager) {
            this();
        }

        public synchronized void threadExiting(GLThread gLThread) {
            gLThread.mExited = true;
            notifyAll();
        }

        public void releaseEglContextLocked(GLThread gLThread) {
            notifyAll();
        }
    }
}
