package com.ihunuo.hnmjpeg.utils;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class UIUtils {
    static final boolean $assertionsDisabled = false;
    private static MediaMetadataRetriever media;

    public static byte[] latlngtobyte(double d) {
        int i = (int) (1.0E7d * d);
        return new byte[]{(byte) (i >> 24), (byte) (i >> 16), (byte) (i >> 8), (byte) (i & 255)};
    }

    public static String byte2hex(byte[] bArr) {
        String str = "";
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            str = String.valueOf(str) + " " + hexString;
        }
        return str;
    }

    public static String byte2bits(byte b) {
        String binaryString = Integer.toBinaryString(b | 256);
        int length = binaryString.length();
        return binaryString.substring(length - 8, length);
    }

    public static boolean isServiceRunning(Context context, String str) {
        if (!"".equals(str) && str != null) {
            ArrayList arrayList = (ArrayList) ((ActivityManager) context.getSystemService("activity")).getRunningServices(30);
            for (int i = 0; i < arrayList.size(); i++) {
                if (((ActivityManager.RunningServiceInfo) arrayList.get(i)).service.getClassName().toString().equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        boolean z = false;
        boolean z2 = identifier > 0 ? resources.getBoolean(identifier) : false;
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            String str = (String) cls.getMethod("get", String.class).invoke(cls, "qemu.hw.mainkeys");
            if (!"1".equals(str)) {
                z = "0".equals(str) ? true : z2;
            }
            return z;
        } catch (Exception e) {
            return z2;
        }
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources;
        int identifier;
        if (!checkDeviceHasNavigationBar(context) || (identifier = (resources = context.getResources()).getIdentifier("navigation_bar_height", "dimen", "android")) <= 0) {
            return 0;
        }
        return resources.getDimensionPixelSize(identifier);
    }

    public static int getDpi(Context context, int i) {
        int i2 = 0;
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        try {
            Class.forName("android.view.Display").getMethod("getRealMetrics", DisplayMetrics.class).invoke(defaultDisplay, displayMetrics);
            if (i == 0) {
                i2 = displayMetrics.widthPixels;
            } else if (i == 1) {
                i2 = displayMetrics.heightPixels;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i2;
    }

    public static boolean memcmp(byte[] bArr, byte[] bArr2, int i) {
        if (bArr == null && bArr2 == null) {
            return true;
        }
        if (bArr == null || bArr2 == null) {
            return false;
        }
        if (bArr != bArr2) {
            for (int i2 = 0; i2 < bArr.length && i2 < bArr2.length && i2 < i; i2++) {
                if (bArr[i2] != bArr2[i2]) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public static void showNormalDialog(int i, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (i == 0) {
            builder.setMessage("");
        }
        builder.setPositiveButton("绾璡ue1bc鐣�", new DialogInterface.OnClickListener() { // from class: com.ihunuo.hnmjpeg.utils.UIUtils.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
            }
        });
        builder.setNegativeButton("閸忔娊妫�", new DialogInterface.OnClickListener() { // from class: com.ihunuo.hnmjpeg.utils.UIUtils.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
            }
        });
        builder.show();
    }

    public static Bitmap filetobitmap(String str) {
        if (str.equals("") || str == null) {
            return null;
        }
        try {
            if (new File(str).exists()) {
                return BitmapFactory.decodeFile(str);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean deleteFile(String str) {
        File file = new File(str);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                PrintStream printStream = System.out;
                printStream.println("閸掔娀娅庨崡鏇氶嚋閺傚洣娆�" + str);
                return true;
            }
            PrintStream printStream2 = System.out;
            printStream2.println("閸掔娀娅庨崡鏇氶嚋閺傚洣娆�" + str + "婢惰精瑙﹂敍锟�");
            return false;
        }
        PrintStream printStream3 = System.out;
        printStream3.println(str + "娑撳秴鐡ㄩ崷\ue7d2绱�");
        return false;
    }

    public static void startAlarm(Context context) {
        Uri defaultUri = RingtoneManager.getDefaultUri(2);
        if (defaultUri != null) {
            RingtoneManager.getRingtone(context, defaultUri).play();
        }
    }

    public static void shootSound(Context context, int i) {
        ((AudioManager) context.getSystemService("audio")).getStreamVolume(5);
    }

    public static ContentValues getVideoContentValues(Context context, File file, long j) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", file.getName());
        contentValues.put("_display_name", file.getName());
        contentValues.put("mime_type", "video/mp4");
        contentValues.put("datetaken", Long.valueOf(j));
        contentValues.put("date_modified", Long.valueOf(j));
        contentValues.put("date_added", Long.valueOf(j));
        contentValues.put("_data", file.getAbsolutePath());
        contentValues.put("_size", Long.valueOf(file.length()));
        return contentValues;
    }

    public static void insertIntoMediaStore(Context context, boolean z, File file, long j) {
        Uri uri;
        ContentResolver contentResolver = context.getContentResolver();
        if (j == 0) {
            j = System.currentTimeMillis();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", file.getName());
        contentValues.put("_display_name", file.getName());
        contentValues.put("datetaken", Long.valueOf(j));
        contentValues.put("date_modified", Long.valueOf(System.currentTimeMillis()));
        contentValues.put("date_added", Long.valueOf(System.currentTimeMillis()));
        if (!z) {
            contentValues.put("orientation", (Integer) 0);
        }
        contentValues.put("_data", file.getAbsolutePath());
        contentValues.put("_size", Long.valueOf(file.length()));
        contentValues.put("mime_type", z ? "video/3gp" : "image/jpeg");
        if (z) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        contentResolver.insert(uri, contentValues);
    }

    public static void copyFile(String str, String str2, Context context) {
        try {
            if (new File(str).exists()) {
                FileInputStream fileInputStream = new FileInputStream(str);
                FileOutputStream fileOutputStream = new FileOutputStream(str2);
                byte[] bArr = new byte[1444];
                int i = 0;
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read != -1) {
                        i += read;
                        System.out.println(i);
                        fileOutputStream.write(bArr, 0, read);
                    } else {
                        fileInputStream.close();
                        scanIntoMediaStore(context, new File(str2));
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("婢跺秴鍩楅崡鏇氶嚋閺傚洣娆㈤幙宥勭稊閸戞椽鏁�");
            e.printStackTrace();
        }
    }

    public static void scanIntoMediaStore(Context context, File file) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

    public static Bitmap getVideoBitmap(String str) {
        if (media == null) {
            media = new MediaMetadataRetriever();
        }
        media.setDataSource(str);
        return media.getFrameAtTime();
    }

    public static void write(String str, byte[] bArr, int i, int i2) {
        try {
            if (Environment.getExternalStorageState().equals("mounted")) {
                File file = new File(str);
                Log.d("ccc", "write: " + file.getPath());
                if (!file.exists()) {
                    file.createNewFile();
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(file.length());
                randomAccessFile.write(bArr, i, i2);
                randomAccessFile.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean bitmaptofile(Bitmap bitmap, String str, String str2) {
        Log.d("bbb", "bitmaptofile path:" + str2 + " name:" + str);
        File file = new File(str2);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(file, String.valueOf(str) + ".jpeg");
        if (file2.exists()) {
            file2.delete();
        }
        boolean z = false;
        try {
            file2.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            z = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return z;
        } catch (IOException e) {
            e.printStackTrace();
            return z;
        }
    }

    public static String getConnectWifiSsid2(Context context) {
        String extraInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1).getExtraInfo();
        return extraInfo != null ? extraInfo.substring(1, extraInfo.length() - 1) : extraInfo;
    }

    public static String getConnectWifiSsid3(Context context) {
        if (Build.VERSION.SDK_INT == 27) {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getApplicationContext().getSystemService("connectivity")).getActiveNetworkInfo();
            return (activeNetworkInfo == null || !activeNetworkInfo.isConnected() || activeNetworkInfo.getExtraInfo() == null) ? "unknown id" : activeNetworkInfo.getExtraInfo().replace("\"", "");
        }
        WifiInfo connectionInfo = ((WifiManager) context.getApplicationContext().getSystemService("wifi")).getConnectionInfo();
        if (Build.VERSION.SDK_INT < 19) {
            return connectionInfo.getSSID();
        }
        return connectionInfo.getSSID().replace("\"", "");
    }
}
