package com.rhude.app.ballchain.activity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.rhude.app.ballchain.R;
import com.rhude.app.ballchain.model.Post;
import com.rhude.app.ballchain.network.WordpressApi;
import com.rhude.app.ballchain.value.AppValues;
import com.rhude.app.ballchain.value.Constants;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupView();
        setupWebView();
    }

    private void setupView() {
        fab = findViewById(R.id.fab);
        webView = findViewById(R.id.webView);
        fab.setOnClickListener(onFabClicked);

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
            // This is adding the Wordpress Authentication to the network call headers
            // (only really needs to be added once, it saves the values)
            AppValues.getInstance().setAuthorization("Sean", "1312Blanshard");

            // Creating the post object that we are creating
            Post post = new Post();
            post.setTitle(String.format("Test - %s", new Date().toString()));
            post.setContent("This is a message post");

            //Making the network call
            WordpressApi.getInstance().getService().createPost(post).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    //This block happens if the request was successful
                    String error = WordpressApi.checkResponseForError(response);
                    if (error != null) {
                        onFailure(call, new Throwable(error));
                        return;
                    }
                    Toast.makeText(MainActivity.this, "Created post :)", Toast.LENGTH_SHORT).show();
                    webView.reload();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    //This block happens if the request failed
                    Toast.makeText(MainActivity.this, "Failed to make post :(", Toast.LENGTH_LONG).show();
                }
            });
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
