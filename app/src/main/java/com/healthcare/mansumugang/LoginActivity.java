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

public class LoginActivity extends AppCompatActivity {

    private EditText loginIdEditText;
    private EditText loginPasswordEditText;
    private Button loginButton;
    private static final String TAG = "LoginActivity";
    private PermissionSupport permission;

    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginIdEditText = findViewById(R.id.login_id);
        loginPasswordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        permission = new PermissionSupport(this, this);

        permissionCheck();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void permissionCheck() {
        // Check and request runtime permissions
        if (!permission.checkPermission()) {
            permission.requestAllPermissions();
        }

        // Check and request background location permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkAndRequestBackgroundLocationPermission();
        }
    }

    private void checkAndRequestBackgroundLocationPermission() {
        boolean backgroundLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!backgroundLocationGranted) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                showPermissionExplanationDialog();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("권한 요청")
                .setMessage("앱이 백그라운드에서 위치 정보를 사용하기 위해 권한이 필요합니다. 권한을 허용해 주세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(
                                LoginActivity.this,
                                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                        );
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Background location permission granted
                Toast.makeText(this, "백그라운드 위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                // Background location permission denied
                Toast.makeText(this, "백그라운드 위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this)
                        .setTitle("권한 필요")
                        .setMessage("백그라운드 위치 권한이 필요합니다. 설정에서 권한을 허용해 주세요.")
                        .setPositiveButton("설정으로 가기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openAppSettings();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .create()
                        .show();
            }
        } else {
            // Handle other permission results
            if (!permission.permissionResult(requestCode, permissions, grantResults)) {
                Toast.makeText(this, "필요한 권한이 거부되었습니다. 권한을 허용해야 앱을 사용할 수 있습니다.", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void performLogin() {
        String username = loginIdEditText.getText().toString();
        String password = loginPasswordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("username", username);
        jsonRequest.addProperty("password", password);
        String jsonString = jsonRequest.toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonString);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(requestBody);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        handleLoginResponse(loginResponse);
                    } else {
                        showError("로그인 실패: 응답 없음");
                    }
                } else {
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

    private void handleLoginResponse(LoginResponse loginResponse) {
        if (loginResponse.getUserType().equals("USER_PROTECTOR")) {
            loginIdEditText.setText("");
            loginPasswordEditText.setText("");
            Toast.makeText(this, "환자 아이디로 로그인 하세요!", Toast.LENGTH_LONG).show();
        } else {
            App.prefs.setToken(loginResponse.getAccessToken());
            App.prefs.setRefreshToken(loginResponse.getRefreshToken());

            Intent intent = new Intent(this, ScheduleActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, message);
    }
}
