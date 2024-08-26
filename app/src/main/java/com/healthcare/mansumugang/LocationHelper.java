package com.healthcare.mansumugang;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LocationHelper 클래스는 위치 정보를 관리하고 서버에 전송하는 역할을 합니다.
 */
public class LocationHelper {

    private Context context; // 컨텍스트를 저장할 변수
    private FusedLocationProviderClient fusedLocationProviderClient; // 위치 서비스를 위한 FusedLocationProviderClient
    private LocationCallback locationCallback; // 위치 결과를 받기 위한 콜백
    private Location lastLocation; // 마지막 위치를 저장할 변수

    /**
     * 생성자: LocationHelper 객체를 초기화합니다.
     *
     * @param context 애플리케이션의 컨텍스트
     */
    public LocationHelper(Context context) {
        this.context = context;
        // FusedLocationProviderClient 객체를 생성하여 초기화합니다.
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        // 위치 콜백을 생성합니다.
        this.locationCallback = createLocationCallback();
    }

    /**
     * 위치를 한 번만 요청합니다.
     */
    public void fetchLocationOnce() {
        // 위치 요청 객체를 생성하고 설정합니다.
        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // 높은 정확도로 위치 요청
                .setInterval(Constants.LOCATION_UPDATE_INTERVAL_MS) // 위치 업데이트 간격 (10초)
                .setFastestInterval(Constants.LOCATION_UPDATE_FASTEST_INTERVAL_MS) // 위치 업데이트의 가장 빠른 간격 (5초)
                .setNumUpdates(1); // 위치 업데이트를 한 번만 받도록 설정

        // 위치 권한이 있는지 확인합니다.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우, 로그를 찍고 반환합니다.
            Log.e(Constants.LOCATION_HELPER_TAG, "Location permissions are not granted.");
            return;
        }

        // 위치 업데이트 요청을 시작합니다.
        Log.d(Constants.LOCATION_HELPER_TAG, "Requesting location updates");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * 위치 콜백을 생성하여 위치 결과를 처리합니다.
     *
     * @return LocationCallback 객체
     */
    private LocationCallback createLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // 위치 결과가 없거나 빈 경우 로그를 찍고 반환합니다.
                if (locationResult == null || locationResult.getLocations().isEmpty()) {
                    Log.e(Constants.LOCATION_HELPER_TAG, "No location result available.");
                    return;
                }

                // 최신 위치를 가져옵니다.
                Location location = locationResult.getLocations().get(0);
                if (location != null) {
                    lastLocation = location; // 마지막 위치 업데이트
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.v(Constants.LOCATION_HELPER_TAG, "LOCATION_UPDATE: " + latitude + ", " + longitude);
                    sendLocationToServer(latitude, longitude); // 위치 정보를 서버로 전송합니다.

                    // 위치 정보를 받은 후, 위치 업데이트를 중지합니다.
                    stopLocationUpdates();
                }
            }
        };
    }

    /**
     * 서버에 위치 정보를 전송합니다.
     *
     * @param latitude  위도
     * @param longitude 경도
     */
    public void sendLocationToServer(double latitude, double longitude) {
        String token = App.prefs.getToken(); // 저장된 토큰을 가져옵니다.
        if (token == null || token.isEmpty()) {
            Log.e(Constants.LOCATION_HELPER_TAG, "Token is null or empty, cannot send location to server.");
            return;
        }

        // ApiService 객체를 생성하여 서버 호출을 준비합니다.
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        CustomLocationRequest locationRequest = new CustomLocationRequest(latitude, longitude); // 위치 요청 객체 생성
        Call<Void> call = apiService.saveLocation("Bearer " + token, locationRequest); // 서버 호출

        // 서버 호출 비동기 처리
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.v(Constants.LOCATION_HELPER_TAG, "Location successfully sent to server.");
                } else {
                    Log.e(Constants.LOCATION_HELPER_TAG, "Failed to send location to server: " + response.message());
                    if (response.code() == 401) {
                        Log.d(Constants.LOCATION_HELPER_TAG, "Token may be expired. Refreshing token.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(Constants.LOCATION_HELPER_TAG, "Error sending location to server", t);
            }
        });
    }

    /**
     * 마지막으로 저장된 위도를 반환합니다.
     *
     * @return 위도
     */
    public double getLatitude() {
        if (lastLocation != null) {
            return lastLocation.getLatitude();
        } else {
            Log.e(Constants.LOCATION_HELPER_TAG, "No last known location available.");
            return 0.0;
        }
    }

    /**
     * 마지막으로 저장된 경도를 반환합니다.
     *
     * @return 경도
     */
    public double getLongitude() {
        if (lastLocation != null) {
            return lastLocation.getLongitude();
        } else {
            Log.e(Constants.LOCATION_HELPER_TAG, "No last known location available.");
            return 0.0;
        }
    }

    /**
     * 위치 업데이트를 중지합니다.
     */
    public void stopLocationUpdates() {
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            Log.d(Constants.LOCATION_HELPER_TAG, "Location updates stopped.");
        }
    }
}
