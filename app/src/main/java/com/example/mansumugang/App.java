package com.example.mansumugang;

import android.app.Application;
import android.content.Context;

/**
 * App 클래스는 애플리케이션 수준에서 초기화를 수행하는 데 사용됩니다.
 * Application 클래스를 상속받아 앱의 전역 상태를 관리할 수 있습니다.
 */
public class App extends Application {
    // 전역적으로 접근할 수 있는 Prefs 객체
    public static Prefs prefs;

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // 애플리케이션이 생성될 때 Prefs 객체를 초기화합니다.
        prefs = new Prefs(getApplicationContext());
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
