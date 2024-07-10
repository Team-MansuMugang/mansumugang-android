package com.example.mansumugang;

import android.content.ContentValues;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordActivity extends AppCompatActivity {
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String fileUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initializeRecordingButtons();

    }

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
                }
            }
        });
    }

    // 녹음을 시작하는 메소드입니다.
    private void startRecording() {
        try {
            Button recordButton = findViewById(R.id.recording_start_button);
            LinearLayout recording_button_box = findViewById(R.id.recording_button_box);

            recordButton.setVisibility(View.GONE);

            recording_button_box.setVisibility(View.VISIBLE);

            setupMediaRecorder(); // MediaRecorder를 설정합니다.

            // 녹음을 준비하고 시작합니다.
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true; // 녹음 상태를 true로 변경합니다.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // MediaRecorder를 설정하는 메소드입니다.
    private void setupMediaRecorder() throws IOException {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 오디오 소스 설정
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 출력 형식 설정
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 오디오 인코더 설정

//        // ParcelFileDescriptor를 통해 얻은 FileDescriptor를 사용
//        ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(fileUri, "w");
//
//        if (pfd == null) {
//            throw new IOException("Cannot open file descriptor for URI: " + fileUri);
//        }

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/" +Environment.DIRECTORY_DOCUMENTS +"/";

        System.out.println(filePath + "파일 위치");
        mediaRecorder.setOutputFile(filePath + createFileName()+ ".mp3");
    }


    // MediaStore를 사용하여 녹음 파일의 URI를 생성하는 메소드입니다.

    // 현재 시간을 기반으로 파일 이름을 생성하는 메소드입니다.
    private String createFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "REC_" + timeStamp;
    }

    // 파일 URI에 대한 파일 디스크립터를 반환하는 메소드입니다.
    private ParcelFileDescriptor getFileDescriptor(Uri uri) throws IOException {
        ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
        if (pfd == null) {
            throw new IOException("Cannot open file descriptor for URI: " + uri);
        }
        return pfd;
    }

    // 녹음을 중지하고 MediaRecorder 리소스를 해제하는 메소드입니다.
    private void stopRecording() {
        if (mediaRecorder != null) {
            Button recordButton = findViewById(R.id.recording_start_button);
            LinearLayout recording_button_box = findViewById(R.id.recording_button_box);

            recordButton.setVisibility(View.VISIBLE);
            recording_button_box.setVisibility(View.GONE);

            mediaRecorder.stop(); // 녹음을 중지합니다.
            mediaRecorder.release(); // MediaRecorder 리소스를 해제합니다.
            mediaRecorder = null; // MediaRecorder 객체를 null로 설정합니다.
            isRecording = false; // 녹음 상태를 false로 변경합니다.
        }
    }
}
