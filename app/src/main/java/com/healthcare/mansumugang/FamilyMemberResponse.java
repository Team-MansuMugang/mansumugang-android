package com.healthcare.mansumugang;

import java.util.List;

public class FamilyMemberResponse {
    private String imageApiUrl;
    private FamilyMember self;
    private FamilyMember protector;
    private List<FamilyMember> otherPatients;

    public String getImageApiUrl() {
        return imageApiUrl;
    }

    public FamilyMember getSelf() {
        return self;
    }


    public FamilyMember getProtector() {
        return protector;
    }


    public List<FamilyMember> getOtherPatients() {
        return otherPatients;
    }


    public static class FamilyMember {
        private String name;
        private String telephone;
        private String profileImageName;

        public String getProfileImageName() {
            return profileImageName;
        }


        public String getName() {
            return name;
        }


        public String getTelephone() {
            return telephone;
        }


    }
}
