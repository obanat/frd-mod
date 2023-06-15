package com.ihunuo.hnmjpeg.Listener;

import android.graphics.Bitmap;

/* loaded from: classes.dex */
public interface HNMjpegListener {
    void createHNTCPSusscced(int i);

    void decodeYuvSusscced(byte[] bArr, int i, int i2);

    void revDataHNSocketTCP(int i);

    void revDataHNSocketUDP(byte[] bArr, int i);

    void takePhotoSuccssed(Bitmap bitmap);

    void videoDecodeSuccssed();
}
