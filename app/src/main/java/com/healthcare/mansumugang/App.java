package com.healthcare.mansumugang;

import android.app.Application;
import android.content.Context;

/**
 * App 클래스는 애플리케이션 수준에서 초기화 작업을 수행하는데 사용됩니다.
 * Application 클래스를 상속받아 앱의 전역 상태를 관리하며, 앱 전반에서 접근 가능한 전역 객체를 제공합니다.
 */
public class App extends Application {
    // 전역적으로 접근할 수 있는 Prefs 객체
    public static Prefs prefs;

    // 애플리케이션 인스턴스
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 애플리케이션이 생성될 때 Prefs 객체를 초기화합니다.
        // Prefs는 앱의 설정이나 공유된 데이터를 저장하고 가져오는 데 사용됩니다.
        prefs = new Prefs(getApplicationContext());
    }

    /**
     * 애플리케이션의 컨텍스트를 반환합니다.
     *
     * @return 애플리케이션의 전역 컨텍스트
     */
    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
