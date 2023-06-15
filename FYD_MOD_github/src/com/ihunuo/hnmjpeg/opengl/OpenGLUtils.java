package com.ihunuo.hnmjpeg.opengl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class OpenGLUtils {
    private static int convertYUVtoARGB(int i, int i2, int i3) {
        float f = i2;
        int i4 = ((int) (1.402f * f)) + i;
        float f2 = i3;
        int i5 = i - ((int) ((0.344f * f2) + (0.714f * f)));
        int i6 = i + ((int) (1.772f * f2));
        if (i4 > 255) {
            i4 = 255;
        } else if (i4 < 0) {
            i4 = 0;
        }
        if (i5 > 255) {
            i5 = 255;
        } else if (i5 < 0) {
            i5 = 0;
        }
        if (i6 > 255) {
            i6 = 255;
        } else if (i6 < 0) {
            i6 = 0;
        }
        return (i5 << 8) | i6 | (-16777216) | (i4 << 16);
    }

    public static boolean detectOpenGLES20(Context context) {
        return ((ActivityManager) context.getSystemService("activity")).getDeviceConfigurationInfo().reqGlEsVersion >= 131072;
    }

    public static List convertYUV420_I420to_Y_U_V(byte[] bArr, int i, int i2) {
        int i3 = i * i2;
        byte[] bArr2 = new byte[i3];
        int i4 = i3 / 4;
        byte[] bArr3 = new byte[i4];
        byte[] bArr4 = new byte[i4];
        ArrayList arrayList = new ArrayList();
        System.arraycopy(bArr, 0, bArr2, 0, i3);
        int i5 = (((i3 * 3) / 2) - i3) / 2;
        System.arraycopy(bArr, i3, bArr3, 0, i5);
        System.arraycopy(bArr, i3 + i4, bArr4, 0, i5);
        arrayList.add(bArr2);
        arrayList.add(bArr3);
        arrayList.add(bArr4);
        return arrayList;
    }

    public static void convertYUV420_NV21to_Y_U_V(byte[] bArr, int i, int i2, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        int i3 = i * i2;
        int i4 = ((i3 * 3) / 2) - i3;
        System.arraycopy(bArr, 0, bArr2, 0, i3);
        for (int i5 = 0; i5 < i4; i5 += 2) {
            int i6 = i5 / 2;
            int i7 = i3 + i5;
            bArr4[i6] = bArr[i7];
            bArr3[i6] = bArr[i7 + 1];
        }
    }

    public static void convertYUV420_NV21to_Y_U_V_VR(byte[] bArr, int i, int i2, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        int i3 = i * i2;
        int i4 = ((i3 * 3) / 2) - i3;
        byte[] bArr5 = new byte[i3];
        int i5 = i3 / 4;
        byte[] bArr6 = new byte[i5];
        byte[] bArr7 = new byte[i5];
        System.arraycopy(bArr, 0, bArr5, 0, i3);
        for (int i6 = 0; i6 < i4; i6 += 2) {
            int i7 = i6 / 2;
            int i8 = i3 + i6;
            bArr7[i7] = bArr[i8];
            bArr6[i7] = bArr[i8 + 1];
        }
        for (int i9 = 0; i9 < i2; i9++) {
            int i10 = i9 * i;
            int i11 = i9 * 2 * i;
            System.arraycopy(bArr5, i10, bArr2, i11, i);
            System.arraycopy(bArr5, i10, bArr2, i11 + i, i);
        }
        for (int i12 = 0; i12 < i2 / 4; i12++) {
            int i13 = i12 * i;
            int i14 = i12 * 2 * i;
            System.arraycopy(bArr6, i13, bArr3, i14, i);
            int i15 = i14 + i;
            System.arraycopy(bArr6, i13, bArr3, i15, i);
            System.arraycopy(bArr7, i13, bArr4, i14, i);
            System.arraycopy(bArr7, i13, bArr4, i15, i);
        }
    }

    public static int[] convertYUV420_NV21toARGB8888(byte[] bArr, int i, int i2) {
        int i3 = i2 * i;
        int[] iArr = new int[i3];
        int i4 = 0;
        int i5 = 0;
        while (i4 < i3) {
            int i6 = i4 + 1;
            int i7 = i + i4;
            int i8 = i7 + 1;
            int i9 = i3 + i5;
            int i10 = (bArr[i9] & 255) - 128;
            int i11 = (bArr[i9 + 1] & 255) - 128;
            iArr[i4] = convertYUVtoARGB(bArr[i4] & 255, i11, i10);
            iArr[i6] = convertYUVtoARGB(bArr[i6] & 255, i11, i10);
            iArr[i7] = convertYUVtoARGB(bArr[i7] & 255, i11, i10);
            iArr[i8] = convertYUVtoARGB(bArr[i8] & 255, i11, i10);
            if (i4 != 0 && (i4 + 2) % i == 0) {
                i4 = i7;
            }
            i4 += 2;
            i5++;
        }
        return iArr;
    }

    public static int[] convertYUV420_I420toARGB8888(byte[] bArr, int i, int i2) {
        int i3 = i2 * i;
        byte[] bArr2 = new byte[i3];
        int i4 = i3 / 4;
        byte[] bArr3 = new byte[i4];
        byte[] bArr4 = new byte[i4];
        int[] iArr = new int[i3];
        int i5 = 0;
        System.arraycopy(bArr, 0, bArr2, 0, i3);
        int i6 = (((i3 * 3) / 2) - i3) / 2;
        System.arraycopy(bArr, i3, bArr3, 0, i6);
        System.arraycopy(bArr, i4 + i3, bArr4, 0, i6);
        int i7 = 0;
        while (i5 < i3) {
            iArr[i5] = convertYUVtoARGB(bArr2[i5], bArr3[i7], bArr4[i7]);
            int i8 = i5 + 1;
            iArr[i8] = convertYUVtoARGB(bArr2[i8], bArr3[i7], bArr4[i7]);
            int i9 = i + i5;
            iArr[i9] = convertYUVtoARGB(bArr2[i9], bArr3[i7], bArr4[i7]);
            iArr[i9 + 1] = convertYUVtoARGB(bArr2[i9], bArr3[i7], bArr4[i7]);
            if (i5 != 0 && (i5 + 2) % i == 0) {
                i5 = i9;
            }
            i5 += 2;
            i7++;
        }
        return iArr;
    }

    public static byte[] getYUVByBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] iArr = new int[width * height];
        bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
        return rgb2YCbCr420(iArr, width, height);
    }

    public static byte[] rgb2YCbCr420(int[] iArr, int i, int i2) {
        int i3 = i * i2;
        byte[] bArr = new byte[(i3 * 3) / 2];
        for (int i4 = 0; i4 < i2; i4++) {
            for (int i5 = 0; i5 < i; i5++) {
                int i6 = (i4 * i) + i5;
                int i7 = iArr[i6] & 16777215;
                int i8 = i7 & 255;
                int i9 = 255;
                int i10 = (i7 >> 8) & 255;
                int i11 = (i7 >> 16) & 255;
                int i12 = (((((i8 * 66) + (i10 * 129)) + (i11 * 25)) + 128) >> 8) + 16;
                int i13 = (((((i8 * (-38)) - (i10 * 74)) + (i11 * 112)) + 128) >> 8) + 128;
                int i14 = (((((i8 * 112) - (i10 * 94)) - (i11 * 18)) + 128) >> 8) + 128;
                int i15 = i12 >= 16 ? i12 > 255 ? 255 : i12 : 16;
                if (i13 < 0) {
                    i13 = 0;
                } else if (i13 > 255) {
                    i13 = 255;
                }
                if (i14 < 0) {
                    i9 = 0;
                } else if (i14 <= 255) {
                    i9 = i14;
                }
                bArr[i6] = (byte) i15;
                int i16 = ((i4 >> 1) * i) + i3 + (i5 & (-2));
                bArr[i16 + 0] = (byte) i9;
                bArr[i16 + 1] = (byte) i13;
            }
        }
        return bArr;
    }

    public static byte[] rgb2YCbCr4202(int[] iArr, int i, int i2) {
        int i3 = i * i2;
        byte[] bArr = new byte[(i3 * 3) / 2];
        for (int i4 = 0; i4 < i2; i4++) {
            for (int i5 = 0; i5 < i; i5++) {
                int i6 = (i4 * i) + i5;
                int i7 = iArr[i6] & 16777215;
                int i8 = i7 & 255;
                int i9 = 255;
                int i10 = (i7 >> 8) & 255;
                int i11 = (i7 >> 16) & 255;
                int i12 = (((((i8 * 66) + (i10 * 129)) + (i11 * 25)) + 128) >> 8) + 16;
                int i13 = (((((i8 * (-38)) - (i10 * 74)) + (i11 * 112)) + 128) >> 8) + 128;
                int i14 = (((((i8 * 112) - (i10 * 94)) - (i11 * 18)) + 128) >> 8) + 128;
                int i15 = i12 >= 16 ? i12 > 255 ? 255 : i12 : 16;
                if (i13 < 0) {
                    i13 = 0;
                } else if (i13 > 255) {
                    i13 = 255;
                }
                if (i14 < 0) {
                    i9 = 0;
                } else if (i14 <= 255) {
                    i9 = i14;
                }
                bArr[i6] = (byte) i15;
                int i16 = ((i4 >> 1) * i) + i3 + (i5 & (-2));
                bArr[i16 + 0] = (byte) i13;
                bArr[i16 + 1] = (byte) i9;
            }
        }
        return bArr;
    }

    public static Bitmap getBitmapFromResource(Resources resources, int i) {
        return BitmapFactory.decodeResource(resources, i);
    }
}
