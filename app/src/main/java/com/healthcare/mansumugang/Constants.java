package com.healthcare.mansumugang;

/**
 * Constants 클래스는 애플리케이션에서 사용되는 상수를 정의합니다.
 */
public class Constants {
    // 위치 서비스의 고유 ID
    static final int LOCATION_SERVICE_ID = 175;

    // 위치 서비스 알림 채널 ID
    public static final String LOCATION_CHANNEL_ID = "location_channel_id";

    // 위치 서비스 알림 ID
    public static final int LOCATION_NOTIFICATION_ID = 1001;

    // 위치 서비스 시작 액션
    static final String ACTION_START_LOCATION_SERVICE = "startLocationService";

    // 위치 서비스 중지 액션
    static final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";

}