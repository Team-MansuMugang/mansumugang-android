package com.healthcare.mansumugang;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * LocationService 클래스는 위치 정보를 백그라운드에서 추적하고 알림을 표시하는 서비스입니다.
 */
public class LocationService extends Service {

    private LocationHelper locationHelper; // 위치 정보를 관리할 LocationHelper 객체

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // 이 서비스는 바인딩되지 않으므로 null을 반환합니다.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스가 생성될 때 호출됩니다.
        locationHelper = new LocationHelper(this); // LocationHelper 객체 초기화

        // Android 8.0 (Oreo) 이상에서 알림 채널을 생성합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 알림 채널을 생성합니다.
            NotificationChannel channel = new NotificationChannel(Constants.LOCATION_SERVICE_CHANNEL, // 채널 ID
                    "Location Service Channel", // 채널 이름
                    NotificationManager.IMPORTANCE_LOW // 채널 중요도 설정
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel); // 알림 채널을 시스템에 등록합니다.
            }
        }

        // 포그라운드 서비스로 시작합니다.
        Notification notification = new NotificationCompat.Builder(this, Constants.LOCATION_SERVICE_CHANNEL).setContentTitle("Location Service") // 알림 제목
                .setContentText("Tracking location in the background") // 알림 텍스트
                .setSmallIcon(R.drawable.location) // 알림 아이콘 (적절한 아이콘으로 교체해야 합니다)
                .build(); // 알림을 빌드합니다.

        startForeground(1, notification); // 포그라운드 서비스로 시작하고 알림을 표시합니다.

        // 위치 업데이트를 시작합니다.
        startLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 시작될 때 호출됩니다.
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                // 인텐트의 액션이 위치 서비스 중지 요청일 경우
                if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService(); // 위치 서비스 중지
                }
            }
        }
        return START_STICKY; // 서비스가 종료되면 자동으로 재시작합니다.
    }

    /**
     * 위치 업데이트를 요청합니다.
     */
    private void startLocationUpdates() {
        if (locationHelper != null) {
            locationHelper.fetchLocationOnce(); // 위치 업데이트를 요청합니다.
        }
    }

    /**
     * 위치 서비스를 중지하고 서비스 종료를 처리합니다.
     */
    private void stopLocationService() {
        if (locationHelper != null) {
            locationHelper.stopLocationUpdates(); // 위치 업데이트를 중지합니다.
        }
        stopForeground(true); // 포그라운드 알림을 제거합니다.
        stopSelf(); // 서비스를 종료합니다.
    }
}
