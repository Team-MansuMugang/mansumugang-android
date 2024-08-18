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
 * LocationHelper 클래스는 위치 정보를 서버에 전송하는 역할을 합니다.
 */
public class LocationHelper {

    private static final String TAG = "LocationHelper";
    private static final long LOCATION_UPDATE_INTERVAL_MS = 10000; // 10초
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL_MS = 5000; // 5초

    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location lastLocation; // 마지막 위치를 저장할 변수 추가

    public LocationHelper(Context context) {
        this.context = context;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        this.locationCallback = getLocationCallback();
    }

    public void fetchLocationOnce() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_UPDATE_INTERVAL_MS)
                .setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL_MS);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우, 권한 요청 또는 사용자에게 알림
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLocations().isEmpty()) {
                    Log.e(TAG, "No location result available.");
                    return;
                }

                Location location = locationResult.getLocations().get(0);
                if (location != null) {
                    lastLocation = location; // 마지막 위치 업데이트
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.v(TAG, "LOCATION_UPDATE: " + latitude + ", " + longitude);
                    sendLocationToServer(latitude, longitude);

                    // 위치 정보를 받은 후, 위치 업데이트를 중지합니다.
                    fusedLocationProviderClient.removeLocationUpdates(this);
                }
            }
        };
    }

    public void sendLocationToServer(double latitude, double longitude) {
        String token = App.prefs.getToken();
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token is null or empty, cannot send location to server.");
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        CustomLocationRequest locationRequest = new CustomLocationRequest(latitude, longitude);
        Call<Void> call = apiService.saveLocation("Bearer " + token, locationRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.v(TAG, "Location successfully sent to server.");
                } else {
                    Log.e(TAG, "Failed to send location to server: " + response.message());
                    if (response.code() == 401) {
                        Log.d(TAG, "Token may be expired. Refreshing token.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error sending location to server", t);
            }
        });
    }

    public double getLatitude() {
        if (lastLocation != null) {
            return lastLocation.getLatitude();
        } else {
            Log.e(TAG, "No last known location available.");
            return 0.0;
        }
    }

    public double getLongitude() {
        if (lastLocation != null) {
            return lastLocation.getLongitude();
        } else {
            Log.e(TAG, "No last known location available.");
            return 0.0;
        }
    }
}
