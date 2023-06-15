package com.ihunuo.hnmjpeg.opengl2.imgvideo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.ihunuo.hnmjpeg.opengl2.egl.HsnShaderUtil;
import com.obana.fydmod.R;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class HsnImgFboRender {
    private Context context;
    private int fPosition;
    private FloatBuffer fragmentBuffer;
    private float[] fragmentData = {0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    private float[] matrix = new float[16];
    private int program;
    private int sampler;
    private int umatrix;
    private int vPosition;
    private int vboId;
    private FloatBuffer vertexBuffer;
    private float[] vertexData;

    public HsnImgFboRender(Context context) {
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

    public void onCreate() {
        int createProgram = HsnShaderUtil.createProgram(HsnShaderUtil.getRawResource(this.context, R.raw.vertex_shader_opengl2), HsnShaderUtil.getRawResource(this.context, R.raw.fragment_shader_screen));
        this.program = createProgram;
        this.vPosition = GLES20.glGetAttribLocation(createProgram, "v_Position");
        this.fPosition = GLES20.glGetAttribLocation(this.program, "f_Position");
        this.sampler = GLES20.glGetUniformLocation(this.program, "sTexture");
        this.umatrix = GLES20.glGetUniformLocation(this.program, "u_Matrix");
        GLES20.glUniform1i(this.sampler, 0);
        int[] iArr = new int[1];
        GLES20.glGenBuffers(1, iArr, 0);
        int i = iArr[0];
        this.vboId = i;
        GLES20.glBindBuffer(34962, i);
        GLES20.glBufferData(34962, (this.vertexData.length * 4) + (this.fragmentData.length * 4), null, 35044);
        GLES20.glBufferSubData(34962, 0, this.vertexData.length * 4, this.vertexBuffer);
        GLES20.glBufferSubData(34962, this.vertexData.length * 4, this.fragmentData.length * 4, this.fragmentBuffer);
        GLES20.glBindBuffer(34962, 0);
    }

    public void onChange(int i, int i2) {
        GLES20.glViewport(0, 0, i, i2);
        Matrix.orthoM(this.matrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
    }

    public void onDraw(int i) {
        GLES20.glClear(16384);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glUseProgram(this.program);
        GLES20.glUniformMatrix4fv(this.umatrix, 1, false, this.matrix, 0);
        GLES20.glBindTexture(3553, i);
        GLES20.glBindBuffer(34962, this.vboId);
        GLES20.glEnableVertexAttribArray(this.vPosition);
        GLES20.glVertexAttribPointer(this.vPosition, 2, 5126, false, 8, 0);
        GLES20.glEnableVertexAttribArray(this.fPosition);
        GLES20.glVertexAttribPointer(this.fPosition, 2, 5126, false, 8, this.vertexData.length * 4);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glBindTexture(3553, 0);
        GLES20.glBindBuffer(34962, 0);
    }
}
