package com.healthcare.mansumugang;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private static final String URL_MAIN = Constants.MAIN_URL + "/sign-up/patient";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);

                // URL이 메인 URL에서 벗어날 경우 액티비티 종료
                if (!url.startsWith(URL_MAIN)) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        webView.loadUrl(URL_MAIN);
    }
}