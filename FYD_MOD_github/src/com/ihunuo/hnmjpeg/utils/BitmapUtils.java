package com.ihunuo.hnmjpeg.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;

/* loaded from: classes.dex */
public class BitmapUtils {

    /* loaded from: classes.dex */
    public interface IBitMapReaderCallBack {
        Bitmap getBitMap(BitmapFactory.Options options);
    }

    public static Bitmap decodeBitmapFromPathUri(Uri uri, Activity activity) {
        return decodeBitmapFromPathUri(uri, activity, true);
    }

    public static Bitmap decodeBitmapFromPathUri(final Uri uri, final Activity activity, boolean z) {
        return compressImage(800.0f, 0, true, z, new IBitMapReaderCallBack() { // from class: com.ihunuo.hnmjpeg.utils.BitmapUtils.1
            @Override // com.ihunuo.hnmjpeg.utils.BitmapUtils.IBitMapReaderCallBack
            public Bitmap getBitMap(BitmapFactory.Options options) {
                try {
                    return BitmapFactory.decodeFileDescriptor(activity.getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor(), null, options);
                } catch (Throwable th) {
                    Log.e("exception", "BitmapUtil.decodeBitmapFromPathUri(Uri pathUri, Activity activity) Exception " + th.getMessage());
                    return null;
                }
            }
        });
    }

    public static Bitmap createImageThumbnailScale(String str, int i) {
        return createImageThumbnailScale(str, i, true);
    }

    public static Bitmap createImageThumbnailScale(final String str, int i, boolean z) {
        return compressImage(i, 0, true, z, new IBitMapReaderCallBack() { // from class: com.ihunuo.hnmjpeg.utils.BitmapUtils.2
            @Override // com.ihunuo.hnmjpeg.utils.BitmapUtils.IBitMapReaderCallBack
            public Bitmap getBitMap(BitmapFactory.Options options) {
                return BitmapFactory.decodeFile(str, options);
            }
        });
    }

    public static Bitmap createImageThumbnail(String str, int i) {
        return createImageThumbnail(str, i, true);
    }

    public static Bitmap createImageThumbnail(final String str, int i, boolean z) {
        return compressImage(i, 0, false, z, new IBitMapReaderCallBack() { // from class: com.ihunuo.hnmjpeg.utils.BitmapUtils.3
            @Override // com.ihunuo.hnmjpeg.utils.BitmapUtils.IBitMapReaderCallBack
            public Bitmap getBitMap(BitmapFactory.Options options) {
                return BitmapFactory.decodeFile(str, options);
            }
        });
    }

    private static int calculateInSampleSize(float f, float f2, float f3, float f4) {
        int round;
        int i = 1;
        if (f4 != 0.0f && f3 != 0.0f) {
            if ((f2 > f4 || f > f3) && (round = Math.round(f2 / f4)) < (i = Math.round(f / f3))) {
                i = round;
            }
            while ((f * f2) / (i * i) > f3 * f4 * 2.0f) {
                i++;
            }
        }
        return i;
    }

    private static Matrix getMatrix(int i) {
        Matrix matrix = new Matrix();
        if (i == 90 || i == 180 || i == 270) {
            matrix.postRotate(i);
        }
        return matrix;
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0039  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0187 A[Catch: Throwable -> 0x01c2, TRY_LEAVE, TryCatch #1 {Throwable -> 0x01c2, blocks: (B:7:0x001a, B:16:0x003d, B:17:0x0043, B:20:0x007c, B:22:0x0082, B:29:0x0096, B:31:0x009e, B:32:0x00a0, B:37:0x00c2, B:38:0x00da, B:40:0x012c, B:42:0x0136, B:50:0x0177, B:43:0x0145, B:45:0x0163, B:47:0x0169, B:49:0x0173, B:28:0x008f, B:51:0x0187), top: B:59:0x001a }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static android.graphics.Bitmap compressImage(float r33, int r34, boolean r35, boolean r36, com.ihunuo.hnmjpeg.utils.BitmapUtils.IBitMapReaderCallBack r37) {
        /*
            Method dump skipped, instructions count: 483
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ihunuo.hnmjpeg.utils.BitmapUtils.compressImage(float, int, boolean, boolean, com.ihunuo.hnmjpeg.utils.BitmapUtils$IBitMapReaderCallBack):android.graphics.Bitmap");
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            return null;
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(i / width, i2 / height);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
            return createBitmap;
        }
        return createBitmap;
    }

    private Bitmap scaleBitmap(Bitmap bitmap, float f) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(f, f);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        if (!createBitmap.equals(bitmap)) {
            bitmap.recycle();
            return createBitmap;
        }
        return createBitmap;
    }

    public static Bitmap cropBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        return Bitmap.createBitmap(bitmap, (width - height) / 2, 0, height, height, (Matrix) null, false);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, float f) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(f);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        createBitmap.equals(bitmap);
        return createBitmap;
    }

    public Bitmap skewBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postSkew(-0.6f, -0.3f);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        if (!createBitmap.equals(bitmap)) {
            bitmap.recycle();
            return createBitmap;
        }
        return createBitmap;
    }

    public static Bitmap mergeBitmap(Bitmap bitmap, Bitmap bitmap2) {
        if (bitmap == null || bitmap.isRecycled() || bitmap2 == null || bitmap2.isRecycled()) {
            return null;
        }
        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        new Canvas(copy).drawBitmap(bitmap2, new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight()), new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), (Paint) null);
        return copy;
    }

    public static Bitmap mergeBitmap_LR(Bitmap bitmap, Bitmap bitmap2, boolean z) {
        int height;
        if (bitmap == null || bitmap.isRecycled() || bitmap2 == null || bitmap2.isRecycled()) {
            return null;
        }
        if (z) {
            height = bitmap.getHeight() > bitmap2.getHeight() ? bitmap.getHeight() : bitmap2.getHeight();
        } else {
            height = bitmap.getHeight() < bitmap2.getHeight() ? bitmap.getHeight() : bitmap2.getHeight();
        }
        if (bitmap.getHeight() != height) {
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (((bitmap.getWidth() * 1.0f) / bitmap.getHeight()) * height), height, false);
        } else if (bitmap2.getHeight() != height) {
            bitmap2 = Bitmap.createScaledBitmap(bitmap2, (int) (((bitmap2.getWidth() * 1.0f) / bitmap2.getHeight()) * height), height, false);
        }
        int width = bitmap.getWidth() + bitmap2.getWidth();
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect rect2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        Rect rect3 = new Rect(bitmap.getWidth(), 0, width, height);
        canvas.drawBitmap(bitmap, rect, rect, (Paint) null);
        canvas.drawBitmap(bitmap2, rect2, rect3, (Paint) null);
        return createBitmap;
    }

    public static Bitmap mergeBitmap_TB(Bitmap bitmap, Bitmap bitmap2, boolean z) {
        int width;
        if (bitmap == null || bitmap.isRecycled() || bitmap2 == null || bitmap2.isRecycled()) {
            return null;
        }
        if (z) {
            width = bitmap.getWidth() > bitmap2.getWidth() ? bitmap.getWidth() : bitmap2.getWidth();
        } else {
            width = bitmap.getWidth() < bitmap2.getWidth() ? bitmap.getWidth() : bitmap2.getWidth();
        }
        if (bitmap.getWidth() != width) {
            bitmap = Bitmap.createScaledBitmap(bitmap, width, (int) (((bitmap.getHeight() * 1.0f) / bitmap.getWidth()) * width), false);
        } else if (bitmap2.getWidth() != width) {
            bitmap2 = Bitmap.createScaledBitmap(bitmap2, width, (int) (((bitmap2.getHeight() * 1.0f) / bitmap2.getWidth()) * width), false);
        }
        int height = bitmap.getHeight() + bitmap2.getHeight();
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect rect2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        Rect rect3 = new Rect(0, bitmap.getHeight(), width, height);
        canvas.drawBitmap(bitmap, rect, rect, (Paint) null);
        canvas.drawBitmap(bitmap2, rect2, rect3, (Paint) null);
        return createBitmap;
    }
}
