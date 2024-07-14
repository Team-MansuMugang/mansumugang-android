package com.example.mansumugang;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * MainActivity 클래스는 애플리케이션의 메인 화면을 담당합니다.
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 위치 권한 확인 및 요청
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        } else {
            startLocationService();
        }

        // 토큰 확인
        String token = App.prefs.getToken();

        if (token == null || token.isEmpty()) {
            // 토큰이 없으면 로그인 액티비티로 이동
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // 현재 액티비티를 종료하여 백 스택에서 제거
        } else {
            // 토큰이 있으면 로그인 액티비티로 이동
            Intent scheduleIntent = new Intent(MainActivity.this, LoginActivity.class); // 현재 토큰을 제거하지 않고 액티비티를 수정하기 위해 scheduleIntent 대신 로그인으로 이동
            startActivity(scheduleIntent);
            finish(); // 현재 액티비티를 종료하여 백 스택에서 제거
        }
    }

    /**
     * 위치 서비스를 시작합니다.
     */
    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                // 권한 거부됨
            }
        }
    }
}
