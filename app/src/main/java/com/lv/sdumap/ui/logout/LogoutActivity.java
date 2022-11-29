package com.lv.sdumap.ui.logout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lv.sdumap.R;
import com.lv.sdumap.utils.Storage;

/**
 * 退出登录
 */
public class LogoutActivity extends AppCompatActivity {
    WebView myWebView;
    private final Handler emptyHandler = new Handler();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        myWebView = findViewById(R.id.login_webview);
        Toast.makeText(this, "正在退出登录...", Toast.LENGTH_LONG).show();
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView, true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().removeSessionCookies(null);
                CookieManager.getInstance().flush();
                return false;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().removeSessionCookies(null);
                CookieManager.getInstance().flush();
                return null;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().removeSessionCookies(null);
                CookieManager.getInstance().flush();
                Toast.makeText(LogoutActivity.this, "已退出登录!", Toast.LENGTH_SHORT).show();
                emptyHandler.postDelayed(()->finish(), 200);
            }
        });
        // 清除所有数据
        myWebView.clearHistory();
        myWebView.clearFormData();
        myWebView.clearCache(true);

        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().removeSessionCookies(null);
        CookieManager.getInstance().flush();
        myWebView.getSettings().setBlockNetworkImage(true);
        myWebView.loadUrl("https://pass.sdu.edu.cn/cas/login");
        Storage.getSingleton().remove(Storage.KEY_USER_NAME);
        Storage.getSingleton().remove(Storage.KEY_USER_PASSWORD);
    }
}