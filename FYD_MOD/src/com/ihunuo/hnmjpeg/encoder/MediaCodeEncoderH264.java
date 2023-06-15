package com.ihunuo.hnmjpeg.encoder;

import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;
import com.ihunuo.hnmjpeg.opengl.OpenGLUtils;
import com.ihunuo.hnmjpeg.utils.SystemUtil;
import com.ihunuo.hnmjpeg.utils.UIUtils;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

/* loaded from: classes.dex */
public class MediaCodeEncoderH264 {
    private byte[] configbyte;
    private int framerate;
    private int height;
    private boolean isYUV;
    public MediaCodec mediaCodec;
    private int width;
    public boolean isRuning = false;
    private int yuvQueueSize = 800;
    public ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(this.yuvQueueSize);
    public ArrayBlockingQueue<Bitmap> bitmapsQueue = new ArrayBlockingQueue<Bitmap>(this.yuvQueueSize);
    private long TIMEOUT_USEC = 0;
    public String SaveH264Path = "";
    public String[] yuv420pPre = {"Letv X501"};
    public boolean isYUV420P = false;

    public MediaCodeEncoderH264(boolean z) {
        this.isYUV = false;
        this.isYUV = z;
    }

    public void initEncoder(int i, int i2, int i3, int i4, String str) {
        this.width = i;
        this.height = i2;
        this.framerate = i3;
        int i5 = 0;
        while (true) {
            String[] strArr = this.yuv420pPre;
            if (i5 >= strArr.length) {
                break;
            } else if (strArr[i5].equals(SystemUtil.getSystemModel())) {
                this.isYUV420P = true;
                break;
            } else {
                i5++;
            }
        }
        MediaFormat createVideoFormat = MediaFormat.createVideoFormat("video/avc", i, i2);
        createVideoFormat.setInteger("color-format", this.isYUV420P ? 19 : 21);
        createVideoFormat.setInteger("bitrate", i * i2 * 5);
        createVideoFormat.setInteger("frame-rate", i3);
        createVideoFormat.setInteger("i-frame-interval", 1);
        this.mediaCodec = MediaCodec.createEncoderByType("video/avc");
        this.mediaCodec.configure(createVideoFormat, (Surface) null, (MediaCrypto) null, 1);
        this.mediaCodec.start();
        setSavePath(str);
    }

    public void StartEncoderThread() {
        Log.d("ccc", "StartEncoderThread: " + this.SaveH264Path + "/abc.h264");
        UIUtils.deleteFile(this.SaveH264Path + "/abc.h264");
        new Thread(new Runnable() { // from class: com.ihunuo.hnmjpeg.encoder.MediaCodeEncoderH264.1
            @Override // java.lang.Runnable
            public void run() {
                MediaCodeEncoderH264.this.isRuning = true;
                long j = 0;
                while (true) {
                    if ((MediaCodeEncoderH264.this.YUVQueue.size() != 0) | MediaCodeEncoderH264.this.isRuning) {
                        byte[] poll = MediaCodeEncoderH264.this.YUVQueue.size() > 0 ? MediaCodeEncoderH264.this.YUVQueue.poll() : null;
                        if (poll != null) {
                            try {
                                ByteBuffer[] inputBuffers = MediaCodeEncoderH264.this.mediaCodec.getInputBuffers();
                                ByteBuffer[] outputBuffers = MediaCodeEncoderH264.this.mediaCodec.getOutputBuffers();
                                int dequeueInputBuffer = MediaCodeEncoderH264.this.mediaCodec.dequeueInputBuffer(-1L);
                                if (dequeueInputBuffer >= 0) {
                                    long computePresentationTime = MediaCodeEncoderH264.this.computePresentationTime(j);
                                    ByteBuffer byteBuffer = inputBuffers[dequeueInputBuffer];
                                    byteBuffer.clear();
                                    byteBuffer.put(poll);
                                    MediaCodeEncoderH264.this.mediaCodec.queueInputBuffer(dequeueInputBuffer, 0, poll.length, computePresentationTime, 0);
                                    j++;
                                    Log.d("ccc", "hn_socket_video: generateIndex=" + j);
                                }
                                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                                int dequeueOutputBuffer = MediaCodeEncoderH264.this.mediaCodec.dequeueOutputBuffer(bufferInfo, MediaCodeEncoderH264.this.TIMEOUT_USEC);
                                while (dequeueOutputBuffer >= 0) {
                                    ByteBuffer byteBuffer2 = outputBuffers[dequeueOutputBuffer];
                                    int i = bufferInfo.size;
                                    byte[] bArr = new byte[i];
                                    byteBuffer2.get(bArr);
                                    if (bufferInfo.flags == 2) {
                                        MediaCodeEncoderH264.this.configbyte = new byte[bufferInfo.size];
                                        MediaCodeEncoderH264.this.configbyte = bArr;
                                    } else if (bufferInfo.flags == 1) {
                                        int length = bufferInfo.size + MediaCodeEncoderH264.this.configbyte.length;
                                        byte[] bArr2 = new byte[length];
                                        System.arraycopy(MediaCodeEncoderH264.this.configbyte, 0, bArr2, 0, MediaCodeEncoderH264.this.configbyte.length);
                                        System.arraycopy(bArr, 0, bArr2, MediaCodeEncoderH264.this.configbyte.length, i);
                                        UIUtils.write(String.valueOf(MediaCodeEncoderH264.this.SaveH264Path) + "/abc.h264", bArr2, 0, length);
                                    } else {
                                        UIUtils.write(String.valueOf(MediaCodeEncoderH264.this.SaveH264Path) + "/abc.h264", bArr, 0, i);
                                    }
                                    MediaCodeEncoderH264.this.mediaCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                                    dequeueOutputBuffer = MediaCodeEncoderH264.this.mediaCodec.dequeueOutputBuffer(bufferInfo, MediaCodeEncoderH264.this.TIMEOUT_USEC);
                                }
                            } catch (Throwable th) {
                                th.printStackTrace();
                            }
                        } else {
                            try {
                                Thread.sleep(20L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        MediaCodeEncoderH264.this.YUVQueue.clear();
                        return;
                    }
                }
            }
        }).start();
        new Thread(new Runnable() { // from class: com.ihunuo.hnmjpeg.encoder.MediaCodeEncoderH264.2
            @Override // java.lang.Runnable
            public void run() {
                MediaCodeEncoderH264.this.isRuning = true;
                while (true) {
                    if (MediaCodeEncoderH264.this.isRuning || MediaCodeEncoderH264.this.YUVQueue.size() != 0) {
                        if (MediaCodeEncoderH264.this.isYUV) {
                            if (MediaCodeEncoderH264.this.YUVQueue.size() > 0) {
                                byte[] poll = MediaCodeEncoderH264.this.YUVQueue.poll();
                                byte[] bArr = new byte[((MediaCodeEncoderH264.this.width * MediaCodeEncoderH264.this.height) * 3) / 2];
                                if (!MediaCodeEncoderH264.this.isYUV420P) {
                                    MediaCodeEncoderH264 mediaCodeEncoderH264 = MediaCodeEncoderH264.this;
                                    mediaCodeEncoderH264.NV21ToNV12(poll, bArr, mediaCodeEncoderH264.width, MediaCodeEncoderH264.this.height);
                                }
                            }
                        } else if (MediaCodeEncoderH264.this.bitmapsQueue.size() > 0) {
                            Bitmap poll2 = MediaCodeEncoderH264.this.bitmapsQueue.poll();
                            if (poll2 != null && !poll2.isRecycled()) {
                                byte[] yUVByBitmap = OpenGLUtils.getYUVByBitmap(poll2);
                                byte[] bArr2 = new byte[((MediaCodeEncoderH264.this.width * MediaCodeEncoderH264.this.height) * 3) / 2];
                                if (!MediaCodeEncoderH264.this.isYUV420P) {
                                    if (MediaCodeEncoderH264.this.YUVQueue != null) {
                                        MediaCodeEncoderH264.this.YUVQueue.add(yUVByBitmap);
                                    }
                                } else {
                                    MediaCodeEncoderH264 mediaCodeEncoderH2642 = MediaCodeEncoderH264.this;
                                    mediaCodeEncoderH2642.yuv420pToNV21(yUVByBitmap, bArr2, mediaCodeEncoderH2642.width, MediaCodeEncoderH264.this.height);
                                    if (MediaCodeEncoderH264.this.YUVQueue != null) {
                                        MediaCodeEncoderH264.this.YUVQueue.add(bArr2);
                                    }
                                }
                            }
                            Log.d("ccc", "run: bitmapsQueue.size=" + MediaCodeEncoderH264.this.bitmapsQueue.size());
                        } else {
                            try {
                                Thread.sleep(50L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        return;
                    }
                }
            }
        }).start();
    }

    public long computePresentationTime(long j) {
        Log.d("ccc", "computePresentationTime: framerate=" + this.framerate);
        return ((1000000 * j) / this.framerate) + 0;
    }

    public void setSavePath(String str) {
        this.SaveH264Path = str;
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void release() {
        MediaCodec mediaCodec = this.mediaCodec;
        if (mediaCodec != null) {
            mediaCodec.stop();
            this.mediaCodec.release();
            this.mediaCodec = null;
        }
        this.isRuning = false;
    }

    public void NV21ToNV12(byte[] bArr, byte[] bArr2, int i, int i2) {
        int i3;
        if (bArr != null && bArr2 != null) {
            int i4 = i * i2;
            System.arraycopy(bArr, 0, bArr2, 0, i4);
            int i5 = 0;
            while (true) {
                i3 = i4 / 2;
                if (i5 >= i3) {
                    break;
                }
                int i6 = i4 + i5;
                bArr2[i6 - 1] = bArr[i6];
                i5 += 2;
            }
            for (int i7 = 0; i7 < i3; i7 += 2) {
                int i8 = i4 + i7;
                bArr2[i8] = bArr[i8 - 1];
            }
        }
    }

    public void yuv420pToNV21(byte[] bArr, byte[] bArr2, int i, int i2) {
        int i3;
        if (bArr != null && bArr2 != null) {
            int i4 = i * i2;
            int i5 = ((i4 * 3) / 2) - i4;
            System.arraycopy(bArr, 0, bArr2, 0, i4);
            int i6 = 0;
            while (true) {
                i3 = i4 / 2;
                if (i6 >= i3) {
                    break;
                }
                bArr2[((i6 / 2) + i4) - 1] = bArr[(i6 + i4) - 1];
                i6 += 2;
            }
            for (int i7 = 0; i7 < i3; i7 += 2) {
                bArr2[(i5 / 2) + i4 + (i7 / 2)] = bArr[i7 + i4];
            }
        }
    }
}
