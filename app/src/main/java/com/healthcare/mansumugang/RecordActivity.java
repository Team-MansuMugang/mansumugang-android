package com.healthcare.mansumugang;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String filePath = "";
    private File audioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.recording);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView, this);

        if (!hasPermissions()) {
            requestAudioPermissions();
        }

        initializeRecordingButtons();
    }

    private boolean hasPermissions() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestAudioPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeRecordingButtons();
            } else {
                Toast.makeText(this, "Permissions are required to record audio", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * 녹음 버튼들을 초기화하는 메소드입니다.
     */
    private void initializeRecordingButtons() {
        Button recordButton = findViewById(R.id.recording_start_button);
        ImageButton recordImageButton = findViewById(R.id.recording_button);
        Button sendButton = findViewById(R.id.recording_save_button);
        Button cancelButton = findViewById(R.id.recording_cancel_button);

        // 녹음 시작 버튼에 클릭 리스너를 설정합니다.
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording();
                }
            }
        });

        recordImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording();
                }
            }
        });

        // 녹음 중지 버튼에 클릭 리스너를 설정합니다.
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecording();
                    convertAudio();

                }
            }
        });

        // 녹음 취소 버튼에 클릭 리스너를 설정합니다.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    cancelRecording();
                }
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
            chronometer.start();

            recordButton.setVisibility(View.GONE);
            recordingButtonBox.setVisibility(View.VISIBLE);

            filePath = setupMediaRecorder();

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

                    // 20분(1200초)에 해당하는 시간을 체크하여 녹음 중지 및 변환
                    if (elapsedMillis >= 20 * 60 * 1000) {  // 20분 = 20 * 60 * 1000 밀리초
                        if (isRecording) {
                            stopRecording();
                            convertAudio();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    /**
     * MediaRecorder를 설정하는 메소드입니다.
     *
     * @return 녹음 파일 경로
     * @throws IOException 입출력 예외
     */
    private String setupMediaRecorder() throws IOException {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioEncodingBitRate(48000);
        mediaRecorder.setAudioSamplingRate(48000);


        String filePath = createFilePath() + createFileName() + ".3gp";
        System.out.println(filePath);
        mediaRecorder.setOutputFile(filePath);
        audioFile = new File(filePath);
        return filePath;
    }

    /**
     * 파일 경로를 생성하는 메소드입니다.
     *
     * @return 파일 경로
     */
    private String createFilePath() {
        return getCacheDir().getAbsolutePath() + "/";
    }

    /**
     * 현재 시간을 기반으로 파일 이름을 생성하는 메소드입니다.
     *
     * @return 파일 이름
     */
    private String createFileName() {
        return "REC_" + System.currentTimeMillis();
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

                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                recordButton.setVisibility(View.VISIBLE);
                recordingButtonBox.setVisibility(View.GONE);

                mediaRecorder.stop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
            }
        }
    }

    private void cancelRecording() {
        stopRecording();
        if (audioFile.exists() && audioFile.delete()) {
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
                audioFile.delete();
                uploadFile(token,convertedFile);
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
            RequestBody requestFile = RequestBody.create(audioFile, MediaType.parse("audio/mp3"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("audio", audioFile.getName(), requestFile);

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Call<Void> call = apiService.saveAudio("Bearer " + token, body);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RecordActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        if (audioFile.exists()){ audioFile.delete(); }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    System.out.println(t);
                }
            });
        } else {
            Toast.makeText(this, "No file to upload", Toast.LENGTH_SHORT).show();
        }
    }


}
