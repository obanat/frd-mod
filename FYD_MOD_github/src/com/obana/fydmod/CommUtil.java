package com.obana.fydmod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;

import com.ihunuo.hnmjpeg.Listener.HNMjpegListener;
import com.ihunuo.hnmjpeg.device.HnDevice;
import com.ihunuo.hnmjpeg.opengl2.imgvideo.HsnImgVideoView;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

public class CommUtil {


    
    public static final int FYD_LOCAL_UDP_PORT = 5555;
    public static final int FYD_LOCAL_TCP_PORT = 5252;
    public static final String FYD_LOCAL_ADDR = "192.168.1.1";
    public static final int FYD_REMOTE_TCP_PORT = 28001;//this port is use for media data
    public static final int TCP_CHECK_PORT = 28000;//this port is use for check connectivity   
    
    private static final int MSG_CHECK_CONNECTIVITY_FINISH = 0x1001; 
    
    private static final int PLAY_MODE_LOCAL = 0x00; 
    private static final int PLAY_MODE_REMOTE = 0x10; 
    
    private static CommUtil instance;
    
    private String tcpAddr = FYD_LOCAL_ADDR;
    private int tcpPort = FYD_LOCAL_TCP_PORT;

    private Activity mActivity;
    private int mPlayMode = PLAY_MODE_LOCAL;
    
    //uav
    private HnDevice hnDevice;
    private HNListener hnListener;
    private HsnImgVideoView hnPlayer;
    
    public static CommUtil getInstance() {
        if (instance == null ) instance = new CommUtil();
        return instance;
    }

    public CommUtil() {
    
    }
    
    public void init (Activity ctx) {
        //just do nothing
        LogUtils.i("CommUtil init");

        mActivity = ctx;
        this.mHandler.post(this.checkAP);
    }
    

    Runnable checkAP = new Runnable() { // from class: com.vison.baselibrary.base.BaseApplication.3
        @Override // java.lang.Runnable
        public void run() {
            
             WifiManager wifiManager = (WifiManager) mActivity.getSystemService("wifi");
             DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

             int intAddr = dhcpInfo.gateway;
             String wifiDhcpAddr = long2ip(intAddr);

             if (intAddr != 0) {
                 LogUtils.i("checkAP run in local mode, addr:" + wifiDhcpAddr);      
                 mPlayMode = PLAY_MODE_LOCAL;
             } else {
            	 String ip = getIpv6HostName();
                 if (ip != null && ip.length() > 4) {
                     tcpAddr = ip;      
                     tcpPort = FYD_REMOTE_TCP_PORT;
                     mPlayMode = PLAY_MODE_REMOTE;
                     LogUtils.i("checkAP, run in ipv6 mode, ip:" + ip);
                 }
             } 

             Message msg = new Message();
             msg.what = MSG_CHECK_CONNECTIVITY_FINISH;
             mHandler.dispatchMessage(msg);
        }
    };
    private String long2ip(int i) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(String.valueOf(i & 255));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((i >> 8) & 255));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((i >> 16) & 255));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((i >> 24) & 255));
        return stringBuffer.toString();
    }

    
    public String getIpv6HostName() {
        String targetHost = null;
        Socket socket = null;
        
        String mac = getSharedPreference("mac","1122334455");
        String getUrl = String.format("http://obana.f3322.org:38086/wificar/getClientIp?mac=%s", mac);

        
        
        String ipaddr = getURLContent(getUrl);
        
        LogUtils.i("getIpv6HostName: v6 addr:" + ipaddr);
        if (ipaddr != null && ipaddr.length() > 8){
            //socket = tryCreateSocket(ipaddr, TCP_CHECK_PORT);

            //if (socket != null && socket.isConnected()){
                targetHost = ipaddr;

                LogUtils.i("tryCreateSocket: success! use remote ipv6 control");
            //}
            try {
                if (socket != null) socket.close();
            } catch (Exception e) {
                LogUtils.i("failed to getURLContent: error:" + e.getMessage());
            }
        }
        return targetHost;
    }
    
    private static String getURLContent(String url) {
        StringBuffer sb = new StringBuffer();
        LogUtils.i("getURLContent:" +url);
        try {
            URL updateURL = new URL(url);
            URLConnection conn = updateURL.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));
            while (true) {
                String s = rd.readLine();
                if (s == null) {
                    break;
                }
                sb.append(s);
            }
        } catch (Exception e){

        }
        return sb.toString();
    }
    

    
    private static Socket tryCreateSocket(String targetHost, int targetPort)  {
        Socket socket = null;
        try {
            LogUtils.i("tryCreateSocket ------->:" + targetHost + ":" + targetPort);
            //socket = new Socket(targetHost, targetPort);
            socket = new Socket();
            SocketAddress remoteAddr = new InetSocketAddress(targetHost, targetPort);
            socket.connect(remoteAddr, 1000);
        } catch (IOException e) {
            LogUtils.e("tryCreateSocket error:" + e.getMessage());
            return null;
        } finally {
            
        }
        LogUtils.i("tryCreateSocket success!------->:" + targetHost);
        return socket;
    }
    
    private Handler mHandler = new Handler() { // from class: com.vison.baselibrary.base.CommUtil.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
            	case MSG_CHECK_CONNECTIVITY_FINISH:
            		LogUtils.i("play mode:" + ((mPlayMode == PLAY_MODE_LOCAL) ? "local" :"remote"));
            		initUAV();
            	break;
                default:
                    return;
            }
        }
    };
    
    private String getSharedPreference(String key, String defaultValue) {
        //return AndRovio.getSharedPreference(key);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
        return sp.getString(key, defaultValue);
    }

    private void initUAV() {
        
        this.hnListener = new HNListener(mActivity, null);
        HnDevice hnDevice = new HnDevice(mActivity);
        this.hnDevice = hnDevice;
        hnDevice.startJLVideo(2);
        this.hnDevice.setBufferNum(0);

        this.hnPlayer = (HsnImgVideoView) mActivity.findViewById(R.id.hnplayer);
        this.hnDevice.start(this.hnPlayer, this.hnListener, tcpAddr, tcpPort);
    }
    
    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class HNListener implements HNMjpegListener {
        @Override // com.ihunuo.hnmjpeg.Listener.HNMjpegListener
        public void decodeYuvSusscced(byte[] bArr, int i, int i2) {
        }

        @Override // com.ihunuo.hnmjpeg.Listener.HNMjpegListener
        public void revDataHNSocketUDP(byte[] bArr, int i) {
        }

        @Override // com.ihunuo.hnmjpeg.Listener.HNMjpegListener
        public void takePhotoSuccssed(Bitmap bitmap) {
        }

        private HNListener() {
        }

        /* synthetic */ 
        HNListener(Context ctx, HNListener hNListener) {
            this();
        }

        @Override // com.ihunuo.hnmjpeg.Listener.HNMjpegListener
        public void videoDecodeSuccssed() {
            LogUtils.i("videoDecodeSuccssed: ");
        }

        @Override // com.ihunuo.hnmjpeg.Listener.HNMjpegListener
        public void revDataHNSocketTCP(int i) {
            LogUtils.i("revDataHNSocketTCP: i:" + i);
        }

        @Override // com.ihunuo.hnmjpeg.Listener.HNMjpegListener
        public void createHNTCPSusscced(int i) {
            hnDevice.hnTcpSend(8);
            hnDevice.hnTcpSend(10);
        }
    }
    
    public void onDestroy() {
        HnDevice hnDevice = this.hnDevice;
        if (hnDevice != null) {
            hnDevice.release();
        }
    }

    public void onPause() {
        HnDevice hnDevice = this.hnDevice;
        if (hnDevice != null) {
            hnDevice.Pause();
        }
    }

    public void onResume() {
        HnDevice hnDevice = this.hnDevice;
        if (hnDevice != null) {
            hnDevice.Resume();
        }
    }
    
}
