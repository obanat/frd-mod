package com.ihunuo.hnmjpeg.opengl2.encodec;

import android.content.Context;
import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import com.ihunuo.hnmjpeg.opengl2.egl.EglHelper;
import com.ihunuo.hnmjpeg.opengl2.egl.HsnEGLSurfaceView;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import javax.microedition.khronos.egl.EGLContext;

/* loaded from: classes.dex */
public abstract class HsnBaseMediaEncoder {
    private static final int RECODE_FRAME_RATE = 25;
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    private MediaCodec.BufferInfo audioBufferinfo;
    private MediaCodec audioEncodec;
    private AudioEncodecThread audioEncodecThread;
    private boolean audioExit;
    private MediaFormat audioFormat;
    private final Context context;
    private EGLContext eglContext;
    private boolean encodecStart;
    private int height;
    private HsnEGLMediaThread hsnEGLMediaThread;
    private HsnEGLSurfaceView.HsnGLRender hsnGLRender;
    private MediaMuxer mediaMuxer;
    private OnMediaInfoListener onMediaInfoListener;
    private int sampleRate;
    private Surface surface;
    private MediaCodec.BufferInfo videoBufferinfo;
    private MediaCodec videoEncodec;
    private VideoEncodecThread videoEncodecThread;
    private boolean videoExit;
    private MediaFormat videoFormat;
    private int width;
    private long audioPts = 0;
    private int mRenderMode = 1;
    private boolean isRecodeAAC = false;
    private String savePath = Environment.getExternalStorageDirectory() + "/FYD-UAV/abc.h264";

    /* loaded from: classes.dex */
    public interface OnMediaInfoListener {
        void onMediaTime(int i);
    }

    public HsnBaseMediaEncoder(Context context) {
        this.context = context;
    }

    public void setRender(HsnEGLSurfaceView.HsnGLRender hsnGLRender) {
        this.hsnGLRender = hsnGLRender;
    }

    public void setmRenderMode(int i) {
        if (this.hsnGLRender == null) {
            throw new RuntimeException("must set render before");
        }
        this.mRenderMode = i;
    }

    public void setOnMediaInfoListener(OnMediaInfoListener onMediaInfoListener) {
        this.onMediaInfoListener = onMediaInfoListener;
    }

    public void initEncodec(EGLContext eGLContext, String str, int i, int i2, int i3, int i4, boolean z) {
        this.width = i;
        this.height = i2;
        this.eglContext = eGLContext;
        this.isRecodeAAC = z;
        this.savePath = str;
        initMediaEncodec(str, i, i2, i3, i4);
    }

    public void startRecord() {
        if (this.surface != null && this.eglContext != null) {
            this.audioPts = 0L;
            this.audioExit = false;
            this.videoExit = false;
            this.encodecStart = false;
            this.hsnEGLMediaThread = new HsnEGLMediaThread(new WeakReference(this));
            this.videoEncodecThread = new VideoEncodecThread(new WeakReference(this));
            this.hsnEGLMediaThread.isCreate = true;
            this.hsnEGLMediaThread.isChange = true;
            this.hsnEGLMediaThread.start();
            this.videoEncodecThread.start();
            if (this.isRecodeAAC) {
                AudioEncodecThread audioEncodecThread = new AudioEncodecThread(new WeakReference(this));
                this.audioEncodecThread = audioEncodecThread;
                audioEncodecThread.start();
            }
        }
    }

    public void stopRecord() {
        VideoEncodecThread videoEncodecThread;
        if (this.hsnEGLMediaThread != null && (videoEncodecThread = this.videoEncodecThread) != null) {
            videoEncodecThread.exit();
            this.hsnEGLMediaThread.onDestory();
            this.videoEncodecThread = null;
            this.hsnEGLMediaThread = null;
        }
        AudioEncodecThread audioEncodecThread = this.audioEncodecThread;
        if (audioEncodecThread != null) {
            audioEncodecThread.exit();
            this.audioEncodecThread = null;
        }
    }

    private void initMediaEncodec(String str, int i, int i2, int i3, int i4) {
        try {
            this.mediaMuxer = new MediaMuxer(str, 0);
            initVideoEncodec("video/avc", i, i2);
            if (this.isRecodeAAC) {
                initAudioEncodec("audio/mp4a-latm", i3, i4);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initVideoEncodec(String str, int i, int i2) {
        if ((i & 1) == 1) {
            i--;
        }
        if ((i2 & 1) == 1) {
            i2--;
        }
        this.videoBufferinfo = new MediaCodec.BufferInfo();
        MediaFormat createVideoFormat = MediaFormat.createVideoFormat(str, i, i2);
        this.videoFormat = createVideoFormat;
        createVideoFormat.setInteger("color-format", 2130708361);
        this.videoFormat.setInteger("bitrate", i * i2 * 4);
        this.videoFormat.setInteger("frame-rate", RECODE_FRAME_RATE);
        this.videoFormat.setInteger("i-frame-interval", 1);
        MediaCodec createEncoderByType = MediaCodec.createEncoderByType(str);
        this.videoEncodec = createEncoderByType;
        createEncoderByType.configure(this.videoFormat, (Surface) null, (MediaCrypto) null, 1);
        this.surface = this.videoEncodec.createInputSurface();
    }

    private void initAudioEncodec(String str, int i, int i2) {
        this.sampleRate = i;
        this.audioBufferinfo = new MediaCodec.BufferInfo();
        MediaFormat createAudioFormat = MediaFormat.createAudioFormat(str, i, i2);
        this.audioFormat = createAudioFormat;
        createAudioFormat.setInteger("bitrate", 16000);
        this.audioFormat.setInteger("aac-profile", 2);
        this.audioFormat.setInteger("max-input-size", 4096);
        MediaCodec createEncoderByType = MediaCodec.createEncoderByType(str);
        this.audioEncodec = createEncoderByType;
        createEncoderByType.configure(this.audioFormat, (Surface) null, (MediaCrypto) null, 1);
    }

    public void putPCMData(byte[] bArr, int i) {
        int dequeueInputBuffer;
        AudioEncodecThread audioEncodecThread = this.audioEncodecThread;
        if (audioEncodecThread != null && !audioEncodecThread.isExit && bArr != null && i > 0 && (dequeueInputBuffer = this.audioEncodec.dequeueInputBuffer(0L)) >= 0) {
            ByteBuffer byteBuffer = this.audioEncodec.getInputBuffers()[dequeueInputBuffer];
            byteBuffer.clear();
            byteBuffer.put(bArr);
            this.audioEncodec.queueInputBuffer(dequeueInputBuffer, 0, i, getAudioPts(i, this.sampleRate), 0);
        }
    }

    public void requestRender() {
        HsnEGLMediaThread hsnEGLMediaThread = this.hsnEGLMediaThread;
        if (hsnEGLMediaThread != null) {
            hsnEGLMediaThread.requestRender();
        }
    }

    /* loaded from: classes.dex */
    public static class HsnEGLMediaThread extends Thread {
        private EglHelper eglHelper;
        private WeakReference<HsnBaseMediaEncoder> encoder;
        private Object object;
        private boolean isExit = false;
        private boolean isCreate = false;
        private boolean isChange = false;
        private boolean isStart = false;

        public HsnEGLMediaThread(WeakReference<HsnBaseMediaEncoder> weakReference) {
            this.encoder = weakReference;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            this.isExit = false;
            this.isStart = false;
            this.object = new Object();
            EglHelper eglHelper = new EglHelper();
            this.eglHelper = eglHelper;
            eglHelper.initEgl(this.encoder.get().surface, this.encoder.get().eglContext);
            while (!this.isExit) {
                if (this.isStart) {
                    if (this.encoder.get().mRenderMode != 0) {
                        if (this.encoder.get().mRenderMode == 1) {
                            try {
                                Thread.sleep(40L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            throw new RuntimeException("mRenderMode is wrong value");
                        }
                    } else {
                        synchronized (this.object) {
                            try {
                                this.object.wait();
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }
                if (this.encoder.get() != null) {
                    onCreate();
                    onChange(this.encoder.get().width, this.encoder.get().height);
                    onDraw();
                }
                this.isStart = true;
            }
            release();
        }

        private void onCreate() {
            if (this.isCreate && this.encoder.get().hsnGLRender != null) {
                this.isCreate = false;
                this.encoder.get().hsnGLRender.onSurfaceCreated();
            }
        }

        private void onChange(int i, int i2) {
            if (this.isChange && this.encoder.get().hsnGLRender != null) {
                this.isChange = false;
                this.encoder.get().hsnGLRender.onSurfaceChanged(i, i2);
            }
        }

        private void onDraw() {
            if (this.encoder.get().hsnGLRender != null && this.eglHelper != null) {
                this.encoder.get().hsnGLRender.onDrawFrame();
                if (!this.isStart) {
                    this.encoder.get().hsnGLRender.onDrawFrame();
                }
                this.eglHelper.swapBuffers();
            }
        }

        public void requestRender() {
            Object obj = this.object;
            if (obj != null) {
                synchronized (obj) {
                    this.object.notifyAll();
                }
            }
        }

        public void onDestory() {
            this.isExit = true;
            requestRender();
        }

        public void release() {
            EglHelper eglHelper = this.eglHelper;
            if (eglHelper != null) {
                eglHelper.destoryEgl();
                this.eglHelper = null;
                this.object = null;
                this.encoder = null;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class VideoEncodecThread extends Thread {
        private WeakReference<HsnBaseMediaEncoder> encoder;
        private boolean isExit;
        private MediaMuxer mediaMuxer;
        private long pts;
        private MediaCodec.BufferInfo videoBufferinfo;
        private MediaCodec videoEncodec;
        private int videoTrackIndex;

        public VideoEncodecThread(WeakReference<HsnBaseMediaEncoder> weakReference) {
            this.videoTrackIndex = -1;
            this.encoder = weakReference;
            this.videoEncodec = weakReference.get().videoEncodec;
            this.videoBufferinfo = weakReference.get().videoBufferinfo;
            this.mediaMuxer = weakReference.get().mediaMuxer;
            this.videoTrackIndex = -1;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            this.pts = 0L;
            this.videoTrackIndex = -1;
            this.isExit = false;
            this.videoEncodec.start();
            while (!this.isExit) {
                int dequeueOutputBuffer = this.videoEncodec.dequeueOutputBuffer(this.videoBufferinfo, 0L);
                if (dequeueOutputBuffer == -2) {
                    this.videoTrackIndex = this.mediaMuxer.addTrack(this.videoEncodec.getOutputFormat());
                    if (this.encoder.get().audioEncodecThread == null) {
                        this.mediaMuxer.start();
                        this.encoder.get().encodecStart = true;
                    } else if (this.encoder.get().audioEncodecThread.audioTrackIndex != -1) {
                        this.mediaMuxer.start();
                        this.encoder.get().encodecStart = true;
                    }
                } else {
                    while (dequeueOutputBuffer >= 0 && this.encoder.get() != null) {
                        if (this.encoder.get().encodecStart) {
                            ByteBuffer byteBuffer = this.videoEncodec.getOutputBuffers()[dequeueOutputBuffer];
                            byteBuffer.position(this.videoBufferinfo.offset);
                            byteBuffer.limit(this.videoBufferinfo.offset + this.videoBufferinfo.size);
                            if (this.pts == 0) {
                                this.pts = this.videoBufferinfo.presentationTimeUs;
                            }
                            this.videoBufferinfo.presentationTimeUs -= this.pts;
                            this.mediaMuxer.writeSampleData(this.videoTrackIndex, byteBuffer, this.videoBufferinfo);
                            byteBuffer.get(new byte[this.videoBufferinfo.size], 0, this.videoBufferinfo.size);
                            if (this.encoder.get().onMediaInfoListener != null) {
                                this.encoder.get().onMediaInfoListener.onMediaTime((int) (this.videoBufferinfo.presentationTimeUs / 1000000));
                            }
                        }
                        this.videoEncodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                        dequeueOutputBuffer = this.videoEncodec.dequeueOutputBuffer(this.videoBufferinfo, 0L);
                    }
                }
            }
            this.videoEncodec.stop();
            this.videoEncodec.release();
            this.videoEncodec = null;
            this.encoder.get().videoExit = true;
            if (this.encoder.get().audioExit) {
                if (this.encoder.get().encodecStart) {
                    this.mediaMuxer.stop();
                    this.mediaMuxer.release();
                    this.mediaMuxer = null;
                    Intent intent = new Intent("com.ihunuo.record");
                    intent.putExtra("savePath", this.encoder.get().savePath);
                    this.encoder.get().context.sendBroadcast(intent);
                }
            } else if (this.encoder.get().encodecStart) {
                this.mediaMuxer.stop();
                this.mediaMuxer.release();
                this.mediaMuxer = null;
                Intent intent2 = new Intent("com.ihunuo.record");
                intent2.putExtra("savePath", this.encoder.get().savePath);
                this.encoder.get().context.sendBroadcast(intent2);
            }
            Log.d("ywl5320", "褰曞埗瀹屾垚");
        }

        public void exit() {
            this.isExit = true;
        }
    }

    /* loaded from: classes.dex */
    public static class AudioEncodecThread extends Thread {
        private MediaCodec audioEncodec;
        private int audioTrackIndex;
        private MediaCodec.BufferInfo bufferInfo;
        private WeakReference<HsnBaseMediaEncoder> encoder;
        private boolean isExit;
        private MediaMuxer mediaMuxer;
        long pts;

        public AudioEncodecThread(WeakReference<HsnBaseMediaEncoder> weakReference) {
            this.audioTrackIndex = -1;
            this.encoder = weakReference;
            this.audioEncodec = weakReference.get().audioEncodec;
            this.bufferInfo = weakReference.get().audioBufferinfo;
            this.mediaMuxer = weakReference.get().mediaMuxer;
            this.audioTrackIndex = -1;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            this.pts = 0L;
            this.isExit = false;
            this.audioEncodec.start();
            while (!this.isExit) {
                int dequeueOutputBuffer = this.audioEncodec.dequeueOutputBuffer(this.bufferInfo, 0L);
                if (dequeueOutputBuffer == -2) {
                    MediaMuxer mediaMuxer = this.mediaMuxer;
                    if (mediaMuxer != null) {
                        this.audioTrackIndex = mediaMuxer.addTrack(this.audioEncodec.getOutputFormat());
                        if (this.encoder.get().videoEncodecThread.videoTrackIndex != -1) {
                            this.mediaMuxer.start();
                            this.encoder.get().encodecStart = true;
                        }
                    }
                } else {
                    while (dequeueOutputBuffer >= 0) {
                        if (this.encoder.get().encodecStart) {
                            ByteBuffer byteBuffer = this.audioEncodec.getOutputBuffers()[dequeueOutputBuffer];
                            byteBuffer.position(this.bufferInfo.offset);
                            byteBuffer.limit(this.bufferInfo.offset + this.bufferInfo.size);
                            if (this.pts == 0) {
                                this.pts = this.bufferInfo.presentationTimeUs;
                            }
                            this.bufferInfo.presentationTimeUs -= this.pts;
                            this.mediaMuxer.writeSampleData(this.audioTrackIndex, byteBuffer, this.bufferInfo);
                        }
                        this.audioEncodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                        dequeueOutputBuffer = this.audioEncodec.dequeueOutputBuffer(this.bufferInfo, 0L);
                    }
                }
            }
            this.audioEncodec.stop();
            this.audioEncodec.release();
            this.audioEncodec = null;
            this.encoder.get().audioExit = true;
            if (this.encoder.get().videoExit) {
                this.mediaMuxer.stop();
                this.mediaMuxer.release();
                this.mediaMuxer = null;
            }
        }

        public void exit() {
            this.isExit = true;
        }
    }

    private long getAudioPts(int i, int i2) {
        long j = this.audioPts + ((long) (((i * 1.0d) / ((i2 * 2) * 2)) * 1000000.0d));
        this.audioPts = j;
        return j;
    }
}
