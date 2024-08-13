package com.example.mansumugang;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class LocationService extends Service {

    private LocationHelper locationHelper;
    private LocationNotificationHelper notificationHelper;
    private boolean isServiceRunning = false; // 서비스 실행 상태를 추적하기 위한 변수

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationHelper = new LocationHelper(this);
        notificationHelper = new LocationNotificationHelper(this);
    }

    private void startLocationService() {
        if (!isServiceRunning) { // 서비스가 이미 실행 중인지 확인
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder builder = notificationHelper.getNotificationBuilder();
                startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
            }
            locationHelper.startLocationUpdates();
            isServiceRunning = true;
        }
    }

    private void stopLocationService() {
        if (isServiceRunning) { // 서비스가 실행 중인 경우에만 중지
            locationHelper.stopLocationUpdates();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopForeground(true);
            }
            notificationHelper.hideNotification(); // 위치 서비스 중지 시 알림 숨기기
            stopSelf();
            isServiceRunning = false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return START_STICKY;
    }
}
