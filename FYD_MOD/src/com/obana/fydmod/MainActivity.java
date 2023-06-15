package com.obana.fydmod;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.obana.fydmod.R;

import com.ihunuo.hnmjpeg.Listener.HNMjpegListener;
import com.ihunuo.hnmjpeg.device.HnDevice;
import com.ihunuo.hnmjpeg.opengl2.imgvideo.HsnImgVideoView;

/* loaded from: classes.dex */
public class MainActivity extends Activity implements View.OnTouchListener {

    public static final int GET_STATUS_DELAY_MS = 5000;
    public static final int MOVE_DELAY_MS = 300;
  
    private WifiCarController controller = null;
    
    public static final int CAMERA_MODE_DEFAULT = 0x01;
    public static final int CAMERA_MODE_JJRC = 0x02;
    public static final int CAMERA_MODE_FYD_UAV = 0x03;
    public static int mCameraMode = CAMERA_MODE_FYD_UAV;

    ImageView settingsView;

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        LogUtils.i("onCreate");
        super.onCreate(bundle);

        //setContentView(R.layout.main);
        getWindow().addFlags(4096);
        
        this.controller = new WifiCarController(this);
 
        LayoutInflater mInflater = LayoutInflater.from(this);

        View contrilView = mInflater.inflate(R.layout.main, (ViewGroup) null);
        addContentView(contrilView, new ViewGroup.LayoutParams(-1, -1));
        
        View overView = mInflater.inflate(R.layout.controller, (ViewGroup) null);
        addContentView(overView, new ViewGroup.LayoutParams(-1, -1));   
        controller.init(findViewById(R.id.joystickView));

        CommUtil.getInstance().init(this);
     
        //initUAV();
        settingsView = (ImageView) findViewById(R.id.setting_button);
        settingsView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //start settings
                startActivity(new Intent(MainActivity.this, Settings.class));
            }
            
        });
    }


    

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override // android.app.Activity
    public void onDestroy() {
    	LogUtils.i("onDestroy");
        CommUtil.getInstance().onDestroy();
        super.onDestroy();
    }

  

    @Override // android.app.Activity
    public void onPause() {
        LogUtils.i("onPause");
        CommUtil.getInstance().onPause();
       
        super.onPause();
    }

    @Override // android.app.Activity
    public void onResume() {
        CommUtil.getInstance().onResume();
        super.onResume();
        LogUtils.i("onResume");
    }
    
  


    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
   
   
    
    
    long lastPressTime = 0;
    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.i("onKeyDown keycode=" + keyCode);
        if (keyCode == 4) {//back key
            long pressTime = System.currentTimeMillis();
            if (pressTime - this.lastPressTime <= 1200) {
                this.finish();
                System.gc();
                System.exit(0);
            } else {
                Toast.makeText(this, R.string.click_again_to_exit_the_program, Toast.LENGTH_LONG).show();
            }
            this.lastPressTime = pressTime;
            return true;
        }
        
        return super.onKeyDown(keyCode, event);
    }

}
