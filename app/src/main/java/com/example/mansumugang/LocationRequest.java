package com.example.mansumugang;

/**
 * LocationRequest 클래스는 위치 정보를 담고 있는 데이터 모델입니다.
 */
public class LocationRequest {
    private double latitude;  // 위도
    private double longitude; // 경도

    /**
     * LocationRequest 생성자
     *
     * @param latitude 위도
     * @param longitude 경도
     */
    public LocationRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * 위도를 반환합니다.
     *
     * @return 위도
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * 위도를 설정합니다.
     *
     * @param latitude 위도
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * 경도를 반환합니다.
     *
     * @return 경도
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * 경도를 설정합니다.
     *
     * @param longitude 경도
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
