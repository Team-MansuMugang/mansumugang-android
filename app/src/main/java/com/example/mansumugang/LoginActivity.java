package com.example.mansumugang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 뷰 요소들을 초기화합니다.
        loginIdEditText = findViewById(R.id.login_id);
        loginPasswordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        // 로그인 버튼 클릭 리스너 설정
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
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
                        String accessToken = loginResponse.getAccessToken();
                        App.prefs.setToken(accessToken); // 액세스 토큰 저장

                        String refreshToken = loginResponse.getRefreshToken();
                        App.prefs.setRefreshToken(refreshToken); // 리프레시 토큰 저장

                        String userType = loginResponse.getUserType();
                        App.prefs.setUserType(userType); // 사용자 유형 저장

                        String savedToken = App.prefs.getToken();
                        System.out.println("저장된 액세스 토큰: " + savedToken);

                        // 스케줄 액티비티로 이동
                        Intent intent = new Intent(LoginActivity.this, ScheduleActivity.class);
                        startActivity(intent);
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
