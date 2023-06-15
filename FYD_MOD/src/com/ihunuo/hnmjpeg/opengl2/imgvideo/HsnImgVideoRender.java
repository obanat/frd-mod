package com.ihunuo.hnmjpeg.opengl2.imgvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;
import com.ihunuo.hnmjpeg.Listener.HNMjpegListener;
import com.ihunuo.hnmjpeg.opengl2.egl.HsnEGLSurfaceView;
import com.ihunuo.hnmjpeg.opengl2.egl.HsnShaderUtil;
import com.ihunuo.hnmjpeg.opengl2.filter.AFilter;
import com.ihunuo.hnmjpeg.opengl2.filter.Beauty;
import com.ihunuo.hnmjpeg.opengl2.filter.GroupFilter;
import com.ihunuo.hnmjpeg.opengl2.filter.LookupFilter;
import com.ihunuo.hnmjpeg.opengl2.filter.NoFilter;
import com.ihunuo.hnmjpeg.utils.BitmapUtils;
import com.ihunuo.hnmjpeg.utils.UIUtils;
import com.obana.fydmod.R;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/* loaded from: classes.dex */
public class HsnImgVideoRender implements HsnEGLSurfaceView.HsnGLRender {
    public static int SplitScreen = 1;
    public static int isTalkPhotoSize;
    private Bitmap bitmap;
    public Bitmap bitmapDaTouTie;
    private Context context;
    private int fPosition;
    private int fboId;
    private FloatBuffer fragmentBuffer;
    private HNMjpegListener hnMjpegListener;
    private HsnImgFboRender hsnImgFboRender;
    private int imgTextureId;
    private boolean isTranslateM;
    private final GroupFilter mGroupFilter;
    private AFilter mShowFilter;
    private OnRenderCreateListener onRenderCreateListener;
    private int program;
    private byte[] rgb;
    private int sampler;
    private int sampler_brightness;
    private int sampler_move_x;
    private int sampler_move_y;
    private int sampler_params;
    private int sampler_singleStepOffset;
    private int sampler_zoom;
    private int talkphotoH;
    private int talkphotoW;
    private int textureid;
    private int umatrix;
    private int vPosition;
    private int vboId;
    private FloatBuffer vertexBuffer;
    private float[] vertexData = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -0.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 1.0f};
    private float[] fragmentData = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
    private float[] matrix = new float[16];
    public float zoom = 1.0f;
    public boolean zoomFlag = false;
    public float[] xyz_move = new float[3];
    public int filter = 1;
    private int renderMode = 0;
    private int srcImg = 0;
    private int widthRGB = 0;
    private int heightRGB = 0;
    private int widthScreen = 0;
    private int heightScreen = 0;
    boolean isRotate = false;
    boolean isVR = false;
    int[] xyz = new int[4];
    public boolean isTalkPhoto = false;
    public boolean isTalkPhotoSave = false;
    public boolean isTaikPhotoPush = true;
    public boolean isTaikPhotoDaTouTie = false;
    public String talkPhotoPath = "";
    private boolean isOnSurfaceChanged = false;
    private int mShowType = 1;
    private float[] f131SM = new float[16];

    /* loaded from: classes.dex */
    public interface OnRenderCreateListener {
        void onCreate(int i);
    }

    private float[] getParams(float f, float f2) {
        float f3 = (f2 * 0.6f) - 0.2f;
        return new float[]{1.6f - (1.2f * f), 1.3f - (f * 0.6f), f3, f3};
    }

    float getBright(float f) {
        return (f - 0.5f) * 0.6f;
    }

    float[] getSingleStepOffset(float f, float f2) {
        return new float[]{2.0f / f, 2.0f / f2};
    }

    public HsnImgVideoRender(Context context) {
        this.context = context;
        this.hsnImgFboRender = new HsnImgFboRender(context);
        FloatBuffer put = ByteBuffer.allocateDirect(this.vertexData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(this.vertexData);
        this.vertexBuffer = put;
        put.position(0);
        FloatBuffer put2 = ByteBuffer.allocateDirect(this.fragmentData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(this.fragmentData);
        this.fragmentBuffer = put2;
        put2.position(0);
        this.mShowFilter = new NoFilter(context.getResources());
        this.mGroupFilter = new GroupFilter(context.getResources());
        LookupFilter lookupFilter = new LookupFilter(context.getResources());
        lookupFilter.setMaskImage("lookup/purity.png");
        lookupFilter.setIntensity(1.0f);
        this.mGroupFilter.addFilter(lookupFilter);
        Beauty beauty = new Beauty(context.getResources());
        beauty.setFlag(6);
        this.mGroupFilter.addFilter(beauty);
    }

    public void setOnRenderCreateListener(OnRenderCreateListener onRenderCreateListener) {
        this.onRenderCreateListener = onRenderCreateListener;
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.egl.HsnEGLSurfaceView.HsnGLRender
    public void onSurfaceCreated() {
        this.hsnImgFboRender.onCreate();
        int createProgram = HsnShaderUtil.createProgram(HsnShaderUtil.getRawResource(this.context, R.raw.vertex_shader_opengl2), HsnShaderUtil.getRawResource(this.context, R.raw.fragment_shader_screen));
        this.program = createProgram;
        this.vPosition = GLES20.glGetAttribLocation(createProgram, "v_Position");
        this.fPosition = GLES20.glGetAttribLocation(this.program, "f_Position");
        this.sampler = GLES20.glGetUniformLocation(this.program, "sTexture");
        this.umatrix = GLES20.glGetUniformLocation(this.program, "u_Matrix");
        this.sampler_zoom = GLES20.glGetUniformLocation(this.program, "sampler_zoom");
        this.sampler_move_x = GLES20.glGetUniformLocation(this.program, "sampler_move_x");
        this.sampler_move_y = GLES20.glGetUniformLocation(this.program, "sampler_move_y");
        this.sampler_singleStepOffset = GLES20.glGetUniformLocation(this.program, "singleStepOffset");
        this.sampler_params = GLES20.glGetUniformLocation(this.program, "params");
        this.sampler_brightness = GLES20.glGetUniformLocation(this.program, "brightness");
        GLES20.glUniform1i(this.sampler, 0);
        this.mGroupFilter.create();
        this.mShowFilter.create();
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.egl.HsnEGLSurfaceView.HsnGLRender
    public void onSurfaceChanged(int i, int i2) {
        this.widthScreen = i;
        this.heightScreen = i2;
        this.mShowFilter.setSize(i, i2);
        this.mGroupFilter.setSize(i, i2);
        int[] iArr = new int[1];
        GLES20.glGenBuffers(1, iArr, 0);
        int i3 = iArr[0];
        this.vboId = i3;
        GLES20.glBindBuffer(34962, i3);
        GLES20.glBufferData(34962, (this.vertexData.length * 4) + (this.fragmentData.length * 4), null, 35044);
        GLES20.glBufferSubData(34962, 0, this.vertexData.length * 4, this.vertexBuffer);
        GLES20.glBufferSubData(34962, this.vertexData.length * 4, this.fragmentData.length * 4, this.fragmentBuffer);
        GLES20.glBindBuffer(34962, 0);
        int[] iArr2 = new int[1];
        GLES20.glGenBuffers(1, iArr2, 0);
        int i4 = iArr2[0];
        this.fboId = i4;
        GLES20.glBindFramebuffer(36160, i4);
        int[] iArr3 = new int[1];
        GLES20.glGenTextures(1, iArr3, 0);
        int i5 = iArr3[0];
        this.textureid = i5;
        GLES20.glBindTexture(3553, i5);
        GLES20.glTexParameteri(3553, 10242, 10497);
        GLES20.glTexParameteri(3553, 10243, 10497);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexImage2D(3553, 0, 6408, i, i2, 0, 6408, 5121, null);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.textureid, 0);
        if (GLES20.glCheckFramebufferStatus(36160) != 36053) {
            Log.e("ywl5320", "fbo wrong");
        } else {
            Log.e("ywl5320", "fbo success" + i + " " + i2);
        }
        GLES20.glBindTexture(3553, 0);
        GLES20.glBindFramebuffer(36160, 0);
        OnRenderCreateListener onRenderCreateListener = this.onRenderCreateListener;
        if (onRenderCreateListener != null) {
            onRenderCreateListener.onCreate(this.textureid);
        }
        GLES20.glViewport(0, 0, i, i2);
        Matrix.orthoM(this.matrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
        this.hsnImgFboRender.onChange(i, i2);
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.egl.HsnEGLSurfaceView.HsnGLRender
    public void onDrawFrame() {
        int loadTexrute = loadTexrute();
        this.imgTextureId = loadTexrute;
        if (this.filter == 100) {
            this.mGroupFilter.setTextureId(loadTexrute);
            this.mGroupFilter.draw();
            this.mShowFilter.setTextureId(this.mGroupFilter.getOutputTexture());
            this.mShowFilter.draw();
            GLES20.glBindTexture(3553, 0);
            GLES20.glBindBuffer(34962, 0);
            GLES20.glDeleteTextures(1, new int[]{this.imgTextureId}, 0);
            GLES20.glBindFramebuffer(36160, 0);
        } else {
            GLES20.glBindFramebuffer(36160, this.fboId);
            GLES20.glClear(16384);
            GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glUseProgram(this.program);
            if (this.isRotate) {
                float[] fArr = this.matrix;
                int[] iArr = this.xyz;
                Matrix.rotateM(fArr, 0, iArr[0], iArr[1], iArr[2], iArr[3]);
                this.isRotate = false;
            }
            GLES20.glUniformMatrix4fv(this.umatrix, 1, false, this.matrix, 0);
            GLES20.glUniform1f(this.sampler_zoom, this.zoom);
            GLES20.glUniform1f(this.sampler_move_x, this.xyz_move[0]);
            GLES20.glUniform1f(this.sampler_move_y, this.xyz_move[1]);
            float[] params = getParams(0.6f, 0.6f);
            GLES20.glUniform4f(this.sampler_params, params[0], params[1], params[2], params[3]);
            GLES20.glUniform1f(this.sampler_brightness, getBright(0.6f));
            float[] singleStepOffset = getSingleStepOffset(this.widthScreen, this.heightScreen);
            GLES20.glUniform2f(this.sampler_singleStepOffset, singleStepOffset[0], singleStepOffset[1]);
            if (this.isVR) {
                GLES20.glBindTexture(3553, this.imgTextureId);
                GLES20.glBindBuffer(34962, this.vboId);
                GLES20.glEnableVertexAttribArray(this.vPosition);
                GLES20.glVertexAttribPointer(this.vPosition, 2, 5126, false, 8, 32);
                GLES20.glEnableVertexAttribArray(this.fPosition);
                GLES20.glVertexAttribPointer(this.fPosition, 2, 5126, false, 8, this.vertexData.length * 4);
                GLES20.glDrawArrays(5, 0, 4);
                GLES20.glBindTexture(3553, this.imgTextureId);
                GLES20.glBindBuffer(34962, this.vboId);
                GLES20.glEnableVertexAttribArray(this.vPosition);
                GLES20.glVertexAttribPointer(this.vPosition, 2, 5126, false, 8, 64);
                GLES20.glEnableVertexAttribArray(this.fPosition);
                GLES20.glVertexAttribPointer(this.fPosition, 2, 5126, false, 8, this.vertexData.length * 4);
                GLES20.glDrawArrays(5, 0, 4);
            } else {
                GLES20.glBindTexture(3553, this.imgTextureId);
                GLES20.glBindBuffer(34962, this.vboId);
                GLES20.glEnableVertexAttribArray(this.vPosition);
                GLES20.glVertexAttribPointer(this.vPosition, 2, 5126, false, 8, 0);
                GLES20.glEnableVertexAttribArray(this.fPosition);
                GLES20.glVertexAttribPointer(this.fPosition, 2, 5126, false, 8, this.vertexData.length * 4);
                GLES20.glDrawArrays(5, 0, 4);
            }
            GLES20.glBindTexture(3553, 0);
            GLES20.glBindBuffer(34962, 0);
            GLES20.glDeleteTextures(1, new int[]{this.imgTextureId}, 0);
            GLES20.glBindFramebuffer(36160, 0);
            this.hsnImgFboRender.onDraw(this.textureid);
        }
        if (this.isTalkPhoto) {
            talk(this.talkPhotoPath, this.hnMjpegListener);
        }
    }

    private int loadTexrute() {
        int i = this.renderMode;
        if (i == 0) {
            //return HsnShaderUtil.loadTexrute(this.srcImg, this.context);
        }
        if (i == 1) {
            return HsnShaderUtil.loadTexruteBitmap(this.bitmap, this.context);
        }
        if (i == 2) {
            return HsnShaderUtil.loadRGBTexture(this.rgb, this.widthRGB, this.heightRGB);
        }
        return 0;
    }

    public void setCurrentImgSrc(int i) {
        this.renderMode = 0;
        this.srcImg = i;
    }

    public void setCurrentBitmap(Bitmap bitmap) {
        this.renderMode = 1;
        this.bitmap = bitmap;
    }

    public void setCurrentRGB(byte[] bArr, int i, int i2) {
        this.renderMode = 2;
        this.rgb = bArr;
        this.widthRGB = i;
        this.heightRGB = i2;
    }

    public void setRotate(int i, int i2, int i3, int i4) {
        int[] iArr = this.xyz;
        iArr[0] = i;
        iArr[1] = i2;
        iArr[2] = i3;
        iArr[3] = i4;
        this.isRotate = true;
    }

    public void setIsVR(boolean z) {
        this.isVR = z;
    }

    public void talkPhoto(boolean z, String str, HNMjpegListener hNMjpegListener, int i, int i2) {
        Log.d("ccc", "talkPhoto: " + i + " " + i2);
        this.isTalkPhoto = true;
        this.isTalkPhotoSave = z;
        this.talkphotoW = i;
        this.talkphotoH = i2;
        this.talkPhotoPath = str;
        this.hnMjpegListener = hNMjpegListener;
    }

    private void talk(String str, HNMjpegListener hNMjpegListener) {
        Bitmap bitmap;
        Bitmap cutBitmap = cutBitmap(0, 0, this.widthScreen, this.heightScreen);
        String str2 = new StringBuilder(String.valueOf(System.currentTimeMillis())).toString();
        int i = 2160;
        int i2 = 4096;
        if (isTalkPhotoSize == 6 && cutBitmap.getWidth() == 1920) {
            if (cutBitmap.getWidth() == 1280) {
                i2 = cutBitmap.getWidth();
                i = cutBitmap.getHeight();
            } else if (cutBitmap.getWidth() != 1920) {
                i = 0;
                i2 = 0;
            }
            cutBitmap = Bitmap.createScaledBitmap(cutBitmap, i2, i, true);
        } else {
            cutBitmap.getWidth();
            cutBitmap.getHeight();
            int i3 = isTalkPhotoSize;
            if (i3 == 1) {
                cutBitmap = Bitmap.createScaledBitmap(cutBitmap, 640, 480, true);
            } else if (i3 == 2) {
                cutBitmap = Bitmap.createScaledBitmap(cutBitmap, 1280, 720, true);
            } else if (i3 == 3) {
                cutBitmap = Bitmap.createScaledBitmap(cutBitmap, 1920, 1080, true);
            } else if (i3 == 4) {
                cutBitmap = Bitmap.createScaledBitmap(cutBitmap, 4096, 2160, true);
            }
        }
        if (this.isTaikPhotoDaTouTie && (bitmap = this.bitmapDaTouTie) != null && !bitmap.isRecycled()) {
            cutBitmap = BitmapUtils.mergeBitmap(cutBitmap, this.bitmapDaTouTie);
        }
        if (this.isTalkPhotoSave) {
            UIUtils.bitmaptofile(cutBitmap, str2, Environment.getExternalStorageDirectory() + "/FYD-UAV/PhotoVideo");
        } else {
            hNMjpegListener.takePhotoSuccssed(cutBitmap);
        }
        if (this.isTaikPhotoPush) {
            String str3 = String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath()) + "/DCIM/Camera/";
            UIUtils.bitmaptofile(cutBitmap, str2, str3);
            UIUtils.scanIntoMediaStore(this.context, new File(String.valueOf(str3) + str2 + ".jpeg"));
        }
        this.isTalkPhoto = false;
    }

    private Bitmap cutBitmap(int i, int i2, int i3, int i4) {
        int i5 = i3 * i4;
        int[] iArr = new int[i5];
        int[] iArr2 = new int[i5];
        IntBuffer wrap = IntBuffer.wrap(iArr);
        wrap.position(0);
        try {
            GLES20.glReadPixels(i, i2, i3, i4, 6408, 5121, wrap);
            for (int i6 = 0; i6 < i4; i6++) {
                int i7 = i6 * i3;
                int i8 = ((i4 - i6) - 1) * i3;
                for (int i9 = 0; i9 < i3; i9++) {
                    int i10 = iArr[i7 + i9];
                    iArr2[i8 + i9] = ((-16711936) & i10) | ((i10 << 16) & 16711680) | ((i10 >> 16) & 255);
                }
            }
            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(Bitmap.createBitmap(iArr2, i3, i4, Bitmap.Config.ARGB_8888), this.talkphotoW, this.talkphotoH, true);
            wrap.clear();
            return createScaledBitmap;
        } catch (GLException e) {
            return null;
        }
    }

    public void setZoom(float f) {
        this.zoom = f;
        this.zoomFlag = true;
    }

    public void setXYZMove(float f, float f2, float f3) {
        float[] fArr = this.xyz_move;
        fArr[0] = f;
        fArr[1] = f2;
        fArr[2] = f3;
        this.isTranslateM = false;
    }
}
