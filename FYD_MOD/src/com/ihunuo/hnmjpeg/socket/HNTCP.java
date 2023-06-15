package com.ihunuo.hnmjpeg.socket;

import android.os.Handler;
import android.util.Log;
import com.ihunuo.hnmjpeg.Listener.HNMjpegListener;
import com.ihunuo.hnmjpeg.opengl2.imgvideo.HsnImgVideoRender;
import com.ihunuo.hnmjpeg.utils.Cipher;
import com.ihunuo.hnmjpeg.utils.DateUtil;
import com.ihunuo.hnmjpeg.utils.UIUtils;
import com.obana.fydmod.CommUtil;
import com.obana.fydmod.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/* loaded from: classes.dex */
public class HNTCP extends Thread {
    private static final int MEDIA_BUF_SIZE = 512000;
    private static final int UAV_TCP_SERVER_PORT = 28000;
    private static HNTCP hntcp = null;
    public static boolean isDualCamera = true;
    public int droneType;
    private HNMjpegListener hnMjpegListener;
    private boolean isConnect;
    public boolean isRead;
    public boolean isWIFIZanYong;
    public boolean isWrite;
    private String TAG = "HNTCP";

    public Socket socket = null;
    public OutputStream write = null;
    public InputStream read = null;
    public boolean TFCardFlag = false;
    public int sizeMode = 0;
    public String WifiVerson = "";
    public boolean jiaoyanflag = false;
    private int jiaoYanTime = 3;
    public int sizeModeChange = 0;
    Handler handler = new Handler();
    private byte[] rand_num = new byte[10];
    private byte[] rand_num_jiaoyan = new byte[14];
    public int tcpReConnectFlag = 0;
    public int wifipower = 0;
    public int power = 0;
    public Queue sendBufQueue = new ConcurrentLinkedQueue();
    private Object mPauseLock = new Object();
    private boolean mPauseFlag = false;
    private ServerSocket mediaSocket = null;
    InputStream mediaStream = null;
    private boolean bMediaConnected = false;
    private boolean bThreadRunning = false;
    Socket clientSocket = null;
    InputStreamReader isr = null;
    private byte[] mediaBuffer = new byte[MEDIA_BUF_SIZE];
    private byte[] bArr2 = new byte[MEDIA_BUF_SIZE];

    private String mTcpAddr = CommUtil.FYD_LOCAL_ADDR;
    private int mTcpPort = CommUtil.FYD_LOCAL_TCP_PORT;
    
    Thread writeThread = new Thread(new Runnable() { // from class: com.ihunuo.hnmjpeg.socket.HNTCP.1
        @Override // java.lang.Runnable
        public void run() {
            HNTCP.this.isWrite = true;
            while (HNTCP.this.isWrite) {
                try {
                    if (HNTCP.this.sendBufQueue != null && HNTCP.this.write != null && HNTCP.this.sendBufQueue.size() != 0) {
                        byte[] bArr = (byte[]) HNTCP.this.sendBufQueue.poll();
                        String str = HNTCP.this.TAG;
                        Log.d(str, "write" + UIUtils.byte2hex(bArr));
                        HNTCP.this.write.write(bArr);
                        HNTCP.this.write.flush();
                    } else {
                        HNTCP.this.onPause();
                        HNTCP.this.pauseThread();
                    }
                    //Log.d(HNTCP.this.TAG, "TCP: write");
                    Thread.sleep(50L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    });
    private Runnable jiaoYanRunnable = new Runnable() { // from class: com.ihunuo.hnmjpeg.socket.HNTCP.2
        @Override // java.lang.Runnable
        public void run() {
            if (HNTCP.this.jiaoyanflag) {
                HNTCP.this.handler.removeCallbacks(HNTCP.this.jiaoYanRunnable);
                return;
            }
            HNTCP.access$310(HNTCP.this);
            String str = HNTCP.this.TAG;
            Log.i(str, "jiaoYanTime: " + HNTCP.this.jiaoYanTime);
            if (HNTCP.this.jiaoYanTime == 0) {
                //System.exit(0);
                LogUtils.e("jiaoYanTime fatal error! do something....");
            }
            HNTCP.this.send(18);
            HNTCP.this.handler.postDelayed(HNTCP.this.jiaoYanRunnable, 5000L);
        }
    };

    static int access$310(HNTCP hntcp2) {
        int i = hntcp2.jiaoYanTime;
        hntcp2.jiaoYanTime = i - 1;
        return i;
    }

    private HNTCP() {
    }

    public static synchronized HNTCP getInstance() {
        HNTCP hntcp2;
        synchronized (HNTCP.class) {
            synchronized (HNTCP.class) {
                if (hntcp == null) {
                    hntcp = new HNTCP();
                }
                hntcp2 = hntcp;
            }
            return hntcp2;
        }
    }

    public void setHntcpNull() {
        if (hntcp != null) {
            hntcp = null;
        }
    }

    public void setHnMjpegListener(HNMjpegListener hNMjpegListener) {
        this.hnMjpegListener = hNMjpegListener;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        try {
        	LogUtils.i(this.TAG, "run: createHNTCP, addr:" + mTcpAddr + " port:" + mTcpPort);
            //createTcpMediaSocket();
            this.isRead = true;
            Socket socket;
            try {
	            socket = new Socket(mTcpAddr, mTcpPort);
	            this.socket = socket;
	            this.write = socket.getOutputStream();
	            this.read = this.socket.getInputStream();
	            this.writeThread.start();

	            Thread mediaThread = new Thread(tcpMediaReceiver);
	            mediaThread.setName("mediaThread");
	            mediaThread.start();
            } catch (IOException e) {
            	LogUtils.e("hntcp error! , e:"+ e.getMessage());
            }
            if (this.socket != null && this.write != null && this.read != null) {
                if (this.hnMjpegListener != null) {
                    this.hnMjpegListener.createHNTCPSusscced(0);
                }
                this.handler.postDelayed(this.jiaoYanRunnable, 5000L);
            }
            this.isConnect = true;
            LogUtils.i(this.TAG, "run: createHNTCP, success!");
            while (this.isRead) {
                byte[] bArr = new byte[100];
                if (this.read != null) {
                    int read = this.read.read(bArr);
                    if (read < 0) {
                        this.isRead = false;
                        this.isConnect = false;
                        Log.d(this.TAG, "run: tcp Read false;");
                        return;
                    }
                    byte[] bArr2 = new byte[read];
                    System.arraycopy(bArr, 0, bArr2, 0, read);
                    String str = this.TAG;
                    Log.i(str, "rev data: " + UIUtils.byte2hex(bArr2));
                    if ((bArr2[0] & 255) != 20 && (bArr2[0] & 255) != 10) {
                        int i = read / 4;
                        for (int i2 = 0; i2 < i; i2++) {
                            int i3 = i2 * 4;
                            parseData(new byte[]{bArr2[i3], bArr2[i3 + 1], bArr2[i3 + 2], bArr2[i3 + 3]});
                        }
                    }
                    parseData(bArr2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            HNMjpegListener hNMjpegListener = this.hnMjpegListener;
            if (hNMjpegListener != null) {
                hNMjpegListener.createHNTCPSusscced(-1);
            }
        }
    }

    private void parseData(byte[] bArr) {
        HNMjpegListener hNMjpegListener;
        //Log.d("CCC-CCC", UIUtils.byte2hex(bArr));
        if ((bArr[0] & 255) == 14 && (bArr[2] & 255) == 175 && (bArr[3] & 255) == 224) {
            if ((bArr[1] & 255) == 1) {
                this.tcpReConnectFlag = 0;

                LogUtils.i(TAG+" rev info set tcpReConnectFlag=0");
            }
        } else if ((bArr[0] & 255) == 6 && (bArr[2] & 255) == 192 && (bArr[3] & 255) == 96) {
            if ((bArr[1] & 255) == 0) {
                this.TFCardFlag = false;
            } else if ((bArr[1] & 255) == 1) {
                this.TFCardFlag = true;
            }
            String str2 = this.TAG;
            Log.d(str2, "run: TFCard " + this.TFCardFlag);
        } else if ((bArr[0] & 255) == 13 && (bArr[2] & 255) == 208 && (bArr[3] & 255) == 208) {
            this.sizeMode = bArr[1] & 255;
            String str3 = this.TAG;
            Log.d(str3, "run: sizeMode " + this.sizeMode);
            HNMjpegListener hNMjpegListener2 = this.hnMjpegListener;
            if (hNMjpegListener2 != null) {
                hNMjpegListener2.revDataHNSocketTCP(5);
            }
        } else if ((bArr[0] & 255) == 10) {
            byte[] bArr2 = new byte[bArr.length - 1];
            System.arraycopy(bArr, 1, bArr2, 0, bArr.length - 1);
            this.WifiVerson = new String(bArr2);
            String str4 = this.TAG;
            Log.d(str4, "run: WifiVerson " + this.WifiVerson);
        } else if ((bArr[0] & 255) == 16) {
            if ((bArr[1] & 255) == 1) {
                this.isWIFIZanYong = true;
                HNMjpegListener hNMjpegListener3 = this.hnMjpegListener;
                if (hNMjpegListener3 != null) {
                    hNMjpegListener3.revDataHNSocketTCP(1);
                }
            } else {
                this.isWIFIZanYong = false;
                HNMjpegListener hNMjpegListener4 = this.hnMjpegListener;
                if (hNMjpegListener4 != null) {
                    hNMjpegListener4.revDataHNSocketTCP(2);
                }
            }
            String str5 = this.TAG;
            Log.d(str5, "run: isWIFIZanYong " + this.isWIFIZanYong);
        } else if ((bArr[0] & 255) != 18) {
            if ((bArr[0] & 255) == 20) {
                byte[] bArr3 = new byte[14];
                System.arraycopy(bArr, 0, bArr3, 0, bArr.length > 14 ? 14 : bArr.length);
                String byte2hex = UIUtils.byte2hex(bArr3);
                String str6 = this.TAG;
                Log.d(str6, "rev: " + byte2hex);
                String str7 = this.TAG;
                Log.d(str7, "rand_num_jiaoyan: " + UIUtils.byte2hex(this.rand_num_jiaoyan));
                if (byte2hex.equals(UIUtils.byte2hex(this.rand_num_jiaoyan))) {
                    Log.d(this.TAG, "check OK, jiaoyanflag = true");
                    this.jiaoyanflag = true;
                    HNMjpegListener hNMjpegListener5 = this.hnMjpegListener;
                    if (hNMjpegListener5 != null) {
                        hNMjpegListener5.createHNTCPSusscced(0);
                    }
                }
            } else if ((bArr[0] & 255) == 22 && (bArr[3] & 255) == 97) {
                HNMjpegListener hNMjpegListener6 = this.hnMjpegListener;
                if (hNMjpegListener6 != null) {
                    hNMjpegListener6.revDataHNSocketTCP(16);
                }
            } else if ((bArr[0] & 255) == 23 && (bArr[3] & 255) == 113) {
                HNMjpegListener hNMjpegListener7 = this.hnMjpegListener;
                if (hNMjpegListener7 != null) {
                    hNMjpegListener7.revDataHNSocketTCP(17);
                }
            } else if ((bArr[0] & 255) == 24) {
                if ((bArr[1] & 255) == 1) {
                    HNMjpegListener hNMjpegListener8 = this.hnMjpegListener;
                    if (hNMjpegListener8 != null) {
                        hNMjpegListener8.revDataHNSocketTCP(18);
                    }
                    Log.d(this.TAG, "run: start talkphoto");
                }
            } else if ((bArr[0] & 255) == 25) {
                HNMjpegListener hNMjpegListener9 = this.hnMjpegListener;
                if (hNMjpegListener9 != null) {
                    hNMjpegListener9.revDataHNSocketTCP(3);
                }
                Log.d(this.TAG, "run: start recording ");
            } else if ((bArr[0] & 255) == 26) {
                if ((bArr[1] & 255) == 0 && (hNMjpegListener = this.hnMjpegListener) != null) {
                    hNMjpegListener.revDataHNSocketTCP(3);
                }
            } else if ((bArr[0] & 255) == 28) {
                this.power = bArr[1] & 255;
                this.wifipower = ((bArr[2] & 255) * 4) / 50;
                HNMjpegListener hNMjpegListener10 = this.hnMjpegListener;
                if (hNMjpegListener10 != null) {
                    hNMjpegListener10.revDataHNSocketTCP(14);
                }
            } else if ((bArr[0] & 255) == 33) {
                HsnImgVideoRender.isTalkPhotoSize = bArr[1] & 255;
                String str8 = this.TAG;
                Log.d(str8, "HsnImgVideoRender.isTalkPhotoSize: " + HsnImgVideoRender.isTalkPhotoSize);
            } else if ((bArr[0] & 255) == 35) {
                String str9 = this.TAG;
                LogUtils.i(str9 + Integer.toHexString(bArr[0]) + "  " + Integer.toHexString(bArr[1]) + "  " + Integer.toHexString(bArr[2]) + "  " + Integer.toHexString(bArr[3]));
            } else if ((bArr[0] & 255) == 38) {
                this.droneType = bArr[1] & 255;
            } else if ((bArr[0] & 255) == 40) {
                if ((bArr[1] & 255) == 0) {
                    isDualCamera = false;
                }
                String str10 = this.TAG;
                Log.i(str10, "isDualCamera: " + isDualCamera);
                HNMjpegListener hNMjpegListener11 = this.hnMjpegListener;
                if (hNMjpegListener11 != null) {
                    hNMjpegListener11.revDataHNSocketTCP(28);
                }
            } else {
                HNMjpegListener hNMjpegListener12 = this.hnMjpegListener;
                if (hNMjpegListener12 != null) {
                    hNMjpegListener12.revDataHNSocketTCP(0);
                }
            }
        }
    }

    public void send(byte[] bArr) {
        this.sendBufQueue.add(bArr);
        onResume();
    }

    public void send(int i) {
        if (i == 0) {
            send(new byte[]{11, 1, -64, -80});
        } else if (i == 1) {
            send(new byte[]{11, -76, -64, -80});
        } else if (i == 2) {
            send(new byte[]{9, 1, -64, -112});
        } else if (i == 3) {
            String curDate = DateUtil.getCurDate("yyyy-MM-dd HH:mm:ss");
            send(("TIME " + curDate).getBytes());
        } else if (i == 4) {
            send(new byte[]{7, 1, -64, 112});
        } else if (i == 5) {
            send(new byte[]{8, 1, -64, Byte.MIN_VALUE});
        } else if (i == 6) {
            send(new byte[]{8, 0, -64, Byte.MIN_VALUE});
        } else if (i == 7) {
            send(new byte[]{5, 1, -64, 80});
        } else if (i == 8) {
            send(new byte[]{1, 1, 2, 16});
        } else if (i == 9) {
            send(new byte[]{1, 0, 1, 16});
        } else if (i == 10) {
            send(new byte[]{2, 1, 3, 32});
        } else if (i == 11) {
            send(new byte[]{2, 0, 2, 32});
        } else if (i == 12) {
            send(new byte[]{12, 1, -64, -64});
        } else if (i == 13) {
            send(new byte[]{15, 1, -81, -16});
        } else if (i == 14) {
            send(new byte[]{14, 1, -81, -32});
        } else if (i == 15) {
            send(new byte[]{18, 0, -81, 33});
        } else if (i == 16) {
            send(new byte[]{13, 1, -48, -48});
        } else if (i == 17) {
            send(new byte[]{19, (byte) this.sizeModeChange, 20, 49});
        } else if (i == 18) {
            byte[] bArr = this.rand_num_jiaoyan;
            bArr[0] = 20;
            bArr[1] = 1;
            bArr[2] = -64;
            bArr[3] = 65;
            Random random = new Random();
            for (int i2 = 0; i2 < 10; i2++) {
                this.rand_num[i2] = (byte) random.nextInt(255);
                this.rand_num_jiaoyan[i2 + 4] = this.rand_num[i2];
            }
            this.rand_num = new Cipher().Encrypt(this.rand_num);
            byte[] bArr2 = new byte[14];
            bArr2[0] = 20;
            bArr2[1] = 1;
            bArr2[2] = -64;
            bArr2[3] = 65;
            for (int i3 = 0; i3 < 10; i3++) {
                bArr2[i3 + 4] = this.rand_num[i3];
            }
            send(bArr2);
        } else if (i == 19) {
            send(new byte[]{21, 1, -64, 81});
        } else if (i == 20) {
            send(new byte[]{21, 0, -64, 81});
        } else if (i == 21) {
            send(new byte[]{26, 1, -64, -95});
        } else if (i == 22) {
            send(new byte[]{26, 2, -64, -95});
        } else if (i == 23) {
            send(new byte[]{27, 1, -64, -79});
        } else if (i != 24) {
            if (i == 26) {
                send(new byte[]{35, 0, -64, 50});
            }
        } else {
            send(new byte[]{27, 0, -64, -79});
        }
    }

    public void onPause() {
        synchronized (this.mPauseLock) {
            this.mPauseFlag = true;
        }
    }

    public void onResume() {
        synchronized (this.mPauseLock) {
            this.mPauseFlag = false;
            this.mPauseLock.notifyAll();
        }
    }

    public void pauseThread() {
        synchronized (this.mPauseLock) {
            if (this.mPauseFlag) {
                try {
                    this.mPauseLock.wait();
                } catch (Exception e) {
                    Log.v("thread", "fails");
                }
            }
        }
    }

    public void close() {
        this.isRead = false;
        this.isWrite = false;
        this.isConnect = false;
        try {
            interrupt();
            if (this.read != null) {
                this.read.close();
            }
            if (this.write != null) {
                this.write.close();
            }
            this.read = null;
            this.write = null;
            if (this.socket != null) {
                this.socket.close();
            }
            this.socket = null;
            Log.d(this.TAG, "close: socket");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnect() {
        return this.isConnect;
    }

    public void createTcpMediaSocket() {
        LogUtils.i(this.TAG + " createTcpMediaSocket start .....");
        try {
            this.mediaSocket = new ServerSocket();
            this.mediaSocket.setReuseAddress(true);
            this.mediaSocket.bind(new InetSocketAddress(UAV_TCP_SERVER_PORT));
        } catch (IOException e) {
        }
        LogUtils.i(this.TAG + " createTcpMediaSocket socket success!");
        this.mediaStream = null;
        this.bThreadRunning = true;
        Thread cmdThread = new Thread(new Runnable() { // from class: com.ihunuo.hnmjpeg.socket.HNTCP.3
            @Override // java.lang.Runnable
            public void run() {
                while (HNTCP.this.bThreadRunning) {
                    LogUtils.i(HNTCP.this.TAG + " createTcpMediaSocket running ....");
                    try {
                        HNTCP.this.clientSocket = HNTCP.this.mediaSocket.accept();
                        String address = HNTCP.this.socket.getRemoteSocketAddress().toString();
                        LogUtils.i(HNTCP.this.TAG + " one client media connected, address:" + address);
                        HNTCP.this.bMediaConnected = true;
                        try {
                            HNTCP.this.mediaStream = HNTCP.this.clientSocket.getInputStream();
                        } catch (IOException e2) {
                        }
                        
                        int len = 0;
                        while (len != -1) {
                            try {
                                len = HNTCP.this.mediaStream.read(HNTCP.this.mediaBuffer);
        
                                processMediaData(HNTCP.this.mediaBuffer, len);
                            } catch (IOException e4) {
                                LogUtils.e(HNTCP.this.TAG + " error read!");
                            }
                        }
                    } catch (IOException e5) {
                        LogUtils.e(HNTCP.this.TAG + " client media connection error, just wait...");
                        try {
                            Thread.sleep(5000L);
                        } catch (InterruptedException e6) {
                        }
                    }
                }
            }
        });
        cmdThread.setName("mediaThread");
        cmdThread.start();
    }
    
    public void setTcp(String addr, int port) {
    	LogUtils.i(this.TAG, "run: setTcp, addr:" + addr + " port:" + port);
        mTcpAddr = addr;
        mTcpPort = port;
    }
    
    private Runnable tcpMediaReceiver = new Runnable() { // from class: com.ihunuo.hnmjpeg.socket.HNTCP.2
    	 @Override // java.lang.Thread, java.lang.Runnable
	    public void run() {
	        
        	LogUtils.i(TAG, "run: create tcpMediaReceiver, addr:" + HNTCP.this.mTcpAddr + " port:" + (mTcpPort + 1));
        	HNTCP.this.mediaStream = null;
            Socket mediaSocket;
            try {
            	mediaSocket = new Socket(mTcpAddr, mTcpPort + 1);
            	mediaSocket.setReceiveBufferSize(MEDIA_BUF_SIZE);
            	HNTCP.this.mediaStream = mediaSocket.getInputStream();
	            //this.read = this.socket.getInputStream();
            	LogUtils.i(TAG, "tcpMediaReceiver running  ........." + mediaSocket.getReceiveBufferSize());
            } catch (IOException e) {
            	LogUtils.e("tcpMediaReceiver error! , e:"+ e.getMessage());
            	return;
            }

            
            HNTCP.this.bThreadRunning = true;
            HNTCP.this.bMediaConnected = true;
            
            LogUtils.i(TAG, "tcpMediaReceiver running  .........");
            int len = 0;
            while (len != -1) {
                try {
                    len = HNTCP.this.mediaStream.read(HNTCP.this.mediaBuffer);

                    processMediaData(HNTCP.this.mediaBuffer, len);
                } catch (IOException e4) {
                    LogUtils.e(HNTCP.this.TAG + " error read!");
                }
            }
    	 }
    };
    
    
   	int i6 = 0;
    int i5 = 0;
    int i4 = 0;
    private void processMediaData(byte[] data, int len) {
    	LogUtils.i(TAG + " thread run: processMediaData len:" + len);
    	if (data[0] == 3) {
            if ((data[3] & 255) == 0) {
                i5 = data[2] & 255;
                try {
                    System.arraycopy(data, 9, HNTCP.this.bArr2, 0, len - 9);
                    i6 = len - 9;
                    i4 = 0;
                    LogUtils.e(TAG + " thread run: arraycopy len:" + i6);
                } catch (Exception e3) {
                	LogUtils.e(TAG + " thread run: exception e:" + e3.getMessage());
                    i4 = 0;
                    i6 = 0;
                    return;
                }
            } else if ((data[3] & 255) == i4 + 1 && i5 == (data[2] & 255)) {
                i4 = data[3] & 255;
                System.arraycopy(data, 9, HNTCP.this.bArr2, i6, len - 9);
                i6 += len - 9;
                LogUtils.e(TAG + " thread run: arraycopy2 len:" + i6);
                if (i6 >= MEDIA_BUF_SIZE) {
                	LogUtils.e(TAG + " thread run: size exceed!");
                	i4 = 0;
                    i6 = 0;
                	return;
                }
            } else {
                LogUtils.e(TAG + " thread run: index error, just ignor!");
                i4 = 0;
                i6 = 0;
            	return;
            }
            if ((data[4] & 255) == i4 + 1 && i5 == (data[2] & 255)) {
            	LogUtils.i(TAG + " thread run: media len:" + i6);
                HNSocketMjpegUDP.getHnSocketMjpegUDP().fengbao(HNTCP.this.bArr2, i6);
            	
                i4 = 0;
                i6 = 0;
            }
        }
    }
}
	                
