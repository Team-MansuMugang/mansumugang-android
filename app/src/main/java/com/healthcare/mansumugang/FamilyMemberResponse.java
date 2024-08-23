package com.healthcare.mansumugang;

import java.util.List;

public class FamilyMemberResponse {
    private FamilyMember self;
    private FamilyMember protector;
    private List<FamilyMember> otherPatients;

    public FamilyMember getSelf() {
        return self;
    }

    public void setSelf(FamilyMember self) {
        this.self = self;
    }

    public FamilyMember getProtector() {
        return protector;
    }

    public void setProtector(FamilyMember protector) {
        this.protector = protector;
    }

    public List<FamilyMember> getOtherPatients() {
        return otherPatients;
    }

    public void setOtherPatients(List<FamilyMember> otherPatients) {
        this.otherPatients = otherPatients;
    }

    public static class FamilyMember {
        private String name;
        private String telephone;
        private String usertype;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getUsertype() {
            return usertype;
        }

        public void setUsertype(String usertype) {
            this.usertype = usertype;
        }
    }
}
