package com.healthcare.mansumugang;

/**
 * CustomLocationRequest 클래스는 위치 정보를 요청하기 위한 데이터를 담고 있는 모델 클래스입니다.
 */
public class CustomLocationRequest {

    // 위치의 위도
    private double latitude;

    // 위치의 경도
    private double longitude;

    /**
     * 생성자: 위도와 경도를 받아서 CustomLocationRequest 객체를 생성합니다.
     *
     * @param latitude  위치의 위도
     * @param longitude 위치의 경도
     */
    public CustomLocationRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * 객체를 문자열로 변환하여 출력할 때 사용되는 메서드입니다.
     *
     * @return CustomLocationRequest 객체의 문자열 표현
     */
    @Override
    public String toString() {
        return "CustomLocationRequest{" + "latitude=" + latitude + ", longitude=" + longitude + '}';
    }
}
