package com.example.srikar.askreddit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = WebViewActivity.class.getSimpleName();

    private static final String EXTRA_URL = "EXTRA_URL";
    private static final String EXTRA_TITLE = "EXTRA_TITLE";
    private String titleStr;
    private String urlStr;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webView);
        // Get the Intent Extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            urlStr = extras.getString(EXTRA_URL);
            titleStr = extras.getString(EXTRA_TITLE);
        }
        // Change the Activity Title
        setTitle(titleStr);

        // Change the permanent link to mobile format by adding
        urlStr = "https://m.reddit.com" + urlStr;
        Log.v(TAG, "Url String: " + urlStr);

        // Enabling JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Handling Page Navigation
        webView.setWebViewClient(new WebViewClient());
        // Load the web page
        webView.loadUrl(urlStr);
    }

    // Navigating web page history
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}