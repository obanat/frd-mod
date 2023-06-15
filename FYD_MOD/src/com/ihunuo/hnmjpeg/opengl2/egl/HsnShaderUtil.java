package com.ihunuo.hnmjpeg.opengl2.egl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class HsnShaderUtil {
    public static String getRawResource(Context context, int i) {
        String readLine = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(i)));
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            try {
                readLine = bufferedReader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
            } else {
                break;
            }
        }
        try {
			bufferedReader.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
        return stringBuffer.toString();
    }

    private static int loadShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        if (glCreateShader != 0) {
            GLES20.glShaderSource(glCreateShader, str);
            GLES20.glCompileShader(glCreateShader);
            int[] iArr = new int[1];
            GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
            if (iArr[0] != 1) {
                Log.d("ywl5320", "shader compile error");
                GLES20.glDeleteShader(glCreateShader);
                return 0;
            }
            return glCreateShader;
        }
        return 0;
    }

    public static int createProgram(String str, String str2) {
        int loadShader = loadShader(35633, str);
        int loadShader2 = loadShader(35632, str2);
        if (loadShader == 0 || loadShader2 == 0) {
            return 0;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(glCreateProgram, loadShader);
        GLES20.glAttachShader(glCreateProgram, loadShader2);
        GLES20.glLinkProgram(glCreateProgram);
        return glCreateProgram;
    }

    public static Bitmap createTextImage(String str, int i, String str2, String str3, int i2) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(str2));
        paint.setTextSize(i);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        float measureText = paint.measureText(str, 0, str.length());
        float f = paint.getFontMetrics().top;
        float f2 = i2 * 2;
        Bitmap createBitmap = Bitmap.createBitmap((int) (measureText + f2), (int) ((paint.getFontMetrics().bottom - f) + f2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(Color.parseColor(str3));
        float f3 = i2;
        canvas.drawText(str, f3, (-f) + f3, paint);
        return createBitmap;
    }

    public static int loadBitmapTexture(Bitmap bitmap) {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        GLES20.glBindTexture(3553, iArr[0]);
        GLES20.glTexParameteri(3553, 10242, 10497);
        GLES20.glTexParameteri(3553, 10243, 10497);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        ByteBuffer allocate = ByteBuffer.allocate(bitmap.getHeight() * bitmap.getWidth() * 4);
        bitmap.copyPixelsToBuffer(allocate);
        allocate.flip();
        GLES20.glTexImage2D(3553, 0, 6408, bitmap.getWidth(), bitmap.getHeight(), 0, 6408, 5121, allocate);
        return iArr[0];
    }

    public static int loadRGBTexture(byte[] bArr, int i, int i2) {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        GLES20.glBindTexture(3553, iArr[0]);
        GLES20.glTexParameteri(3553, 10242, 10497);
        GLES20.glTexParameteri(3553, 10243, 10497);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        ByteBuffer allocate = ByteBuffer.allocate(i * i2 * 4);
        allocate.put(bArr);
        allocate.flip();
        GLES20.glTexImage2D(3553, 0, 6408, i, i2, 0, 6408, 5121, allocate);
        return iArr[0];
    }

    public static int loadTexrute(int i, Context context) {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        GLES20.glBindTexture(3553, iArr[0]);
        GLES20.glTexParameteri(3553, 10242, 10497);
        GLES20.glTexParameteri(3553, 10243, 10497);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), i);
        GLUtils.texImage2D(3553, 0, decodeResource, 0);
        decodeResource.recycle();
        GLES20.glBindTexture(3553, 0);
        return iArr[0];
    }

    public static int loadTexruteBitmap(Bitmap bitmap, Context context) {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        GLES20.glBindTexture(3553, iArr[0]);
        GLES20.glTexParameteri(3553, 10242, 10497);
        GLES20.glTexParameteri(3553, 10243, 10497);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        GLES20.glBindTexture(3553, 0);
        return iArr[0];
    }
}
