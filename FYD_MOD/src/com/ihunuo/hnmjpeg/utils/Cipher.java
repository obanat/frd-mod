package com.ihunuo.hnmjpeg.utils;

/* loaded from: classes.dex */
public class Cipher {
    byte key1 = -118;
    byte key2 = 60;

    public byte[] Encrypt(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length];
        int length = bArr.length;
        int i = length - 1;
        bArr2[i] = bArr[i];
        for (int i2 = length - 2; i2 >= 0; i2--) {
            int i3 = i2 + 1;
            bArr2[i2] = (byte) ((((bArr[i2] ^ bArr2[i3]) + bArr2[i3]) ^ this.key1) + this.key2);
        }
        return bArr2;
    }

    public byte[] Decrypt(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length];
        int length = bArr.length;
        int i = length - 1;
        bArr2[i] = bArr[i];
        for (int i2 = length - 2; i2 >= 0; i2--) {
            int i3 = i2 + 1;
            bArr2[i2] = (byte) ((((bArr[i2] - this.key2) ^ this.key1) - bArr[i3]) ^ bArr[i3]);
        }
        return bArr2;
    }
}
