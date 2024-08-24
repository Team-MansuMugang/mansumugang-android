package com.healthcare.mansumugang;

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

    @Override
    public String toString() {
        return "CustomLocationRequest{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
