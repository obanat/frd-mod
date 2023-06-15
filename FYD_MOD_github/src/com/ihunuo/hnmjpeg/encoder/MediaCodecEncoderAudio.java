package com.ihunuo.hnmjpeg.encoder;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.view.Surface;
import com.ihunuo.hnmjpeg.utils.UIUtils;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

/* loaded from: classes.dex */
public class MediaCodecEncoderAudio {
    private String SaveH264Path;
    public ConcurrentLinkedQueue aacQueue = new ConcurrentLinkedQueue();
    public boolean isRuning;
    private MediaCodec mediaCodec;

    public void initEncoder(String str) {
        this.mediaCodec = MediaCodec.createEncoderByType("audio/mp4a-latm");
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString("mime", "audio/mp4a-latm");
        mediaFormat.setInteger("max-input-size", 10240);
        mediaFormat.setInteger("channel-count", 1);
        mediaFormat.setInteger("sample-rate", 8000);
        mediaFormat.setInteger("bitrate", 16000);
        mediaFormat.setInteger("aac-profile", 2);
        this.mediaCodec.configure(mediaFormat, (Surface) null, (MediaCrypto) null, 1);
        this.mediaCodec.start();
        setSavePath(str);
        UIUtils.deleteFile(String.valueOf(this.SaveH264Path) + "/123.aac");
    }

    public void StartEncoderThread() {
        new Thread(new Runnable() { // from class: com.ihunuo.hnmjpeg.encoder.MediaCodecEncoderAudio.1
            @Override // java.lang.Runnable
            public void run() {
                MediaCodecEncoderAudio.this.isRuning = true;
                while (true) {
                    if (!MediaCodecEncoderAudio.this.isRuning && MediaCodecEncoderAudio.this.aacQueue.size() == 0) {
                        return;
                    }
                    if (MediaCodecEncoderAudio.this.aacQueue.size() != 0) {
                        ByteBuffer[] inputBuffers = MediaCodecEncoderAudio.this.mediaCodec.getInputBuffers();
                        ByteBuffer[] outputBuffers = MediaCodecEncoderAudio.this.mediaCodec.getOutputBuffers();
                        int dequeueInputBuffer = MediaCodecEncoderAudio.this.mediaCodec.dequeueInputBuffer(-1L);
                        if (dequeueInputBuffer >= 0) {
                            ByteBuffer byteBuffer = inputBuffers[dequeueInputBuffer];
                            byteBuffer.clear();
                            byte[] bArr = (byte[]) MediaCodecEncoderAudio.this.aacQueue.poll();
                            byteBuffer.put(bArr);
                            MediaCodecEncoderAudio.this.mediaCodec.queueInputBuffer(dequeueInputBuffer, 0, bArr.length, 0L, 0);
                        }
                        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                        int dequeueOutputBuffer = MediaCodecEncoderAudio.this.mediaCodec.dequeueOutputBuffer(bufferInfo, 0L);
                        while (dequeueOutputBuffer >= 0) {
                            ByteBuffer byteBuffer2 = outputBuffers[dequeueOutputBuffer];
                            byteBuffer2.position(bufferInfo.offset);
                            byte[] bArr2 = new byte[bufferInfo.size];
                            byteBuffer2.get(bArr2);
                            byte[] bArr3 = new byte[bufferInfo.size + 7];
                            System.arraycopy(bArr2, 0, bArr3, 7, bufferInfo.size);
                            MediaCodecEncoderAudio.this.addADTStoPacket(bArr3, bufferInfo.size + 7);
                            MediaCodecEncoderAudio.this.mediaCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                            dequeueOutputBuffer = MediaCodecEncoderAudio.this.mediaCodec.dequeueOutputBuffer(bufferInfo, 0L);
                        }
                    } else {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public void addADTStoPacket(byte[] bArr, int i) {
        bArr[0] = -1;
        bArr[1] = -7;
        bArr[2] = 108;
        bArr[3] = (byte) ((i >> 11) + 64);
        bArr[4] = (byte) ((i & 2047) >> 3);
        bArr[5] = (byte) (((i & 7) << 5) + 31);
        bArr[6] = -4;
        UIUtils.write(String.valueOf(this.SaveH264Path) + "/123.aac", bArr, 0, bArr.length);
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
}
