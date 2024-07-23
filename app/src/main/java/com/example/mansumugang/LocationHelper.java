package com.example.mansumugang;

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
 * LocationHelper 클래스는 위치 요청과 관련된 기능을 제공합니다.
 */
public class LocationHelper {
    private static final String TAG = "LocationHelper";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000; // 30초
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10000; // 10초
    private long lastLocationTime = 0; // 마지막 위치 업데이트 타임스탬프

    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    public LocationHelper(Context context) {
        this.context = context;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        this.locationCallback = getLocationCallback();
    }

    /**
     * 위치 업데이트를 시작합니다.
     */
    public void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS) // 30초 간격으로 위치 업데이트 요청
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS) // 최소 10초 간격으로 위치 업데이트 요청
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * 위치 업데이트를 중지합니다.
     */
    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /**
     * LocationCallback 객체를 반환합니다.
     *
     * @return LocationCallback 객체
     */
    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                try {
                    super.onLocationResult(locationResult);
                    if (locationResult != null && locationResult.getLastLocation() != null) {
                        Location location = locationResult.getLastLocation();
                        long locationTime = location.getTime();

                        // 위치 업데이트의 타임스탬프가 이전 업데이트보다 이후인 경우에만 처리
                        if (locationTime > lastLocationTime) {
                            lastLocationTime = locationTime;
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            Log.v(TAG, "LOCATION_UPDATE: " + latitude + ", " + longitude);
                            sendLocationToServer(latitude, longitude); // 서버로 위치 전송
                        } else {
                            Log.w(TAG, "Non-monotonic location received, ignoring: " + locationTime);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing location update", e);
                }
            }
        };
    }

    /**
     * 서버로 위치 정보를 전송합니다.
     *
     * @param latitude 위도
     * @param longitude 경도
     */
    private void sendLocationToServer(double latitude, double longitude) {
        String token = App.prefs.getToken();
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token is null or empty, cannot send location to server.");
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        com.example.mansumugang.LocationRequest locationRequest = new com.example.mansumugang.LocationRequest(latitude, longitude);
        Call<Void> call = apiService.saveLocation("Bearer " + token, locationRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.v(TAG, "Location successfully sent to server.");
                } else {
                    Log.e(TAG, "Failed to send location to server: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error sending location to server: " + t.getMessage());
            }
        });
    }
}
