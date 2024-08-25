package com.healthcare.mansumugang;

import android.Manifest;

/**
 * Constants 클래스는 애플리케이션에서 사용되는 상수를 정의합니다.
 */
public class Constants {
    static final String ALARM_RECIVER_TAG = "AlarmReceiver"; // 로그 출력을 위한 태그

    static final String ALARM_SCHEDULER_TAG = "AlarmScheduler";

    static final String ALARM_LOCATION_SCHEDULER_TAG = "AlarmLocationScheduler"; // 로그를 찍을 때 사용할 태그

    static final String CAMERA_ACTIVITY_TAG = "CAMERAACTIVITY";

    static final String LOCATION_HELPER_TAG = "LocationHelper";

    static final String LOGIN_ACTIVITY = "LoginActivity";

    static final String SCHEDULE_ACTIVITY = "ScheduleActivity";

    static final String TOKEN_AUTHENTICATOR = "TokenAuthenticator";

    static final  String PREVIEW = "Preview";


    static final String CHANNEL_ID = "alarm_channel"; // 알림 채널 ID

    static final String LOCATION_SERVICE_CHANNEL = "LocationServiceChannel";

    static final String BASE_URL = "http://minnnisu.iptime.org";

    // 위치 서비스 시작 액션
    static final String ACTION_START_LOCATION_SERVICE = "startLocationService";

    // 위치 서비스 중지 액션
    static final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";

    static final String PREF_NAME = "mPref"; // SharedPreferences 이름

    static final String TOKEN_KEY = "token"; // 액세스 토큰 키

    static final String REFRESH_TOKEN_KEY = "refreshToken"; // 리프레시 토큰 키


    static final long REFRESH_INTERVAL = 60000L; // 알람 반복 간격, 60초

    static final int ALARM_REQUEST_CODE = 0; // 알람 요청 코드

    static final int REQUEST_CAMERA = 1;


    static final long LOCATION_UPDATE_INTERVAL_MS = 10000; // 10초

    static final long LOCATION_UPDATE_FASTEST_INTERVAL_MS = 5000; // 5초


    static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1024;

    static final int MULTIPLE_PERMISSIONS = 1023;

    static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    static final String[] PERMISSIONS = {
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

}
