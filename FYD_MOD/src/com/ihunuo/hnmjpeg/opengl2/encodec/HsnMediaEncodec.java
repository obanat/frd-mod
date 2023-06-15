package com.ihunuo.hnmjpeg.opengl2.encodec;

import android.content.Context;
import android.util.Log;

/* loaded from: classes.dex */
public class HsnMediaEncodec extends HsnBaseMediaEncoder {
    private HsnEncodecRender hsnEncodecRender;
    public boolean isContinue;

    public HsnMediaEncodec(Context context, int i) {
        super(context);
        this.isContinue = true;
        HsnEncodecRender hsnEncodecRender = new HsnEncodecRender(context, i);
        this.hsnEncodecRender = hsnEncodecRender;
        setRender(hsnEncodecRender);
        setmRenderMode(0);
        new Thread(new Runnable() { // from class: com.ihunuo.hnmjpeg.opengl2.encodec.HsnMediaEncodec.1
            @Override // java.lang.Runnable
            public void run() {
                while (HsnMediaEncodec.this.isContinue) {
                    try {
                        Thread.sleep(40L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("褰曞埗瑙嗛\ue576", "run: ");
                    HsnMediaEncodec.this.requestRender();
                }
            }
        }).start();
    }
}
