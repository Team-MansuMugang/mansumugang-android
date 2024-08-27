package com.healthcare.mansumugang;

import android.Manifest;

/**
 * Constants 클래스는 애플리케이션에서 사용되는 상수를 정의합니다.
 */
public class Constants {

    // AlarmReceiver에서 사용하는 로그 태그
    static final String ALARM_RECIVER_TAG = "AlarmReceiver";

    // AlarmScheduler에서 사용하는 로그 태그
    static final String ALARM_SCHEDULER_TAG = "AlarmScheduler";

    static final String SETTINGS_ACTIVITY = "SETTINGSACTIVITY";

    static final String RECORD_ACTIVITY = "RECORDACTIVITY";

    // AlarmLocationScheduler에서 사용하는 로그 태그
    static final String ALARM_LOCATION_SCHEDULER_TAG = "AlarmLocationScheduler";

    // CameraActivity에서 사용하는 로그 태그
    static final String CAMERA_ACTIVITY_TAG = "CAMERAACTIVITY";

    // LocationHelper에서 사용하는 로그 태그
    static final String LOCATION_HELPER_TAG = "LocationHelper";

    // 로그인 화면의 식별자
    static final String LOGIN_ACTIVITY = "LoginActivity";

    // 로그아웃 태그
    static final String LOGOUT = "Logout";

    // 일정 화면의 식별자
    static final String SCHEDULE_ACTIVITY = "ScheduleActivity";

    // TokenAuthenticator의 식별자
    static final String TOKEN_AUTHENTICATOR = "TokenAuthenticator";

    // Preview의 식별자
    static final String PREVIEW = "Preview";

    // 알림 채널 ID
    static final String CHANNEL_ID = "alarm_channel";

    // 위치 서비스 알림 채널 ID
    static final String LOCATION_SERVICE_CHANNEL = "LocationServiceChannel";

    // 서버의 기본 URL
    static final String BASE_URL = "https://api.mansumugang.kr";

    // 위치 서비스 시작 액션을 위한 상수
    static final String ACTION_START_LOCATION_SERVICE = "startLocationService";

    // 위치 서비스 중지 액션을 위한 상수
    static final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";

    // SharedPreferences의 이름
    static final String PREF_NAME = "mPref";

    // 액세스 토큰을 저장하기 위한 SharedPreferences의 키
    static final String TOKEN_KEY = "token";

    // 리프레시 토큰을 저장하기 위한 SharedPreferences의 키
    static final String REFRESH_TOKEN_KEY = "refreshToken";

    // 알람의 반복 간격 (밀리초 단위), 60초
    static final long REFRESH_INTERVAL = 60000L;

    // 알람 요청을 식별하기 위한 코드
    static final int ALARM_REQUEST_CODE = 0;

    // 카메라 권한 요청 코드
    static final int REQUEST_CAMERA = 1;

    // 위치 업데이트 간격 (밀리초 단위), 10초
    static final long LOCATION_UPDATE_INTERVAL_MS = 10000;

    // 위치 업데이트의 가장 빠른 간격 (밀리초 단위), 5초
    static final long LOCATION_UPDATE_FASTEST_INTERVAL_MS = 5000;

    // 백그라운드 위치 권한 요청 코드
    static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1024;

    // 여러 권한 요청 코드
    static final int MULTIPLE_PERMISSIONS = 1023;

    // 오디오 녹음 권한 요청 코드
    static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    // 웹뷰 요청 코드
    static final int SIGN_UP_REQUEST_CODE = 101;

    // 웹뷰 요청 코드
    static final int WITHDRAW_REQUEST_CODE = 102;


    // 오디오 녹음과 저장에 필요한 권한 목록
    static final String[] RECORD_PERMISSIONS = {android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

}
