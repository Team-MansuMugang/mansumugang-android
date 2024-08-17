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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = notificationHelper.getNotificationBuilder();
            startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
        }
        locationHelper.fetchLocationOnce(); // 위치를 한 번만 요청
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
        // 서비스가 중지될 때 stopSelf()를 호출하여 서비스 종료
        stopSelf();
        return START_NOT_STICKY; // 서비스가 종료되면 다시 시작되지 않음
    }

    private void stopLocationService() {
        // 위치 업데이트 중지 호출
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        }
        notificationHelper.hideNotification(); // 위치 서비스 중지 시 알림 숨기기
    }
}
