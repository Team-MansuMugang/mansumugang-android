package com.example.mansumugang;

import android.content.ContentValues;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * RecordActivity 클래스는 오디오 녹음을 관리합니다.
 */
public class RecordActivity extends AppCompatActivity {
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String filePath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.recording);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView, this);


        initializeRecordingButtons();
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
                    filePath = startRecording();
                }
            }
        });

        recordImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    filePath = startRecording();
                }
            }
        });

        // 녹음 중지 버튼에 클릭 리스너를 설정합니다.
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecording();
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

            filePath = setupMediaRecorder(); // MediaRecorder를 설정합니다.

            // 녹음을 준비하고 시작합니다.
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true; // 녹음 상태를 true로 변경합니다.
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
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 오디오 소스 설정
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 출력 형식 설정
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 오디오 인코더 설정

        String filePath = createFilePath() + createFileName() + ".mp3";
        mediaRecorder.setOutputFile(filePath);
        return filePath;
    }

    /**
     * 파일 경로를 생성하는 메소드입니다.
     *
     * @return 파일 경로
     */
    private String createFilePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_DOCUMENTS + "/";
    }

    /**
     * 현재 시간을 기반으로 파일 이름을 생성하는 메소드입니다.
     *
     * @return 파일 이름
     */
    private String createFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "REC_" + timeStamp;
    }

    /**
     * 녹음을 취소하는 메소드입니다.
     * 녹음을 중지하고 파일을 삭제합니다.
     */
    protected void cancelRecording() {
        stopRecording();
        System.out.println("파일 경로: " + filePath);
        File file = new File(filePath);
        file.delete();
    }

    /**
     * 녹음을 중지하고 MediaRecorder 리소스를 해제하는 메소드입니다.
     */
    private void stopRecording() {
        if (mediaRecorder != null) {
            Button recordButton = findViewById(R.id.recording_start_button);
            LinearLayout recordingButtonBox = findViewById(R.id.recording_button_box);
            Chronometer chronometer = findViewById(R.id.recording_time);

            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            recordButton.setVisibility(View.VISIBLE);
            recordingButtonBox.setVisibility(View.GONE);

            mediaRecorder.stop(); // 녹음을 중지합니다.
            mediaRecorder.release(); // MediaRecorder 리소스를 해제합니다.
            mediaRecorder = null; // MediaRecorder 객체를 null로 설정합니다.
            isRecording = false; // 녹음 상태를 false로 변경합니다.
        }
    }
}
