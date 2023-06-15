package com.ihunuo.hnmjpeg.opengl2.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.ihunuo.hnmjpeg.opengl2.egl.HsnEGLSurfaceView;
import com.ihunuo.hnmjpeg.opengl2.egl.HsnShaderUtil;
import com.obana.fydmod.R;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class HsnTextureRender implements HsnEGLSurfaceView.HsnGLRender {
    public Bitmap bitmap;
    private Context context;
    private int fPosition;
    private FloatBuffer fragmentBuffer;
    private float[] fragmentData = {0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    private int program;
    private int sampler;
    public int textureid;
    private int vPosition;
    private FloatBuffer vertexBuffer;
    private float[] vertexData;

    public HsnTextureRender(Context context) {
        float[] fArr = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
        this.vertexData = fArr;
        this.context = context;
        FloatBuffer put = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(this.vertexData);
        this.vertexBuffer = put;
        put.position(0);
        FloatBuffer put2 = ByteBuffer.allocateDirect(this.fragmentData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(this.fragmentData);
        this.fragmentBuffer = put2;
        put2.position(0);
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.egl.HsnEGLSurfaceView.HsnGLRender
    public void onSurfaceCreated() {
        int createProgram = HsnShaderUtil.createProgram(HsnShaderUtil.getRawResource(this.context, R.raw.vertex_shader_opengl2), HsnShaderUtil.getRawResource(this.context, R.raw.fragment_shader_opengl2));
        this.program = createProgram;
        this.vPosition = GLES20.glGetAttribLocation(createProgram, "v_Position");
        this.fPosition = GLES20.glGetAttribLocation(this.program, "f_Position");
        this.sampler = GLES20.glGetUniformLocation(this.program, "sTexture");
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        int i = iArr[0];
        this.textureid = i;
        GLES20.glBindTexture(3553, i);
        GLES20.glActiveTexture(33984);
        GLES20.glUniform1i(this.sampler, 0);
        GLES20.glTexParameteri(3553, 10242, 10497);
        GLES20.glTexParameteri(3553, 10243, 10497);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glBindTexture(3553, 0);
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.egl.HsnEGLSurfaceView.HsnGLRender
    public void onSurfaceChanged(int i, int i2) {
        GLES20.glViewport(0, 0, i, i2);
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.egl.HsnEGLSurfaceView.HsnGLRender
    public void onDrawFrame() {
        GLES20.glClear(16384);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glUseProgram(this.program);
        GLES20.glBindTexture(3553, this.textureid);
        GLES20.glEnableVertexAttribArray(this.vPosition);
        GLES20.glVertexAttribPointer(this.vPosition, 2, 5126, false, 8, (Buffer) this.vertexBuffer);
        GLES20.glEnableVertexAttribArray(this.fPosition);
        GLES20.glVertexAttribPointer(this.fPosition, 2, 5126, false, 8, (Buffer) this.fragmentBuffer);
        GLES20.glDrawArrays(5, 0, 4);
        Bitmap bitmap = this.bitmap;
        if (bitmap != null) {
            GLUtils.texImage2D(3553, 0, bitmap, 0);
        }
        GLES20.glBindTexture(3553, 0);
    }
}
