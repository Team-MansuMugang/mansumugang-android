package com.example.mansumugang;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity 클래스는 애플리케이션의 메인 화면을 담당합니다.
 * 현재는 로그인 화면으로 바로 이동하는 기능을 수행합니다.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MainActivity에서 LoginActivity로 이동하는 인텐트 생성 및 시작
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
