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

public class PermissionSupport {

    private Context context;
    private Activity activity;
    private List<String> permissions;

    // 권한 요청 코드

    public PermissionSupport(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        this.permissions = getPermissions();
    }

    private List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.CAMERA);
            permissions.add(Manifest.permission.RECORD_AUDIO);
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            permissions.add(Manifest.permission.RECORD_AUDIO);
            permissions.add(Manifest.permission.CAMERA);
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.CAMERA);
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
            permissions.add(Manifest.permission.RECORD_AUDIO);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.SCHEDULE_EXACT_ALARM);
        }
        return permissions;
    }

    public boolean checkPermission() {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions.isEmpty();
    }

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

    public void checkAndRequestBackgroundLocationPermission() {
        // Check if ACCESS_BACKGROUND_LOCATION permission is granted
        boolean backgroundLocationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If the permission is not granted, request it
        if (!backgroundLocationGranted) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    Constants.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    public boolean permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.MULTIPLE_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            return allPermissionsGranted;
        }
        return false;
    }
}
