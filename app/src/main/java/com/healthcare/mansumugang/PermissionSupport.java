package com.healthcare.mansumugang;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * PermissionSupport 클래스는 런타임 권한 요청 및 검사를 지원합니다.
 * 이 클래스는 앱의 필요한 권한을 관리하고, 권한을 요청하거나 권한이 허용되었는지 확인하는 기능을 제공합니다.
 */
public class PermissionSupport {

    private Context context;
    private Activity activity;
    private List<String> permissions;

    // 권한 요청 코드 상수
    // 다중 권한 요청 코드
    // 배경 위치 권한 요청 코드

    /**
     * PermissionSupport 생성자
     *
     * @param activity 현재 활동(Activity)
     * @param context  현재 컨텍스트(Context)
     */
    public PermissionSupport(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        this.permissions = getPermissions(); // 필요한 권한 리스트를 가져옵니다.
    }

    /**
     * 필요 권한 리스트를 반환하는 메서드
     * Android 버전별로 필요한 권한을 설정하여 반환합니다.
     *
     * @return 필요한 권한 리스트
     */
    private List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        // Android 버전별로 권한을 설정합니다.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // Android P 이하 버전
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.CAMERA);
            permissions.add(Manifest.permission.RECORD_AUDIO);
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            // Android Q부터 S 버전까지
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.RECORD_AUDIO);
            permissions.add(Manifest.permission.CAMERA);
        } else {
            // Android S 이상 버전
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.CAMERA);
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        // Android S 이상에서 스케줄 정확한 알람 권한 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.SCHEDULE_EXACT_ALARM);
        }
        return permissions;
    }

    /**
     * 권한이 허용되었는지 확인하는 메서드
     *
     * @return 모든 권한이 허용되었는지 여부
     */
    public boolean checkPermission() {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions.isEmpty(); // 허용된 권한만 있을 경우 true 반환
    }

    /**
     * 모든 필요한 권한을 요청하는 메서드
     * 권한이 허용되지 않은 경우, 권한 요청 대화상자를 표시합니다.
     */
    public void requestAllPermissions() {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (!deniedPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(new String[0]), Constants.MULTIPLE_PERMISSIONS);
        }
    }

    /**
     * 배경 위치 권한이 허용되었는지 확인하고 요청하는 메서드
     * <p>
     * 배경 위치 권한이 허용되지 않은 경우, 권한 요청 대화상자를 표시합니다.
     * </p>
     */
    public void checkAndRequestBackgroundLocationPermission() {
        // 배경 위치 권한이 허용되었는지 확인
        boolean backgroundLocationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // 권한이 허용되지 않은 경우, 권한 요청
        if (!backgroundLocationGranted) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 권한 요청 결과를 처리하는 메서드
     *
     * @param requestCode  요청 코드
     * @param permissions  요청된 권한 배열
     * @param grantResults 권한 허용 결과 배열
     * @return 모든 권한이 허용되었는지 여부
     */
    public boolean permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.MULTIPLE_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            return allPermissionsGranted; // 모든 권한이 허용되었는지 여부를 반환
        }
        return false;
    }
}
