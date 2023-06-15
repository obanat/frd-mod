package com.ihunuo.hnmjpeg.socket;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.ihunuo.hnmjpeg.Listener.HNMjpegListener;
import com.ihunuo.hnmjpeg.utils.UIUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class HNUDP {
    private Context context;
    public byte[] data_send;
    private String f133ip;
    private HNMjpegListener hnSingleFrameListener;
    private WifiManager.MulticastLock lock;
    private int port;
    private int portbroadcast;
    public String server_ip;
    public int server_port;
    private TimerTask task;
    private Timer timer;
    private String TAG = "HNUDP";
    public DatagramSocket socket = null;
    boolean isread = true;
    private Handler handler = new Handler() { // from class: com.ihunuo.hnmjpeg.socket.HNUDP.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 110756) {
                HNUDP.this.write();
            }
        }
    };

    public HNUDP(String str, int i, Context context, HNMjpegListener hNMjpegListener) {
        this.f133ip = "";
        this.port = 0;
        this.portbroadcast = 0;
        this.f133ip = str;
        this.port = i;
        this.portbroadcast = i;
        this.context = context;
        this.hnSingleFrameListener = hNMjpegListener;
    }

    public HNUDP(String str, int i, int i2, Context context, HNMjpegListener hNMjpegListener) {
        this.f133ip = "";
        this.port = 0;
        this.portbroadcast = 0;
        this.f133ip = str;
        this.port = i;
        this.portbroadcast = i2;
        this.context = context;
        this.hnSingleFrameListener = hNMjpegListener;
    }

    public void initUDP() {
        if (this.socket == null) {
            this.lock = ((WifiManager) this.context.getSystemService("wifi")).createMulticastLock("test wifi");
            new Thread(new Runnable() { // from class: com.ihunuo.hnmjpeg.socket.HNUDP.2
                @Override // java.lang.Runnable
                public void run() {
                    byte[] bArr = new byte[1024];
                    try {
                        HNUDP.this.socket = new DatagramSocket(HNUDP.this.port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DatagramPacket datagramPacket = new DatagramPacket(bArr, 1024);
                    HNUDP.this.isread = true;
                    while (HNUDP.this.isread && HNUDP.this.socket != null) {
                        HNUDP.this.lock.acquire();
                        try {
                            HNUDP.this.socket.receive(datagramPacket);
                        } catch (IOException e2) {
                        }
                        byte[] data = datagramPacket.getData();
                        HNUDP.this.lock.release();
                        HNUDP.this.server_ip = datagramPacket.getAddress().toString().substring(1);
                        HNUDP.this.server_port = datagramPacket.getPort();
                        byte[] bArr2 = new byte[datagramPacket.getLength()];
                        System.arraycopy(data, 0, bArr2, 0, datagramPacket.getLength());
                        String str = HNUDP.this.TAG;
                        Log.d(str, "rev: " + UIUtils.byte2hex(bArr2));
                        if (HNUDP.this.hnSingleFrameListener != null) {
                            HNUDP.this.hnSingleFrameListener.revDataHNSocketUDP(bArr2, datagramPacket.getLength());
                        }
                    }
                }
            }).start();
        }
    }

    /* loaded from: classes.dex */
    public class SendUDPThread extends Thread {
        public SendUDPThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            if (HNUDP.this.data_send != null) {
                try {
                    if (HNUDP.this.socket == null) {
                        Log.e(HNUDP.this.TAG, "send_udp: socket=null");
                    } else {
                        DatagramPacket datagramPacket = new DatagramPacket(HNUDP.this.data_send, HNUDP.this.data_send.length, InetAddress.getByName(HNUDP.this.f133ip), HNUDP.this.portbroadcast);
                        datagramPacket.setData(HNUDP.this.data_send);
                        String str = HNUDP.this.TAG;
                        Log.d(str, "send_udp1: " + HNUDP.this.f133ip + " " + HNUDP.this.portbroadcast + " " + UIUtils.byte2hex(HNUDP.this.data_send));
                        HNUDP.this.lock.acquire();
                        HNUDP.this.socket.send(datagramPacket);
                        HNUDP.this.lock.release();
                        String str2 = HNUDP.this.TAG;
                        Log.d(str2, "send_udp: " + UIUtils.byte2hex(HNUDP.this.data_send));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void write() {
        new SendUDPThread().start();
    }

    public void setSendData(byte[] bArr) {
        this.data_send = bArr;
    }

    public void setIP_Port(String str, int i) {
        this.f133ip = str;
        this.port = i;
    }

    public void set_Port(int i) {
        this.port = i;
    }

    public void startTimer(long j, long j2) {
        if (this.timer == null) {
            this.timer = new Timer();
        }
        if (this.task == null) {
            this.task = new TimerTask() { // from class: com.ihunuo.hnmjpeg.socket.HNUDP.3
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    Message message = new Message();
                    message.what = 110756;
                    HNUDP.this.handler.sendMessage(message);
                }
            };
        }
        this.timer.schedule(this.task, j, j2);
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

    public void close() {
        this.isread = false;
        stopTimer();
        DatagramSocket datagramSocket = this.socket;
        if (datagramSocket != null) {
            datagramSocket.close();
            this.socket = null;
        }
    }
}
