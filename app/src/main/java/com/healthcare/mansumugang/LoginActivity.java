package com.healthcare.mansumugang;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

/**
 * LoginActivity 클래스는 로그인 화면을 관리하고 로그인 기능을 수행합니다.
 */
public class LoginActivity extends AppCompatActivity {

    // 뷰 요소들을 정의합니다.
    private EditText loginIdEditText;
    private EditText loginPasswordEditText;
    private Button loginButton;
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_PERMISSION_FINE_LOCATION = 1001;
    private static final int REQUEST_PERMISSION_COARSE_LOCATION = 1002;
    private static final int REQUEST_PERMISSION_BACKGROUND_LOCATION = 1003;
    private static final int REQUEST_PERMISSION_RECORD_AUDIO = 1004;
    private static final int REQUEST_PERMISSION_POST_NOTIFICATIONS = 1005;
    private static final int REQUEST_PERMISSION_READ_STORAGE = 1006;
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 1007;
    private static final int REQUEST_PERMISSION_CAMERA = 1008;
    private static final int REQUEST_PERMISSION_SCHEDULE_EXACT_ALARM = 1009;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 뷰 요소들을 초기화합니다.
        loginIdEditText = findViewById(R.id.login_id);
        loginPasswordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

//        checkAndRequestPermissions();

        // 로그인 버튼 클릭 리스너 설정
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    /**
     * 권한이 허용되었는지 확인하고 필요한 경우 요청합니다.
     */
    private void checkAndRequestPermissions() {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_FINE_LOCATION);
        }
        if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION_COARSE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    REQUEST_PERMISSION_BACKGROUND_LOCATION);
        }
        if (!hasPermission(Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION_RECORD_AUDIO);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_PERMISSION_POST_NOTIFICATIONS);
        }
        if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_STORAGE);
        }
        if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE_STORAGE);
        }
        if (!hasPermission(Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_CAMERA);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !hasPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},
                    REQUEST_PERMISSION_SCHEDULE_EXACT_ALARM);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION_FINE_LOCATION:
                // Handle the result for fine location permission
                checkAndRequestPermissions(); // Continue with the next permission request
                break;
            case REQUEST_PERMISSION_COARSE_LOCATION:
                // Handle the result for coarse location permission
                checkAndRequestPermissions(); // Continue with the next permission request
                break;
            case REQUEST_PERMISSION_BACKGROUND_LOCATION:
                // Handle the result for background location permission
                checkAndRequestPermissions(); // Continue with the next permission request
                break;
            case REQUEST_PERMISSION_RECORD_AUDIO:
                // Handle the result for record audio permission
                checkAndRequestPermissions(); // Continue with the next permission request
                break;
            case REQUEST_PERMISSION_POST_NOTIFICATIONS:
                // Handle the result for post notifications permission
                checkAndRequestPermissions(); // Continue with the next permission request
                break;
            case REQUEST_PERMISSION_READ_STORAGE:
                // Handle the result for read storage permission
                checkAndRequestPermissions(); // Continue with the next permission request
                break;
            case REQUEST_PERMISSION_WRITE_STORAGE:
                // Handle the result for write storage permission
                checkAndRequestPermissions(); // Continue with the next permission request
                break;
            case REQUEST_PERMISSION_CAMERA:
                // Handle the result for camera permission
                checkAndRequestPermissions(); // Continue with the next permission request
                break;
            case REQUEST_PERMISSION_SCHEDULE_EXACT_ALARM:
                // Handle the result for schedule exact alarm permission
                // No further permissions to request
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * performLogin 메서드는 사용자가 입력한 아이디와 비밀번호로 로그인 요청을 수행합니다.
     */
    private void performLogin() {
        String username = loginIdEditText.getText().toString();
        String password = loginPasswordEditText.getText().toString();

        // 아이디와 비밀번호가 비어있는지 확인합니다.
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // JSON 형식으로 변환하여 출력
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("username", username);
        jsonRequest.addProperty("password", password);
        String jsonString = jsonRequest.toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonString);

        // Retrofit을 사용하여 API 호출
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(requestBody);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    // 성공적으로 응답을 받은 경우
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        if (loginResponse.getUserType().equals("USER_PROTECTOR"))
                        {
                            // USER_PROTECTOR인 경우
                            loginIdEditText.setText("");
                            loginPasswordEditText.setText("");
                            Toast.makeText(LoginActivity.this, "환자 아이디로 로그인 하세요!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        String accessToken = loginResponse.getAccessToken();
                        App.prefs.setToken(accessToken); // 액세스 토큰 저장

                        String refreshToken = loginResponse.getRefreshToken();
                        App.prefs.setRefreshToken(refreshToken); // 리프레시 토큰 저장

                        String userType = loginResponse.getUserType();

                        String savedToken = App.prefs.getToken();
                        String RefreshToken = App.prefs.getRefreshToken();

                        System.out.println("저장된 액세스 토큰: " + savedToken);
                        System.out.println("저장된 ref 토큰: " + RefreshToken);
                        // 스케줄 액티비티로 이동
                        Intent intent = new Intent(LoginActivity.this, ScheduleActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish(); // 현재 액티비티를 종료하여 백 스택에서 제거
                    } else {
                        String errorMessage = "로그인 실패: 응답 없음";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, errorMessage + " - 로그인 실패");
                    }
                } else {
                    // 응답이 실패한 경우
                    String errorMessage = "로그인 실패: " + response.message();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, errorMessage + " - 로그인 실패");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // 요청이 실패한 경우
                String errorMessage = "로그인 실패: " + t.getMessage();
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                Log.e(TAG, errorMessage + " - 로그인 실패", t);
            }
        });
    }
}
