package com.kiosk.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class KioskActivity extends Activity {
    private float startX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        WebView webView = findViewById(R.id.webview);
        View escapeHatch = findViewById(R.id.escape_hatch);

        // 1. Create the overlay
        FrameLayout overlay = new FrameLayout(this);
        addContentView(overlay, new ViewGroup.LayoutParams(-1, -1));

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://192.168.13.50/ui/dashboard");

        escapeHatch.setOnLongClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
            return true;
        });

        // 2. Attach the listener to the correctly defined overlay
        overlay.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startX = event.getX();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                float dx = event.getX() - startX;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                
                if (dx > 150) { // Swipe Right to Hide
                    webView.evaluateJavascript("document.activeElement.blur();", null);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                } else if (Math.abs(dx) < 20) { // Tap to Show
                    webView.requestFocus();
                    webView.evaluateJavascript("document.body.focus();", null);
                    imm.showSoftInput(webView, InputMethodManager.SHOW_FORCED);
                }
            }
            return false;
        });
    }
}