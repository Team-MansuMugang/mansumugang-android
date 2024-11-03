package com.healthcare.mansumugang;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity extends AppCompatActivity {
    private MediaRecorder mediaRecorder; // MediaRecorder 인스턴스
    private boolean isRecording = false; // 녹음 상태 플래그
    private String filePath = ""; // 녹음 파일 경로
    private File audioFile; // 녹음 파일 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording); // 레이아웃 설정

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.recording_bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView, this);

        // 오디오 권한 체크 및 요청
        if (!hasPermissions()) {
            requestAudioPermissions();
        }

        // 녹음 버튼 초기화
        initializeRecordingButtons();
    }

    // 권한이 있는지 확인하는 메소드
    private boolean hasPermissions() {
        for (String permission : Constants.RECORD_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false; // 권한이 없으면 false 반환
            }
        }
        return true; // 모든 권한이 있으면 true 반환
    }

    // 오디오 권한 요청 메소드
    private void requestAudioPermissions() {
        ActivityCompat.requestPermissions(this, Constants.RECORD_PERMISSIONS, Constants.REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeRecordingButtons(); // 권한이 허용되면 버튼 초기화
            } else {
                Toast.makeText(this, "Permissions are required to record audio", Toast.LENGTH_SHORT).show();
                finish(); // 권한이 거부되면 액티비티 종료
            }
        }
    }

    /**
     * 녹음 버튼들을 초기화하는 메소드입니다.
     */
    private void initializeRecordingButtons() {
        Button recordButton = findViewById(R.id.recording_start_button); // 녹음 시작 버튼
        ImageButton recordImageButton = findViewById(R.id.recording_button); // 이미지 버튼 (녹음 시작)
        Button sendButton = findViewById(R.id.recording_save_button); // 녹음 저장 버튼
        Button cancelButton = findViewById(R.id.recording_cancel_button); // 녹음 취소 버튼

        // 녹음 시작 버튼 클릭 리스너 설정
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording(); // 녹음 시작
                }
            }
        });

        // 이미지 버튼 클릭 리스너 설정
        recordImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording(); // 녹음 시작
                }
            }
        });

        // 녹음 중지 버튼 클릭 리스너 설정
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecording(); // 녹음 중지
                    convertAudio(); // 오디오 파일 변환
                }
            }
        });

        // 녹음 취소 버튼 클릭 리스너 설정
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    cancelRecording(); // 녹음 취소
                }
            }
        });

        updateLimit();

    }

    private void updateLimit(){
        // API를 호출하여 잔여 녹음 송신 횟수를 가져옴
        TextView limitCheckTextView = findViewById(R.id.save_limit); // 잔여 횟수 텍스트

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = App.prefs.getToken(); // 저장된 토큰을 가져옴
        Call<ResponseBody> call = apiService.getSaveAudioLimit("Bearer " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 성공적으로 응답을 받았을 때
                    try {
                        String responseBody = response.body().string();
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(responseBody).getAsJsonObject();

                        // 서버에서 받아온 남은 송신 횟수 정보를 UI에 표시
                        String message = "잔여 송신 횟수 : ";
                        message += jsonObject.has("remainingRecordingCount") ? jsonObject.get("remainingRecordingCount").getAsString() : "No message";
                        limitCheckTextView.setText(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    // 토큰이 만료된 경우 로그
                    Log.d(Constants.RECORD_ACTIVITY, "Token may be expired. Refreshing token.");
                } else {
                    // 오류 처리
                    String errorMessage = "알 수 없는 오류";
                    if (response.errorBody() != null) {
                        try {
                            // 오류 메시지를 가져와서 로그
                            String errorBody = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(errorBody).getAsJsonObject();
                            errorMessage = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "서버 오류";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // 오류 메시지를 토스트로 표시
                    Toast.makeText(RecordActivity.this, "오류 발생: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * 녹음을 시작하는 메소드입니다.
     *
     * @return 녹음 파일 경로
     */
    private String startRecording() {
        try {
            Button recordButton = findViewById(R.id.recording_start_button);
            LinearLayout recordingButtonBox = findViewById(R.id.recording_button_box);
            Chronometer chronometer = findViewById(R.id.recording_time);

            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start(); // 크로노미터 시작

            recordButton.setVisibility(View.GONE);
            recordingButtonBox.setVisibility(View.VISIBLE);

            filePath = setupMediaRecorder(); // MediaRecorder 설정

            mediaRecorder.prepare();
            mediaRecorder.start(); // 녹음 시작
            isRecording = true;
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

                    // 20분(1200초)에 해당하는 시간이 경과하면 녹음 중지 및 변환
                    if (elapsedMillis >= 20 * 60 * 1000) {  // 20분 = 20 * 60 * 1000 밀리초
                        if (isRecording) {
                            stopRecording();
                            convertAudio();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace(); // 예외 처리
        }
        return filePath; // 녹음 파일 경로 반환
    }

    /**
     * MediaRecorder를 설정하는 메소드입니다.
     *
     * @return 녹음 파일 경로
     * @throws IOException 입출력 예외
     */
    private String setupMediaRecorder() throws IOException {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 마이크 소스 설정
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // 출력 포맷 설정
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); // 오디오 인코더 설정
        mediaRecorder.setAudioEncodingBitRate(48000); // 비트레이트 설정
        mediaRecorder.setAudioSamplingRate(48000); // 샘플링 레이트 설정

        String filePath = createFilePath() + createFileName() + ".3gp"; // 파일 경로 생성
        System.out.println(filePath);
        mediaRecorder.setOutputFile(filePath); // 출력 파일 설정
        audioFile = new File(filePath); // 파일 객체 생성
        return filePath; // 파일 경로 반환
    }

    /**
     * 파일 경로를 생성하는 메소드입니다.
     *
     * @return 파일 경로
     */
    private String createFilePath() {
        return getCacheDir().getAbsolutePath() + "/"; // 캐시 디렉토리 경로 반환
    }

    /**
     * 현재 시간을 기반으로 파일 이름을 생성하는 메소드입니다.
     *
     * @return 파일 이름
     */
    private String createFileName() {
        return "REC_" + System.currentTimeMillis(); // 현재 시간을 기반으로 파일 이름 생성
    }

    /**
     * 녹음을 중지하고 MediaRecorder 리소스를 해제하는 메소드입니다.
     */
    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                Button recordButton = findViewById(R.id.recording_start_button);
                LinearLayout recordingButtonBox = findViewById(R.id.recording_button_box);
                Chronometer chronometer = findViewById(R.id.recording_time);

                chronometer.stop(); // 크로노미터 중지
                chronometer.setBase(SystemClock.elapsedRealtime());
                recordButton.setVisibility(View.VISIBLE);
                recordingButtonBox.setVisibility(View.GONE);

                mediaRecorder.stop(); // 녹음 중지
            } catch (RuntimeException e) {
                e.printStackTrace(); // 예외 처리
            } finally {
                mediaRecorder.reset(); // MediaRecorder 리셋
                mediaRecorder.release(); // MediaRecorder 해제
                mediaRecorder = null;
                isRecording = false;
            }
        }
    }

    private void cancelRecording() {
        stopRecording(); // 녹음 중지
        if (audioFile.exists() && audioFile.delete()) { // 파일 존재 시 삭제
            Toast.makeText(this, "Recording cancelled and file deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void convertAudio() {
        String inputPath = audioFile.getAbsolutePath(); // 3GP 파일 경로
        String outputPath = inputPath.replace(".3gp", ".mp3"); // 변환될 MP3 파일 경로

        // AudioConverter를 사용하여 3GP 파일을 MP3로 변환
        AudioConverter converter = new AudioConverter();
        converter.convert3gpToMp3(inputPath, outputPath, new AudioConverter.ConversionCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // 성공적으로 변환된 파일을 사용할 수 있습니다.
                String token = App.prefs.getToken();
                System.out.println("Conversion successful: " + convertedFile.getAbsolutePath());
                audioFile.delete(); // 원본 파일 삭제
                uploadFile(token, convertedFile); // 파일 업로드
            }

            @Override
            public void onFailure(Exception error) {
                // 변환 실패 처리
                System.err.println("Conversion failed: " + error.getMessage());
            }
        });

    }

    private void uploadFile(String token, File audioFile) {
        if (audioFile != null && audioFile.exists()) {
            RequestBody requestFile = RequestBody.create(audioFile, MediaType.parse("audio/mp3")); // 요청 본문 생성
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", audioFile.getName(), requestFile); // MultipartBody.Part 생성

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class); // API 서비스 생성
            RequestBody model = RequestBody.create("whisper-1", MediaType.parse("text/plain")); // 모델 이름 요청 본문 생성

            Call<Void> call = apiService.saveAudio("Bearer " + token, body, model); // API 호출
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // 파일 삭제
                        if (audioFile.exists()) {
                            audioFile.delete();
                            updateLimit();
                        }

                    } else if (response.code() == 401) {
                        Log.d(Constants.RECORD_ACTIVITY, "Token may be expired. Refreshing token.");
                    } else {
                        String errorMessage = "API 호출 실패";
                        if (response.errorBody() != null) {
                            try {
                                String errorBody = response.errorBody().string();
                                JsonParser parser = new JsonParser();
                                JsonObject jsonObject = parser.parse(errorBody).getAsJsonObject();
                                errorMessage = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "서버 오류";
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(RecordActivity.this, "오류 발생: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    System.out.println(t); // 오류 출력
                }
            });

        }
    }

}
