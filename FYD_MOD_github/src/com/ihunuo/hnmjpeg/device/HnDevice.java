package com.ihunuo.hnmjpeg.device;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.ihunuo.hnmjpeg.Listener.HNMjpegListener;
import com.ihunuo.hnmjpeg.opengl.OpenGLUtils;
import com.ihunuo.hnmjpeg.opengl2.imgvideo.HsnImgVideoRender;
import com.ihunuo.hnmjpeg.opengl2.imgvideo.HsnImgVideoView;
import com.ihunuo.hnmjpeg.socket.HNSocketMjpegUDP;
import com.ihunuo.hnmjpeg.socket.HNTCP;
import com.ihunuo.hnmjpeg.utils.UIUtils;
import java.io.File;

/* loaded from: classes.dex */
public class HnDevice {
    private final Activity activity;
    private int bufferNum;
    private HNMjpegListener hnMjpegListener;
    public HNTCP hnTCP;
    private HsnImgVideoView hsnGLSurfaceView;
    Handler handler = new Handler();
    public int isMjpeg = 1;
    public String Path_Photo_Video_Save = "";
    int modeSize = 0;
    private boolean isTaking = false;
    private String mTcpAddr = "";
    private int mTcpPort = 0;
    
    Runnable runnableTCPHeartbeat = new Runnable() { // from class: com.ihunuo.hnmjpeg.device.HnDevice.1
        @Override // java.lang.Runnable
        public void run() {
            HnDevice.this.handler.postDelayed(HnDevice.this.runnableTCPHeartbeat, 1000L);
            if (HnDevice.this.hnTCP.socket != null) {
                HnDevice.this.hnTcpSend(14);
            }
        }
    };
    boolean flagBroadcastReceiver = true;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.ihunuo.hnmjpeg.device.HnDevice.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(final Context context, final Intent intent) {
            char c;
            String action = intent.getAction();
            int hashCode = action.hashCode();
            if (hashCode != -406720640) {
                if (hashCode != -343630553 || !action.equals("android.net.wifi.STATE_CHANGE")) {
                }
                c = 65535;
            } else {
                if (action.equals("com.ihunuo.record")) {
                }
                c = 65535;
            }
            if (c != 0) {
                if (c == 1) {
                    new Thread(new Runnable() { // from class: com.ihunuo.hnmjpeg.device.HnDevice.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                Thread.sleep(5000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            String stringExtra = intent.getStringExtra("savePath");
                            Log.d("ccc", "savePath=" + stringExtra);
                            String str = String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath()) + "/DCIM/Camera/" + stringExtra.substring(stringExtra.lastIndexOf("/") + 1);
                            Log.d("ccc", "isADD_DCIM: " + str);
                            UIUtils.copyFile(stringExtra, str, context);
                        }
                    }).start();
                    return;
                }
                return;
            }
            Parcelable parcelableExtra = intent.getParcelableExtra("networkInfo");
            if (parcelableExtra != null && ((NetworkInfo) parcelableExtra).getState() == NetworkInfo.State.CONNECTED) {
                if (!HnDevice.this.flagBroadcastReceiver) {
                    HnDevice.this.flagBroadcastReceiver = true;
                    HnDevice hnDevice = HnDevice.this;
                    hnDevice.isMjpeg = hnDevice.isMjpeg(hnDevice.activity);
                    if (HnDevice.this.isMjpeg == 1) {
                        HnDevice.this.hnTCP.close();
                        HnDevice.this.handler.postDelayed(new Runnable() { // from class: com.ihunuo.hnmjpeg.device.HnDevice.2.2
                            @Override // java.lang.Runnable
                            public void run() {
                                HnDevice.this.hnTCP.setHntcpNull();
                                HnDevice.this.hnTCP = null;
                                HnDevice.this.hnTCP = HNTCP.getInstance();
                                HnDevice.this.hnTCP.setHnMjpegListener(HnDevice.this.hnMjpegListener);
                                HnDevice.this.hnTCP.setTcp(mTcpAddr, mTcpPort);
                                HnDevice.this.hnTCP.start();
                                HnDevice.this.sendFirstConnet();
                            }
                        }, 1000L);
                    }
                    Log.d("ReConnect", "onReceive: hnTCP.start() isMjpeg=" + HnDevice.this.isMjpeg);
                }
                HnDevice.this.handler.postDelayed(new Runnable() { // from class: com.ihunuo.hnmjpeg.device.HnDevice.2.3
                    @Override // java.lang.Runnable
                    public void run() {
                        HnDevice.this.flagBroadcastReceiver = false;
                    }
                }, 2000L);
            }
        }
    };
    public Runnable runnableTcpReConnect = new Runnable() { // from class: com.ihunuo.hnmjpeg.device.HnDevice.3
        @Override // java.lang.Runnable
        public void run() {
            if (HnDevice.this.hnTCP.tcpReConnectFlag >= 15) {
                HnDevice hnDevice = HnDevice.this;
                hnDevice.isMjpeg = hnDevice.isMjpeg(hnDevice.activity);
                if (HnDevice.this.isMjpeg == 1) {
                    HnDevice.this.hnTCP.close();
                    HnDevice.this.hnSocketMjpegUDP.hn_UDP_release();
                    HnDevice.this.handler.postDelayed(new Runnable() { // from class: com.ihunuo.hnmjpeg.device.HnDevice.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            HnDevice.this.hnSocketMjpegUDP = HNSocketMjpegUDP.getHnSocketMjpegUDP();
                            HnDevice.this.hnSocketMjpegUDP.initUDP(HnDevice.this.activity, HnDevice.this.hsnGLSurfaceView, HnDevice.this.hnMjpegListener, HnDevice.this.isMjpeg);
                            HnDevice.this.hnTCP.setHntcpNull();
                            HnDevice.this.hnTCP = null;
                            HnDevice.this.hnTCP = HNTCP.getInstance();
                            HnDevice.this.hnTCP.setHnMjpegListener(HnDevice.this.hnMjpegListener);
                            HnDevice.this.hnTCP.setTcp(mTcpAddr, mTcpPort);
                            HnDevice.this.hnTCP.start();
                            HnDevice.this.sendFirstConnet();
                        }
                    }, 1000L);
                }
                Log.d("ReConnect", "runnableTcpReConnect: hnTCP.start()");
                HnDevice.this.hnTCP.tcpReConnectFlag = 0;
            }
            if (HnDevice.this.isMjpeg > 0) {
                HnDevice.this.hnTCP.tcpReConnectFlag++;
            }
            Log.d("tcpReConnectFlag", " " + HnDevice.this.hnTCP.tcpReConnectFlag);
            HnDevice.this.handler.postDelayed(HnDevice.this.runnableTcpReConnect, 1000L);
        }
    };
    public HNSocketMjpegUDP hnSocketMjpegUDP = HNSocketMjpegUDP.getHnSocketMjpegUDP();

    public HnDevice(Activity activity) {
        this.activity = activity;
    }

    public void start(HsnImgVideoView hsnImgVideoView, HNMjpegListener hNMjpegListener, String tcpAddr, int tcpPort) {
        this.hnMjpegListener = hNMjpegListener;
        this.hsnGLSurfaceView = hsnImgVideoView;
        this.hnSocketMjpegUDP.setBufferNum(this.bufferNum);
        this.hnSocketMjpegUDP.initUDP(this.activity, hsnImgVideoView, hNMjpegListener, this.isMjpeg);
        HNTCP hntcp = HNTCP.getInstance();
        this.hnTCP = hntcp;
        hntcp.setHnMjpegListener(hNMjpegListener);
        mTcpAddr = tcpAddr;
        mTcpPort = tcpPort;
        this.hnTCP.setTcp(tcpAddr, tcpPort);//new add
        this.hnTCP.start();
        sendTCPHeartbeat();
        IntentFilter intentFilter = new IntentFilter("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("com.ihunuo.record");
        this.activity.registerReceiver(this.mReceiver, intentFilter);
        this.handler.postDelayed(new Runnable() { // from class: com.ihunuo.hnmjpeg.device.HnDevice.4
            @Override // java.lang.Runnable
            public void run() {
                HnDevice.this.flagBroadcastReceiver = false;
            }
        }, 10000L);
    }

    public void startForU3D(HNMjpegListener hNMjpegListener) {
        this.hnSocketMjpegUDP.setBufferNum(this.bufferNum);
        this.hnSocketMjpegUDP.initUDPForU3D(this.activity, hNMjpegListener);
        sendTCPHeartbeat();
    }

    public void startJLVideo(int i) {
        this.modeSize = i;
        int isMjpeg = isMjpeg(this.activity);
        this.isMjpeg = isMjpeg;
        this.hnSocketMjpegUDP.isMjpeg = isMjpeg;
    }

    public void setRotate(int i, int i2, int i3, int i4) {
        if (this.hnSocketMjpegUDP.hsnGLSurfaceView != null) {
            this.hnSocketMjpegUDP.hsnGLSurfaceView.setRotate(i, i2, i3, i4);
        }
    }

    public void setBufferNum(int i) {
        this.bufferNum = i;
    }

    public void setTEXT(TextView textView, TextView textView2, ImageView imageView) {
        this.hnSocketMjpegUDP.setTEXT(textView, textView2, imageView);
    }

    public boolean startStopRecording(boolean z) {
        return true;
    }

    public boolean isRecording() {
        return isRecording();
    }

    public boolean talkPhoto(boolean z) {
        return true;
    }

    public void isTalkPhotoDaTouTie(boolean z, Bitmap bitmap) {
        this.hnSocketMjpegUDP.hsnGLSurfaceView.hsnImgVideoRender.isTaikPhotoDaTouTie = z;
        this.hnSocketMjpegUDP.hsnGLSurfaceView.hsnImgVideoRender.bitmapDaTouTie = bitmap;
    }

    public void isTalkPhotoPush(boolean z) {
        this.hnSocketMjpegUDP.hsnGLSurfaceView.hsnImgVideoRender.isTaikPhotoPush = z;
    }

    public void setSaveMediaPath(String str) {
        this.hnSocketMjpegUDP.setSavePath(str);
        createPath(str);
    }

    private void createPath(String str) {
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public String getSaveMediaPath() {
        return this.hnSocketMjpegUDP.getSavePath();
    }

    public boolean getIsVR() {
        return this.hnSocketMjpegUDP.hsnGLSurfaceView.getIsVR();
    }

    public void setIsVR(boolean z) {
        this.hnSocketMjpegUDP.hsnGLSurfaceView.setIsVR(z);
    }

    public void setSurfaceBg(Resources resources, int i) {
        this.hnSocketMjpegUDP.resIdBitmap = OpenGLUtils.getBitmapFromResource(resources, i);
        this.hnSocketMjpegUDP.hsnGLSurfaceView.setCurrentBitmap(this.hnSocketMjpegUDP.resIdBitmap);
    }

    public void renderBg(boolean z) {
        this.hnSocketMjpegUDP.renderBg(z);
    }

    public void hnTcpSend(int i) {
        HNTCP hntcp = this.hnTCP;
        if (hntcp != null) {
            hntcp.send(i);
        }
        if (i == 19) {
            this.hnSocketMjpegUDP.isVoice = true;
        } else if (i == 20) {
            this.hnSocketMjpegUDP.isVoice = false;
        }
    }

    public void hnTcpSendChangeSizeMode(int i) {
        this.hnTCP.sizeModeChange = i;
        this.hnTCP.send(17);
    }

    public boolean getTFCardStatus() {
        return this.hnTCP.TFCardFlag;
    }

    public int getJLTFCardStatus() {
        return 0;
    }

    public int getWifiSizeMode() {
        return this.hnTCP.sizeMode;
    }

    public boolean isWIFIZanYong() {
        return this.hnTCP.isWIFIZanYong;
    }

    public String getWifiVerson() {
        return this.hnTCP.WifiVerson;
    }

    public void setIsTalkPhotoSize(int i) {
        HNSocketMjpegUDP hNSocketMjpegUDP = this.hnSocketMjpegUDP;
        if (hNSocketMjpegUDP != null) {
            HsnImgVideoRender hsnImgVideoRender = hNSocketMjpegUDP.hsnGLSurfaceView.hsnImgVideoRender;
            HsnImgVideoRender.isTalkPhotoSize = i;
        }
    }

    public int getVideoformat() {
        if (this.hnSocketMjpegUDP.width <= 640) {
            return 1;
        }
        if (this.hnSocketMjpegUDP.width <= 1280) {
            return 2;
        }
        if (this.hnSocketMjpegUDP.width <= 1920) {
            return 3;
        }
        if (this.hnSocketMjpegUDP.width <= 2560) {
            return 4;
        }
        return this.hnSocketMjpegUDP.width <= 4096 ? 5 : 0;
    }

    public void setFilter(int i) {
        this.hnSocketMjpegUDP.filter = i;
        this.hsnGLSurfaceView.getHsnRender().filter = i;
    }

    public void sendTCPHeartbeat() {
        this.handler.post(this.runnableTCPHeartbeat);
        //this.handler.post(this.runnableTcpReConnect);
        sendFirstConnet();
    }

    public void sendFirstConnet() {
        this.handler.postDelayed(new Runnable() { // from class: com.ihunuo.hnmjpeg.device.HnDevice.5
            @Override // java.lang.Runnable
            public void run() {
                HnDevice.this.hnTcpSend(2);
                HnDevice.this.hnTcpSend(3);
                HnDevice.this.hnTcpSend(7);
                HnDevice.this.hnTcpSend(13);
                HnDevice.this.hnTcpSend(16);
                HnDevice.this.hnTcpSend(23);
            }
        }, 500L);
        this.handler.postDelayed(new Runnable() { // from class: com.ihunuo.hnmjpeg.device.HnDevice.6
            @Override // java.lang.Runnable
            public void run() {
                HnDevice.this.hnTcpSend(18);
            }
        }, 2000L);
    }

    public void stopSendTCPHeartbeat() {
        this.handler.removeCallbacks(this.runnableTCPHeartbeat);
    }

    public void Resume() {
        this.hnSocketMjpegUDP.hn_UDP_Resume();
    }

    public void Pause() {
        this.hnSocketMjpegUDP.hn_UDP_Pause();
    }

    public void release() {
        this.hnSocketMjpegUDP.hn_UDP_release();
        this.hnSocketMjpegUDP = null;
        this.activity.unregisterReceiver(this.mReceiver);
        this.handler.removeCallbacks(this.runnableTCPHeartbeat);
        this.handler.removeCallbacks(this.runnableTcpReConnect);
    }

    public int isMjpeg(Context context) {
        return 1;
    }
}
