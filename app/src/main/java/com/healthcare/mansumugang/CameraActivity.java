package com.healthcare.mansumugang;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * CameraActivity 클래스는 카메라 미리보기를 제공하고 사진을 캡처할 수 있는 액티비티입니다.
 */
public class CameraActivity extends AppCompatActivity {
    private TextureView mCameraTextureView; // 카메라 미리보기를 표시할 TextureView
    private Preview mPreview; // 카메라 프리뷰를 처리하는 Preview 객체

    private Button mCameraCaptureButton; // 사진 촬영 버튼

    Activity mainActivity = this; // 현재 액티비티 참조

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.camera_bottom_nav); // 현재 화면을 카메라로 설정

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView, this);

        // UI 요소 초기화
        mCameraCaptureButton = findViewById(R.id.capture);
        mCameraTextureView = findViewById(R.id.cameraTextureView);

        // Preview 객체 생성
        mPreview = new Preview(this, mCameraTextureView, mCameraCaptureButton);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 권한 요청 결과 처리
        switch (requestCode) {
            case Constants.REQUEST_CAMERA:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];

                    // 카메라 권한이 승인되었는지 확인
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            // 카메라 권한이 승인된 경우, TextureView와 Preview 객체 초기화
                            mCameraTextureView = findViewById(R.id.cameraTextureView);
                            mPreview = new Preview(mainActivity, mCameraTextureView, mCameraCaptureButton);
                            mPreview.openCamera(); // 카메라 열기
                            Log.d(Constants.CAMERA_ACTIVITY_TAG, "mPreview set");
                        } else {
                            // 카메라 권한이 거부된 경우, 사용자에게 권한 필요 알림
                            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                            finish(); // 액티비티 종료
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 액티비티가 화면에 표시될 때 호출되는 메소드
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 액티비티가 화면에서 사라질 때 호출되는 메소드
    }
}
