package com.healthcare.mansumugang;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Prefs 클래스는 SharedPreferences를 사용하여 사용자 설정 및 데이터를 저장하고 가져옵니다.
 * 이 클래스는 액세스 토큰과 리프레시 토큰을 저장하고 불러오는 기능을 제공합니다.
 */
public class Prefs {
    private SharedPreferences prefs;

    /**
     * Prefs 생성자
     *
     * @param context 애플리케이션 컨텍스트
    *                생성자에서는 주어진 컨텍스트를 사용하여 SharedPreferences 객체를 초기화합니다.
    *                SharedPreferences는 애플리케이션의 설정 및 데이터 저장에 사용됩니다.
     */
    public Prefs(Context context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    // 액세스 토큰 관련 메서드

    /**
     * 저장된 액세스 토큰을 반환합니다.
     *
     * @return 액세스 토큰
     * SharedPreferences에서 액세스 토큰을 가져와 반환합니다.
     * 저장된 토큰이 없을 경우 null을 반환합니다.
     */
    public String getToken() {
        return prefs.getString(Constants.TOKEN_KEY, null);
    }

    /**
     * 액세스 토큰을 저장합니다.
     *
     * @param token 액세스 토큰
     *              주어진 액세스 토큰을 SharedPreferences에 저장합니다.
     *              저장된 토큰은 애플리케이션에서 인증을 위해 사용됩니다.
     */
    public void setToken(String token) {
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(Constants.TOKEN_KEY, token).apply();
    }

    // 리프레시 토큰 관련 메서드

    /**
     * 저장된 리프레시 토큰을 반환합니다.
     *
     * @return 리프레시 토큰
     * SharedPreferences에서 리프레시 토큰을 가져와 반환합니다.
     * 저장된 토큰이 없을 경우 null을 반환합니다.
     */
    public String getRefreshToken() {
        return prefs.getString(Constants.REFRESH_TOKEN_KEY, null);
    }

    /**
     * 리프레시 토큰을 저장합니다.
     *
     * @param refreshToken 리프레시 토큰
     *                     주어진 리프레시 토큰을 SharedPreferences에 저장합니다.
     *                     리프레시 토큰은 액세스 토큰의 갱신에 사용됩니다.
     */
    public void setRefreshToken(String refreshToken) {
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(Constants.REFRESH_TOKEN_KEY, refreshToken).apply();
    }
}
