package com.example.mansumugang;

/**
 * CustomLocationRequest 클래스는 위치 요청을 나타냅니다.
 */
public class CustomLocationRequest {
    private double latitude;
    private double longitude;

    public CustomLocationRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "CustomLocationRequest{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
