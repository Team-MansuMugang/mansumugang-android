package com.healthcare.mansumugang;

import java.util.List;

/**
 * FamilyMemberResponse 클래스는 가족 구성원 정보에 대한 응답을 나타냅니다.
 */
public class FamilyMemberResponse {

    // 이미지 API의 URL
    private String imageApiUrl;

    // 응답에 포함된 본인 정보
    private FamilyMember self;

    // 응답에 포함된 보호자 정보
    private FamilyMember protector;

    // 응답에 포함된 기타 환자들에 대한 정보
    private List<FamilyMember> otherPatients;

    /**
     * 이미지 API의 URL을 반환합니다.
     *
     * @return 이미지 API의 URL
     */
    public String getImageApiUrl() {
        return imageApiUrl;
    }

    /**
     * 본인 정보를 반환합니다.
     *
     * @return FamilyMember 객체, 본인에 대한 정보
     */
    public FamilyMember getSelf() {
        return self;
    }

    /**
     * 보호자 정보를 반환합니다.
     *
     * @return FamilyMember 객체, 보호자에 대한 정보
     */
    public FamilyMember getProtector() {
        return protector;
    }

    /**
     * 기타 환자들에 대한 정보를 반환합니다.
     *
     * @return FamilyMember 객체의 리스트, 기타 환자들에 대한 정보
     */
    public List<FamilyMember> getOtherPatients() {
        return otherPatients;
    }

    /**
     * FamilyMemberResponse 클래스의 내부 클래스입니다.
     * FamilyMember는 가족 구성원의 정보를 나타냅니다.
     */
    public static class FamilyMember {

        // 가족 구성원의 이름
        private String name;

        // 가족 구성원의 전화번호
        private String telephone;

        // 가족 구성원의 프로필 이미지 이름
        private String profileImageName;

        /**
         * 프로필 이미지의 이름을 반환합니다.
         *
         * @return 프로필 이미지의 이름
         */
        public String getProfileImageName() {
            return profileImageName;
        }

        /**
         * 가족 구성원의 이름을 반환합니다.
         *
         * @return 가족 구성원의 이름
         */
        public String getName() {
            return name;
        }

        /**
         * 가족 구성원의 전화번호를 반환합니다.
         *
         * @return 가족 구성원의 전화번호
         */
        public String getTelephone() {
            return telephone;
        }
    }
}
