package com.ezen.haneor.mytreat.nav_item_activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ezen.haneor.mytreat.R;

public class PrivateActivity extends AppCompatActivity {

    WebView webView;
    WebSettings webSettings;
    String url = "https://dodam2018.blogspot.com/2018/09/blog-post.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private);

        Toolbar toolbar = findViewById(R.id.activity_private_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("개인정보 처리 방침");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = findViewById(R.id.web_private);

        webView.setWebViewClient(new WebViewClient());
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(url);

    }

    @Override
    // 메뉴 백버튼(왼쪽) 활성화
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
