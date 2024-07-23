package com.example.mansumugang;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * LocationService 클래스는 위치 서비스를 관리합니다.
 */
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
        // Android O 이상에서 NotificationHelper 초기화
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHelper = new LocationNotificationHelper(this);
        }
    }

    /**
     * 위치 서비스를 시작합니다.
     * 포그라운드 서비스로 시작하여 사용자에게 알림을 표시합니다.
     */
    private void startLocationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 알림 채널을 통해 포그라운드 서비스 시작
            NotificationCompat.Builder builder = notificationHelper.getNotificationBuilder();
            startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
        }
        // 위치 업데이트 시작
        locationHelper.startLocationUpdates();
    }

    /**
     * 위치 서비스를 중지합니다.
     * 위치 업데이트를 중단하고, 포그라운드 서비스에서 제거합니다.
     */
    private void stopLocationService() {
        // 위치 업데이트 중단
        locationHelper.stopLocationUpdates();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 포그라운드 서비스 중단
            stopForeground(true);
        }
        // 서비스 중지
        stopSelf();
    }

    /**
     * 서비스가 시작될 때 호출됩니다.
     * 인텐트에 따라 위치 서비스를 시작하거나 중지합니다.
     *
     * @param intent  서비스 시작 인텐트
     * @param flags   추가 데이터
     * @param startId 서비스 시작 ID
     * @return 서비스 시작 상태
     */
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
        return super.onStartCommand(intent, flags, startId);
    }
}
