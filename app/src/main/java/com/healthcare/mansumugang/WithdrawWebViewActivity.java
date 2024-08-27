package com.healthcare.mansumugang;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WithdrawWebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // WebView를 초기화하고 JavaScript를 사용할 수 있도록 설정합니다.
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        // WebViewClient를 설정하여 페이지 로딩 및 URL 변경을 처리합니다.
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);

                // 방문 기록이 업데이트될 때 호출되며, URL이 변경되었는지 확인합니다.
                // 현재 URL이 가입 완료 페이지와 다를 경우, 앱을 종료하고 결과를 반환합니다.
                if (!url.startsWith("https://mansumugang.kr/account/withdraw-patient")) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        // WebViewClient 설정하여 웹 페이지의 웹 관련 이벤트를 처리합니다.
        webView.setWebViewClient(new WebViewClient());

        // 웹뷰에서 로드할 URL을 지정하고 페이지를 로드합니다.
        String url = "https://mansumugang.kr/account/withdraw-patient"; // base url 변경 시 상수등록
        webView.loadUrl(url);
    }
}
