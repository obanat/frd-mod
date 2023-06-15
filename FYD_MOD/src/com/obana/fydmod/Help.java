package com.obana.fydmod;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/* loaded from: classes.dex */
public class Help extends Activity {

    /* renamed from: a */
    private WebView f173a;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.help);
        this.f173a = (WebView) findViewById(R.id.help_webview);
        this.f173a.setBackgroundColor(0);
        this.f173a.setScrollBarStyle(0);
        this.f173a.loadUrl("file:///android_asset/help-en_us.html");
    }
}
