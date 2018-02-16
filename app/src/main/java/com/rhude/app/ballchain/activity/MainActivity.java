package com.rhude.app.ballchain.activity;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.support.v4.widget.SwipeRefreshLayout;

import com.rhude.app.ballchain.R;
import com.rhude.app.ballchain.value.Constants;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class MainActivity extends AppCompatActivity  {
    private FloatingActionButton fab;
    private WebView webView;
    private WebView mWebView;
    private SwipeRefreshLayout swipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.swipe);
        mWebView = findViewById(R.id.webView);

        setupView();
        setupWebView();

    }




    private void setupView() {
        fab = findViewById(R.id.fab);
        webView = findViewById(R.id.webView);
        fab.setOnClickListener(onFabClicked);
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(mSwipeRefreshLayout);



        // Using a library to fill fab image (SVG format)
        fab.setImageDrawable(MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.PENCIL).setColorResource(android.R.color.white).setSizeDp(24).build());
    }

    private void setupWebView() {
        webView.setWebViewClient(new WebViewClient());
        // Do some fancy caching
        webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        if (!isNetworkAvailable()) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        webView.loadUrl(Constants.endpoint);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return true;
        }

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //<editor-fold desc="Listeners">
    private View.OnClickListener onFabClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            // This is adding the Wordpress Authentication to the network call headers
//            // (only really needs to be added once, it saves the values)
//            AppValues.getInstance().setAuthorization("testuser", "abba123abba");
//
//            // Creating the post object that we are creating
//            Post post = new Post();
//            post.setTitle(String.format("Test - %s", new Date().toString()));
//            post.setContent("This is a message post");

            startActivityForResult(new Intent(MainActivity.this, PostActivity.class), 1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("request", "PostActivity Returned.");
        WebView mWebView;
        mWebView = findViewById(R.id.webView);
        mWebView.reload();
    }


    private SwipeRefreshLayout.OnRefreshListener mSwipeRefreshLayout = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Log.i("refreshing", "Refreshing...");
            webView.reload();
            swipe.setRefreshing(false);

        }
    };

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
            return;
        }

        super.onBackPressed();
    }
    //</editor-fold>
}
