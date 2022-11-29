package com.lv.sdumap.utils;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.text.StringEscapeUtils;

/**
 * 自动化的 WebView
 * 使用 AutoProcedure 定义一个自动化流程，必要时将用户介入登录
 */
public class AutomatedWebView extends WebView {

    public static final String URL_LOGIN_PREFIX = "https://pass.sdu.edu.cn/cas/login";

    String username = "";
    String password = "";
    AutoProcedure autoProcedure = null;
    private boolean hasTriedLogin = false;
    private boolean isInLoginIntervention = false; // 是否正在以用户介入的方式进行登录
    private final Handler emptyHandler = new Handler();

    public AutomatedWebView(@NonNull Context context) {
        super(context);
    }

    public AutomatedWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutomatedWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutomatedWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 初始化 webview
     */
    public void init() {
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                emptyHandler.post(new MyRunnable());
                // 如果遇到期待的 url 则跳转
                if (autoProcedure.waitForUrl != null && request.getUrl().toString().startsWith(autoProcedure.waitForUrl)) {
                    if (isInLoginIntervention) {
                        Toast.makeText(getContext(), "登录成功。", Toast.LENGTH_SHORT).show();
                        Storage.getSingleton().setString(Storage.KEY_USER_NAME, username);
                        Storage.getSingleton().setString(Storage.KEY_USER_PASSWORD, password);
                        if (autoProcedure.callbackLoginInterventionEnd != null)
                            autoProcedure.callbackLoginInterventionEnd.callback(request.getUrl().toString());
                        if (autoProcedure.loadingCover != null)
                            autoProcedure.loadingCover.setVisibility(View.VISIBLE);
                        if (autoProcedure.hideWebViewWhenLoading)
                            AutomatedWebView.this.setVisibility(View.INVISIBLE);
                    }
                    view.loadUrl(autoProcedure.targetUrl);
                    return true;
                }
                return false;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                emptyHandler.post(new MyRunnable());
                if (request.getUrl().toString().startsWith("https://scenter.sdu.edu.cn/tp_fp/formParser?status=select")) {
                    // 出校申请表单开始加载，尝试移除遮罩层
                    emptyHandler.post(() -> {
                        for (String script : new String[]{
                                "document.getElementById(\"layui-layer-shade1\").remove();",
                                "document.getElementById(\"layui-layer1\").remove();",
                        }) {
                            view.evaluateJavascript("javascript:" + script, null);
                        }
                    });
                }
                if (request.getUrl().toString().startsWith("https://scenter.sdu.edu.cn/tp_fp/formParser?status=codeList")) {
                    // 出校申请表单加载完成，帮助用户自动点一些选项
                    emptyHandler.postDelayed(() -> {
                        for (String script : new String[]{
                                "document.getElementById(\"formIframe\").contentWindow.document.querySelector(\"#wclx_vant > div > div:nth-child(1)\").click();",
                                "document.getElementById(\"formIframe\").contentWindow.document.querySelector(\"#wcsy_vant > div > div:nth-child(6)\").click();",
                                "document.getElementById(\"formIframe\").contentWindow.document.querySelector(\"#wcmdd_vant > div > div:nth-child(1)\").click();"
                        }) {
                            view.evaluateJavascript("javascript:" + script, null);
                        }
                    }, 100);
                }
                return null;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                emptyHandler.post(new MyRunnable());

                if (url.startsWith(autoProcedure.targetUrl)) {
                    Storage.getSingleton().setString(Storage.KEY_USER_NAME, username);
                    Storage.getSingleton().setString(Storage.KEY_USER_PASSWORD, password);
                    if (autoProcedure.loadingCover != null)
                        autoProcedure.loadingCover.setVisibility(View.INVISIBLE);

                    if (autoProcedure.callbackTarget != null) {
                        emptyHandler.post(() -> {
                            view.evaluateJavascript("javascript:window.java_obj.showSource(document.getElementsByTagName('html')[0].innerHTML);",
                                    value -> {
                                        emptyHandler.post(() -> loadUrl("file:///android_asset/misc/empty.html"));
                                    });
                        });
                    }
                }
                if (url.startsWith(URL_LOGIN_PREFIX)) {
                    if (!username.isEmpty() && !hasTriedLogin) {
                        // try auto login
                        hasTriedLogin = true;
                        String script = "javascript:{" +
                                "document.getElementById('un').value=\"" +
                                StringEscapeUtils.ESCAPE_ECMASCRIPT.translate(username) +
                                "\";" +
                                "document.getElementById('pd').value=\"" +
                                StringEscapeUtils.ESCAPE_ECMASCRIPT.translate(password) +
                                "\";" +
                                "document.getElementsByClassName(\"login_box_checkbox\")[0].click();" +
                                "document.getElementById(\"index_login_btn\").click();}";
                        view.evaluateJavascript(script, null);
                    } else {
                        hasTriedLogin = true;
                        if (autoProcedure.loadingCover != null)
                            autoProcedure.loadingCover.setVisibility(View.INVISIBLE);
                        if (autoProcedure.hideWebViewWhenLoading)
                            AutomatedWebView.this.setVisibility(View.VISIBLE);
                        if (autoProcedure.callbackLoginInterventionBegin != null)
                            autoProcedure.callbackLoginInterventionBegin.callback(url);
                        Toast.makeText(getContext(), "请登录。", Toast.LENGTH_LONG).show();
                        isInLoginIntervention = true;
                    }
                }
            }
        });
    }

    /**
     * 允许缩放
     */
    public void allowZoom() {
        WebSettings webSettings = getSettings();
        // 设置可以支持缩放
        webSettings.setUseWideViewPort(true);
        //设置可以支持缩放
        webSettings.setSupportZoom(true);
        //设置出现缩放工具
        webSettings.setBuiltInZoomControls(true);
        //设定缩放控件隐藏
        webSettings.setDisplayZoomControls(false);
    }

    /**
     * 启动 automated procedure
     * @param autoProcedure automated procedure
     */
    public void open(AutoProcedure autoProcedure) {
        this.username = Storage.getSingleton().getString(Storage.KEY_USER_NAME, "");
        this.password = Storage.getSingleton().getString(Storage.KEY_USER_PASSWORD, "");
        this.autoProcedure = autoProcedure;
        this.hasTriedLogin = false;
        this.isInLoginIntervention = false;
        if (autoProcedure.loadingCover != null)
            autoProcedure.loadingCover.setVisibility(View.VISIBLE);
        if (autoProcedure.hideWebViewWhenLoading) this.setVisibility(View.INVISIBLE);

        this.loadUrl(autoProcedure.firstOpenUrl);
    }

    public interface Callback {
        void callback(String str);
    }

    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            autoProcedure.callbackTarget.callback(html);
        }
    }

    /**
     * 获取用户名和密码，用于自动登录
     */
    class MyRunnable implements Runnable {

        private String stripQuote(String s) {
            return s.substring(1, s.length() - 1);
        }

        @Override
        public void run() {
            if (getUrl().startsWith("https://pass.sdu.edu.cn/")) {
                evaluateJavascript("javascript:document.getElementById('un').value", value -> {
                    if (!"null".equals(value)) {
                        value = stripQuote(value);
                        if (!value.isEmpty())
                            username = StringEscapeUtils.unescapeEcmaScript(value);
                    }
                });
                evaluateJavascript("javascript:document.getElementById('pd').value", value -> {
                    if (!"null".equals(value)) {
                        value = stripQuote(value);
                        if (!value.isEmpty())
                            password = StringEscapeUtils.unescapeEcmaScript(value);
                    }
                });
            }

        }
    }

    /**
     * Automated procedure 定义一个自动化流程
     */
    public static class AutoProcedure {
        @Nullable
        View loadingCover;
        boolean hideWebViewWhenLoading;
        String firstOpenUrl; // open this url
        @Nullable
        String waitForUrl; // wait until shouldOverrideUrlLoading captures this url
        String targetUrl; // override this above url to this target
        @Nullable
        Callback callbackLoginInterventionBegin; // call this when login intervention begins
        @Nullable
        Callback callbackLoginInterventionEnd; // call this when login intervention is over
        @Nullable
        Callback callbackTarget; // pass the html source to this callbackTarget

        public AutoProcedure(@Nullable View loadingCover, boolean hideWebViewWhenLoading, String firstOpenUrl, @Nullable String waitForUrl, String targetUrl, @Nullable Callback callbackLoginInterventionBegin, @Nullable Callback callbackLoginInterventionEnd, @Nullable Callback callbackTarget) {
            this.loadingCover = loadingCover;
            this.hideWebViewWhenLoading = hideWebViewWhenLoading;
            this.firstOpenUrl = firstOpenUrl;
            this.waitForUrl = waitForUrl;
            this.targetUrl = targetUrl;
            this.callbackLoginInterventionBegin = callbackLoginInterventionBegin;
            this.callbackLoginInterventionEnd = callbackLoginInterventionEnd;
            this.callbackTarget = callbackTarget;
        }
    }

}
