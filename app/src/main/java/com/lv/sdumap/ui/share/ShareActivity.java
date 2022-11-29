package com.lv.sdumap.ui.share;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.lv.sdumap.R;
import com.lv.sdumap.utils.Storage;

/**
 * 分享
 */
public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        WebView myWebView = findViewById(R.id.share_webview);
        Button button = findViewById(R.id.share_button);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.loadUrl(Storage.getSingleton().getString(Storage.KEY_CLOUD_SHARE_QR_URL, "https://lyuxi99.github.io/mysdu/share.html"));
        button.setOnClickListener(v -> {
            Intent share_intent = new Intent();
            share_intent.setAction(Intent.ACTION_SEND);
            share_intent.setType("text/plain");
            share_intent.putExtra(Intent.EXTRA_TEXT, Storage.getSingleton().getString(Storage.KEY_CLOUD_SHARE_TEXT, "MySdu 全新改版，支持课程表、待办事项、校区地图、出入校园信息填报、身份证二维码、图书馆预约、查成绩、考试安排等功能。下载地址：https://lyuxi99.github.io/mysdu/share.html"));
            startActivity(Intent.createChooser(share_intent, "分享"));
        });
    }
}