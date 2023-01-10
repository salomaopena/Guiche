package com.fenixinnovation.guiche;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class WebActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener{

    private String BASE_URL = "https://www.guichegpn.gov.ao/";
    private WebView mWebView;
    private SwipeRefreshLayout refreshLayout;
    private FrameLayout frameLayout;
    private ProgressBar progressBar;
    private RelativeLayout noConnection, mainLayout;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mWebView = findViewById(R.id.webview);
        refreshLayout = findViewById(R.id.swipe);
        progressBar = findViewById(R.id.progressBar);
        frameLayout = findViewById(R.id.frameLayout);
        noConnection = findViewById(R.id.no_conection);
        mainLayout = findViewById(R.id.main_layout);
        updateButton = findViewById(R.id.bt_update);

        if (!haveNetworkConnection()) {
            noConnection.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        } else {
            noConnection.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            getUrlWebView();
        }

        updateButton.setOnClickListener(view -> {
            noConnection.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            if (!haveNetworkConnection()) {
                noConnection.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
            } else {
                noConnection.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
                getUrlWebView();
            }
        });
    }

    private void getUrlWebView() {

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                refreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                BASE_URL = url;
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    frameLayout.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                    setTitle(view.getTitle());
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.getDisplayZoomControls();
        webSettings.getCacheMode();
        webSettings.getLoadWithOverviewMode();
        webSettings.setAppCacheEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.supportZoom();


        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setGeolocationEnabled(true);

       WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        mWebView.setWebViewClient(webViewClient);
        mWebView.getProgress();
        mWebView.pageDown(true);
        mWebView.computeScroll();

        refreshLayout.setOnRefreshListener(this);
        mWebView.loadUrl(BASE_URL);

        progressBar.setProgress(0);

    }

    public class WebViewClientImpl extends WebViewClient {

        private Activity activity = null;

        public WebViewClientImpl(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            if (url.indexOf(BASE_URL) > -1) {
                return false;
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(intent);
                return true;
            }
        }

    }

    @Override
    public void onRefresh() {
        if (!haveNetworkConnection()) {
            noConnection.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
            refreshLayout.setRefreshing(false);
        } else {
            noConnection.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(true);
            mWebView.loadUrl(BASE_URL);
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onKeyDown(int keyEvent, KeyEvent event) {
        if ((keyEvent == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyEvent, event);
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectionWifi = false;
        boolean haveConnectionMobile = false;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (NetworkInfo ni : networkInfos) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected())
                    haveConnectionWifi = true;
            }

            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected())
                    haveConnectionMobile = true;
            }
        }
        return haveConnectionWifi || haveConnectionMobile;
    }



    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, WebActivity.class);
        return intent;
    }
}