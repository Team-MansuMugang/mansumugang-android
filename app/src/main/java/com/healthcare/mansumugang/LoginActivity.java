package com.healthcare.mansumugang;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

/**
 * LoginActivity 클래스는 사용자가 로그인할 수 있는 화면을 제공합니다.
 * 사용자는 아이디와 비밀번호를 입력하고, 로그인 버튼을 눌러 로그인 프로세스를 진행합니다.
 * 또한 위치 권한을 요청하고 처리하는 기능도 포함되어 있습니다.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText loginIdEditText; // 로그인 아이디 입력 필드
    private EditText loginPasswordEditText; // 로그인 비밀번호 입력 필드
    private Button loginButton; // 로그인 버튼
    private PermissionSupport permission; // 권한 요청을 지원하는 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI 컴포넌트 초기화
        loginIdEditText = findViewById(R.id.login_id);
        loginPasswordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        // 권한 요청을 지원하는 객체 초기화
        permission = new PermissionSupport(this, this);

        // 권한 확인 및 요청
        permissionCheck();

        // 로그인 버튼 클릭 시 로그인 작업 수행
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    /**
     * 런타임 권한을 확인하고 필요한 경우 요청합니다.
     */
    private void permissionCheck() {
        // 런타임 권한을 체크하고 요청합니다.
        if (!permission.checkPermission()) {
            permission.requestAllPermissions();
        }

        // Android Q 이상에서 백그라운드 위치 권한을 요청합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkAndRequestBackgroundLocationPermission();
        }
    }

    /**
     * 백그라운드 위치 권한이 필요한 경우 확인하고 요청합니다.
     */
    private void checkAndRequestBackgroundLocationPermission() {
        boolean backgroundLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!backgroundLocationGranted) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                showPermissionExplanationDialog(); // 권한 설명 다이얼로그를 표시합니다.
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * 백그라운드 위치 권한을 요청하는 설명 다이얼로그를 표시합니다.
     */
    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this).setTitle("권한 요청").setMessage("앱이 백그라운드에서 위치 정보를 사용하기 위해 권한이 필요합니다. 권한을 허용해 주세요.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE);
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }).create().show();
    }

    /**
     * 앱 설정 화면을 열어 권한을 수동으로 설정할 수 있도록 합니다.
     */
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 권한 요청 결과를 처리합니다.
        if (requestCode == Constants.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 백그라운드 위치 권한이 허용되었을 때
                Toast.makeText(this, "백그라운드 위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                // 백그라운드 위치 권한이 거부되었을 때
                Toast.makeText(this, "백그라운드 위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this).setTitle("권한 필요").setMessage("백그라운드 위치 권한이 필요합니다. 설정에서 권한을 허용해 주세요.").setPositiveButton("설정으로 가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openAppSettings(); // 앱 설정 화면을 엽니다.
                    }
                }).setNegativeButton("취소", null).create().show();
            }
        } else {
            // 다른 권한 요청 결과 처리
            if (!permission.permissionResult(requestCode, permissions, grantResults)) {
                Toast.makeText(this, "필요한 권한이 거부되었습니다. 권한을 허용해야 앱을 사용할 수 있습니다.", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 사용자가 입력한 아이디와 비밀번호로 로그인 시도를 합니다.
     */
    private void performLogin() {
        String username = loginIdEditText.getText().toString();
        String password = loginPasswordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            // 아이디 또는 비밀번호가 비어 있을 때
            Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 로그인 요청을 위한 JSON 객체 생성
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("username", username);
        jsonRequest.addProperty("password", password);
        String jsonString = jsonRequest.toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonString);

        // API 서비스 객체를 통해 로그인 요청
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(requestBody);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    // 로그인 성공
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        handleLoginResponse(loginResponse);
                    } else {
                        showError("로그인 실패: 응답 없음");
                    }
                } else {
                    // 로그인 실패
                    String errorMessage = "로그인 실패: " + response.message();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    showError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showError("로그인 실패: " + t.getMessage());
            }
        });
    }

    /**
     * 로그인 응답을 처리합니다.
     * 사용자 유형에 따라 다른 화면으로 이동하거나 메시지를 표시합니다.
     *
     * @param loginResponse 로그인 응답 객체
     */
    private void handleLoginResponse(LoginResponse loginResponse) {
        if (loginResponse.getUserType().equals("USER_PROTECTOR")) {
            // 보호자 사용자일 때
            loginIdEditText.setText("");
            loginPasswordEditText.setText("");
            Toast.makeText(this, "환자 아이디로 로그인 하세요!", Toast.LENGTH_LONG).show();
        } else {
            // 일반 사용자일 때
            App.prefs.setToken(loginResponse.getAccessToken());
            App.prefs.setRefreshToken(loginResponse.getRefreshToken());

            Intent intent = new Intent(this, ScheduleActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    /**
     * 에러 메시지를 토스트로 표시하고 로그에 기록합니다.
     *
     * @param message 에러 메시지
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e(Constants.LOGIN_ACTIVITY, message);
    }
}
