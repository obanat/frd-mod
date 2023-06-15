package com.ihunuo.hnmjpeg.socket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.ihunuo.hnmjpeg.Listener.HNMjpegListener;
import com.ihunuo.hnmjpeg.opengl2.encodec.HsnBaseMediaEncoder;
import com.ihunuo.hnmjpeg.opengl2.encodec.HsnMediaEncodec;
import com.ihunuo.hnmjpeg.opengl2.imgvideo.HsnImgVideoView;
import com.ihunuo.hnmjpeg.utils.UIUtils;
import com.obana.fydmod.CommUtil;
import com.obana.fydmod.LogUtils;
import com.obana.fydmod.R;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.microedition.khronos.egl.EGLContext;

/* loaded from: classes.dex */
public class HNSocketMjpegUDP {
    private static HNSocketMjpegUDP hnSocketMjpegUDP;
    public Bitmap bitmap;
    public Context context;
    public int height;
    public HNMjpegListener hnMjpegListener;
    public HsnImgVideoView hsnGLSurfaceView;
    private HsnMediaEncodec hsnMediaEncodec;
    private ImageView ivphoto;
    private WifiManager.MulticastLock lock;
    private WifiManager manager;
    private String name;
    public Bitmap resIdBitmap;
    private String savePath;
    private SendUDPThread sendUDPThread;
    private TimerTask task;
    private Thread thread;
    private Timer timer;
    private TextView tv_rev_display;
    private TextView tv_rev_state;
    public int width;
    private DatagramSocket socket = null;
    private boolean isplay = true;
    private boolean isRev = true;
    private int delay_time = 200;
    private Queue videoQueue = null;
    private boolean is_video_flag = false;
    private BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
    private boolean isFirst = true;
    public boolean isVoice = false;
    public boolean isVoicePlay = false;
    public boolean aiThreadFlag = false;
    public int isMjpeg = 0;
    public int filter = 0;
    private byte[] send_udp_state = new byte[10];
    private int decode_time = 100;
    private int fps = 0;
    private int fps1 = 0;
    private int cur = 0;
    private int fps_sec_flag = 0;
    private int frame_number = 0;
    private boolean is_frame_time_flag = false;
    private Handler handler = new Handler() { // from class: com.ihunuo.hnmjpeg.socket.HNSocketMjpegUDP.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            byte[] bArr;
            if (message.what == 520756) {
                HNSocketMjpegUDP.access$708(HNSocketMjpegUDP.this);
                if (HNSocketMjpegUDP.this.fps_sec_flag % 30 == 0) {
                    HNSocketMjpegUDP hNSocketMjpegUDP = HNSocketMjpegUDP.this;
                    hNSocketMjpegUDP.fps = hNSocketMjpegUDP.fps1;
                    HNSocketMjpegUDP.this.fps1 = 0;
                    if (HNSocketMjpegUDP.this.fps_sec_flag == 6000) {
                        HNSocketMjpegUDP.this.fps_sec_flag = 0;
                    }
                }
                if (HNSocketMjpegUDP.this.videoQueue != null && HNSocketMjpegUDP.this.videoQueue.size() != 0 && HNSocketMjpegUDP.this.frame_number >= 3) {
                    if (!((HNSocketMjpegUDP.this.videoQueue.size() <= HNSocketMjpegUDP.this.frame_number + (-2)) & (!HNSocketMjpegUDP.this.is_frame_time_flag))) {
                        if (HNSocketMjpegUDP.this.videoQueue.size() < HNSocketMjpegUDP.this.frame_number + 3 || HNSocketMjpegUDP.this.is_frame_time_flag) {
                            if (HNSocketMjpegUDP.this.is_frame_time_flag & (HNSocketMjpegUDP.this.frame_number + 1 <= HNSocketMjpegUDP.this.videoQueue.size())) {
                                HNSocketMjpegUDP.this.stopTimer();
                                HNSocketMjpegUDP.this.startTimer(0L, 100L);
                                HNSocketMjpegUDP.this.is_frame_time_flag = false;
                                HNSocketMjpegUDP.this.decode_time = 100;
                                Log.e("aaa", "handleMessage: 33333");
                            }
                        } else {
                            HNSocketMjpegUDP.this.stopTimer();
                            HNSocketMjpegUDP.this.startTimer(0L, 80L);
                            HNSocketMjpegUDP.this.is_frame_time_flag = true;
                            HNSocketMjpegUDP.this.decode_time = 80;
                            Log.e("aaa", "handleMessage: 22222");
                        }
                    } else {
                        HNSocketMjpegUDP.this.stopTimer();
                        HNSocketMjpegUDP.this.startTimer(0L, 120L);
                        HNSocketMjpegUDP.this.is_frame_time_flag = true;
                        HNSocketMjpegUDP.this.decode_time = 120;
                        Log.e("aaa", "handleMessage: 11111");
                    }
                }
                if (HNSocketMjpegUDP.this.videoQueue != null && HNSocketMjpegUDP.this.videoQueue.size() > 0 && (bArr = (byte[]) HNSocketMjpegUDP.this.videoQueue.poll()) != null) {
                    HNSocketMjpegUDP.this.video_decode(bArr, bArr.length);
                    Log.d("aaa", "handleMessage: " + HNSocketMjpegUDP.this.fps_sec_flag);
                }
                WifiInfo connectionInfo = ((WifiManager) HNSocketMjpegUDP.this.context.getSystemService("wifi")).getConnectionInfo();
                int rssi = connectionInfo.getRssi();
                int linkSpeed = connectionInfo.getLinkSpeed();
                if (HNSocketMjpegUDP.this.tv_rev_display == null || HNSocketMjpegUDP.this.videoQueue == null) {
                    if (HNSocketMjpegUDP.this.tv_rev_display != null) {
                        TextView textView = HNSocketMjpegUDP.this.tv_rev_display;
                        textView.setText("display cur=" + HNSocketMjpegUDP.this.cur + "\tqueue.size:0\twifiRssi:" + rssi + " decode_time:" + HNSocketMjpegUDP.this.decode_time + "ms speed:" + linkSpeed);
                        return;
                    }
                    return;
                }
                TextView textView2 = HNSocketMjpegUDP.this.tv_rev_display;
                textView2.setText("display cur=" + HNSocketMjpegUDP.this.cur + "\tqueue.size:" + HNSocketMjpegUDP.this.videoQueue.size() + "\twifiRssi:" + rssi + " decode_time:" + HNSocketMjpegUDP.this.decode_time + "ms speed:" + linkSpeed);
            }
        }
    };

    static int access$708(HNSocketMjpegUDP hNSocketMjpegUDP) {
        int i = hNSocketMjpegUDP.fps_sec_flag;
        hNSocketMjpegUDP.fps_sec_flag = i + 1;
        return i;
    }

    public static HNSocketMjpegUDP getHnSocketMjpegUDP() {
        if (hnSocketMjpegUDP == null) {
            hnSocketMjpegUDP = new HNSocketMjpegUDP();
        }
        return hnSocketMjpegUDP;
    }

    public void initUDP(Context context, HsnImgVideoView hsnImgVideoView, HNMjpegListener hNMjpegListener, int i) {
        this.context = context;
        this.hsnGLSurfaceView = hsnImgVideoView;
        this.hnMjpegListener = hNMjpegListener;
        this.savePath = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name);
        initUDP(false);
    }

    public void initUDPForU3D(Context context, HNMjpegListener hNMjpegListener) {
        this.context = context;
        this.hnMjpegListener = hNMjpegListener;
        this.savePath = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name);
        initUDP(true);
    }

    public void initUDP(final boolean z) {
        if (this.frame_number != 0) {
            this.videoQueue = new ConcurrentLinkedQueue();
        }
        WifiManager wifiManager = (WifiManager) this.context.getSystemService("wifi");
        this.manager = wifiManager;
        this.lock = wifiManager.createMulticastLock("test wifi");
        File file = new File(String.valueOf(this.savePath) + "/H264");
        if (!file.exists()) {
            file.mkdirs();
        }
        Thread thread = new Thread(new Runnable() { // from class: com.ihunuo.hnmjpeg.socket.HNSocketMjpegUDP.2
            @Override // java.lang.Runnable
            public void run() {
                byte[] bArr = new byte[500000];
                byte[] bArr2 = new byte[500000];
                HNSocketMjpegUDP.this.isRev = true;
                HNSocketMjpegUDP.this.isplay = true;
                try {
                    HNSocketMjpegUDP.this.socket = new DatagramSocket(CommUtil.FYD_LOCAL_UDP_PORT);
                    HNSocketMjpegUDP.this.socket.setReceiveBufferSize(500000);
                } catch (Exception e2) {
                    LogUtils.e("HNSocketMjpegUDP, udp socket error:" + e2.getMessage());
                }
                DatagramPacket datagramPacket = new DatagramPacket(bArr, 500000);
                int i4 = 0;
                int i5 = 0;
                int i6 = 0;
                while (HNSocketMjpegUDP.this.isRev) {
                    try {
                        //HNSocketMjpegUDP.this.lock.acquire();
                    } catch (Exception e) {
                    }
                    if (HNSocketMjpegUDP.this.socket != null) {
                        try {
                            HNSocketMjpegUDP.this.socket.receive(datagramPacket);
                        } catch (IOException e3) {
                        }
                        byte[] data = datagramPacket.getData();
                        //HNSocketMjpegUDP.this.lock.release();
                        if (data[0] == 3 && HNSocketMjpegUDP.this.isplay) {
                            if ((data[3] & 255) == 0) {
                                i5 = data[2] & 255;
                                try {
                                    System.arraycopy(data, 9, bArr2, 0, datagramPacket.getLength() - 9);
                                    i6 = (datagramPacket.getLength() - 9) + 0;
                                    i4 = 0;
                                } catch (Exception e4) {
                                    i4 = 0;
                                    i6 = 0;
                                }
                            } else if ((data[3] & 255) == i4 + 1 && i5 == (data[2] & 255)) {
                                i4 = data[3] & 255;
                                System.arraycopy(data, 9, bArr2, i6, datagramPacket.getLength() - 9);
                                i6 += datagramPacket.getLength() - 9;
                            } else {
                                Log.e("error", "run: index not right, drop this frame!");
                                i4 = 0;
                            }
                            if ((data[4] & 255) == i4 + 1 && i5 == (data[2] & 255)) {
                                if (z) {
                                    HNSocketMjpegUDP.this.hnMjpegListener.revDataHNSocketUDP(bArr2, i6);
                                } else {
                                    HNSocketMjpegUDP.this.fengbao(bArr2, i6);
                                }
                                i4 = 0;
                            }
                        } else if (data[0] == 4 && HNSocketMjpegUDP.this.isplay) {
                            if (HNSocketMjpegUDP.this.isVoicePlay) {
                                byte[] bArr3 = new byte[datagramPacket.getLength() - 3];
                                System.arraycopy(data, 1, bArr3, 0, datagramPacket.getLength() - 3);
                                HNSocketMjpegUDP.this.decode_audio(bArr3);
                            }
                        } else if ((data[0] & 255) == 102 && (data[1] & 255) == 62) {
                            if (HNSocketMjpegUDP.this.hnMjpegListener != null) {
                                HNSocketMjpegUDP.this.hnMjpegListener.revDataHNSocketUDP(data, data.length);
                            }
                            byte b = data[2];
                            byte b2 = data[8];
                        }
                    } else {
                        return;
                    }
                }
            }
        });
        this.thread = thread;
        thread.start();
    }

    public void fengbao(byte[] bArr, int i) {
        this.fps1++;
        this.cur++;
        if (this.videoQueue != null) {
            byte[] bArr2 = new byte[i];
            System.arraycopy(bArr, 0, bArr2, 0, i);
            this.videoQueue.add(bArr2);
        } else {
            video_decode(bArr, i);
        }
        Log.d("aaa", "rev socket len=" + i + "  flag=" + (bArr[1] & 255) + " fps=" + this.fps);
    }

    public void decode_audio(byte[] bArr) {
    }

    public void video_decode(byte[] bArr, int i) {
        if (this.isFirst) {
            this.hnMjpegListener.videoDecodeSuccssed();
            this.isFirst = false;
        }
        this.mBitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, i, this.mBitmapOptions);
        if (decodeByteArray != null) {
            this.bitmap = decodeByteArray;
            this.width = decodeByteArray.getWidth();
            this.height = decodeByteArray.getHeight();
            Log.d("ccc", "video_decode: length=" + i + " " + this.width + " " + this.height);
            this.hsnGLSurfaceView.setCurrentBitmap(decodeByteArray);
            //decodeByteArray.recycle();
        }
    }

    /* loaded from: classes.dex */
    public class SendUDPThread extends Thread {
        public SendUDPThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            try {
                if (HNSocketMjpegUDP.this.socket == null) {
                    Log.e("aaa", "send_udp: socket=null");
                    return;
                }
                byte[] bArr = new byte[4];
                DatagramPacket datagramPacket = new DatagramPacket(bArr, 4, InetAddress.getByName(CommUtil.FYD_LOCAL_ADDR), CommUtil.FYD_LOCAL_UDP_PORT);
                bArr[0] = HNSocketMjpegUDP.this.send_udp_state[0];
                bArr[1] = HNSocketMjpegUDP.this.send_udp_state[1];
                for (int i = 0; i < 2; i++) {
                    bArr[2] = (byte) (bArr[2] + bArr[i]);
                }
                bArr[3] = HNSocketMjpegUDP.this.send_udp_state[2];
                Log.d("aaa", "send_udp: " + UIUtils.byte2hex(bArr));
                datagramPacket.setData(bArr);
                //HNSocketMjpegUDP.this.lock.acquire();
                HNSocketMjpegUDP.this.socket.send(datagramPacket);
                //HNSocketMjpegUDP.this.lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send_UDP_data(byte[] bArr) {
        for (int i = 0; i < bArr.length; i++) {
            this.send_udp_state[i] = bArr[i];
        }
        this.sendUDPThread = null;
        SendUDPThread sendUDPThread = new SendUDPThread();
        this.sendUDPThread = sendUDPThread;
        sendUDPThread.interrupt();
        this.sendUDPThread.start();
    }

    public void startTimer(long j, long j2) {
        if (this.timer == null) {
            this.timer = new Timer();
        }
        if (this.task == null) {
            TimerTask timerTask = new TimerTask() { // from class: com.ihunuo.hnmjpeg.socket.HNSocketMjpegUDP.3
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    Message message = new Message();
                    message.what = 520756;
                    HNSocketMjpegUDP.this.handler.sendMessage(message);
                }
            };
            this.task = timerTask;
            this.timer.schedule(timerTask, j, j2);
        }
    }

    public void stopTimer() {
        Timer timer = this.timer;
        if (timer != null) {
            timer.cancel();
            this.timer = null;
        }
        TimerTask timerTask = this.task;
        if (timerTask != null) {
            timerTask.cancel();
            this.task = null;
        }
    }

    public void setBufferNum(int i) {
        this.frame_number = i;
    }

    public boolean hn_socket_photo(boolean z) {
        if (this.width != 0 && this.height != 0) {
            this.hsnGLSurfaceView.hsnImgVideoRender.talkPhoto(z, getSavePath(), this.hnMjpegListener, this.width, this.height);
        }
        return this.hsnGLSurfaceView.hsnImgVideoRender.isTalkPhoto;
    }

    public boolean hn_socket_video(boolean z, int i, int i2) {
        if (this.width != 0 && this.height != 0) {
            this.is_video_flag = z;
            if (z) {
                if (i == 0 || i2 == 0) {
                    i = this.width;
                    i2 = this.height;
                }
                this.name = String.valueOf(System.currentTimeMillis()) + ".mp4";
                HsnMediaEncodec hsnMediaEncodec = new HsnMediaEncodec(this.context, this.hsnGLSurfaceView.getFbotextureid());
                this.hsnMediaEncodec = hsnMediaEncodec;
                EGLContext eglContext = this.hsnGLSurfaceView.getEglContext();
                hsnMediaEncodec.initEncodec(eglContext, Environment.getExternalStorageDirectory() + "/FYD-UAV/PhotoVideo/" + this.name, i, i2, 16000, 2, this.isVoice);
                this.hsnMediaEncodec.setOnMediaInfoListener(new HsnBaseMediaEncoder.OnMediaInfoListener() { // from class: com.ihunuo.hnmjpeg.socket.HNSocketMjpegUDP.4
                    @Override // com.ihunuo.hnmjpeg.opengl2.encodec.HsnBaseMediaEncoder.OnMediaInfoListener
                    public void onMediaTime(int i3) {
                        Log.d("ywl5320", "time is : " + i3);
                    }
                });
                this.hsnMediaEncodec.startRecord();
            } else {
                HsnMediaEncodec hsnMediaEncodec2 = this.hsnMediaEncodec;
                if (hsnMediaEncodec2 != null) {
                    hsnMediaEncodec2.isContinue = false;
                    this.hsnMediaEncodec.stopRecord();
                }
                this.handler.postDelayed(new Runnable() { // from class: com.ihunuo.hnmjpeg.socket.HNSocketMjpegUDP.5
                    @Override // java.lang.Runnable
                    public void run() {
                        HNSocketMjpegUDP.this.hsnMediaEncodec = null;
                    }
                }, 500L);
            }
        }
        return this.is_video_flag;
    }

    public void decodePlayAudioAAC(String str) {
    }

    public void releaseDecodeAudioAAC() {
    }

    public void setSavePath(String str) {
        this.savePath = str;
        File file = new File(String.valueOf(this.savePath) + "/H264");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public String getSavePath() {
        return this.savePath;
    }

    public void hn_UDP_Resume() {
        this.isplay = true;
        this.isVoicePlay = true;
    }

    public void hn_UDP_Pause() {
        this.isVoicePlay = false;
    }

    public void hn_UDP_release() {
        this.cur = 0;
        this.isRev = false;
        stopTimer();
        Queue queue = this.videoQueue;
        if (queue != null) {
            queue.clear();
            this.videoQueue = null;
        }
        DatagramSocket datagramSocket = this.socket;
        if (datagramSocket != null) {
            datagramSocket.close();
        }
        this.socket = null;
    }

    public void write(String str, byte[] bArr, int i, int i2) {
        try {
            if (Environment.getExternalStorageState().equals("mounted")) {
                new File(str);
                File file = new File(str);
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

    public void setTEXT(TextView textView, TextView textView2, ImageView imageView) {
        this.tv_rev_state = textView;
        this.tv_rev_display = textView2;
        this.ivphoto = imageView;
    }

    public boolean isRecording() {
        return this.is_video_flag;
    }

    public void renderBg(boolean z) {
        Bitmap bitmap = this.resIdBitmap;
        if (bitmap != null && z) {
            ImageView imageView = this.ivphoto;
            if (imageView == null) {
                this.hsnGLSurfaceView.setCurrentBitmap(bitmap);
            } else {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
