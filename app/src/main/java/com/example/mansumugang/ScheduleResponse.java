package com.example.mansumugang;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ScheduleResponse {

    @SerializedName("imageApiUrlPrefix")
    private String imageApiUrlPrefix;

    @SerializedName("date")
    private String date;

    @SerializedName("medicineSchedules")
    private List<Schedule> medicineSchedules;


    // Getters and setters
    public String getImageApiUrlPrefix() {
        return imageApiUrlPrefix;
    }

    public void setImageApiUrlPrefix(String imageApiUrlPrefix) {
        this.imageApiUrlPrefix = imageApiUrlPrefix;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Schedule> getMedicineSchedules() {
        return medicineSchedules;
    }

    public void setMedicineSchedules(List<Schedule> medicineSchedules) {
        this.medicineSchedules = medicineSchedules;
    }

    public static class Schedule {
        @SerializedName("time")
        private String time;

        @SerializedName("medicines")
        private List<Medicine> medicines;

        @SerializedName("hospital")
        private Hospital hospital;

        // Getters and setters
        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public List<Medicine> getMedicines() {
            return medicines;
        }

        public void setMedicines(List<Medicine> medicines) {
            this.medicines = medicines;
        }

        public Hospital getHospital() { // Getter 추가
            return hospital;
        }

        public static class Medicine {
            @SerializedName("status")
            private String status;

            @SerializedName("medicineIntakeTime")
            private String medicineIntakeTime;

            @SerializedName("medicineId")
            private long medicineId;

            @SerializedName("medicineImageName")
            private String medicineImageName;

            @SerializedName("hospitalName")
            private String hospitalName;

            @SerializedName("medicineDescription")
            private String medicineDescription;

            @SerializedName("medicineName")
            private String medicineName;

            @SerializedName("imageApiUrlPrefix")
            private String imageApiUrlPrefix;


            // Getters and setters
            public String isStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getMedicineIntakeTime() {
                return medicineIntakeTime;
            }

            public void setMedicineIntakeTime(String medicineIntakeTime) {
                this.medicineIntakeTime = medicineIntakeTime;
            }

            public long getMedicineId() {
                return medicineId;
            }

            public void setMedicineId(int medicineId) {
                this.medicineId = medicineId;
            }


            public String getImageApiUrlPrefix() {
                return imageApiUrlPrefix;
            }

            public String getMedicineImageName() {
                return medicineImageName;
            }

            public void setMedicineImageName(String medicineImageName) {
                this.medicineImageName = medicineImageName;
            }

            public String getHospitalName() {
                return hospitalName;
            }

            public void setHospitalName(String hospitalName) {
                this.hospitalName = hospitalName;
            }

            public String getMedicineDescription() {
                return medicineDescription;
            }

            public void setMedicineDescription(String medicineDescription) {
                this.medicineDescription = medicineDescription;
            }

            public String getMedicineName() {
                return medicineName;
            }

            public void setMedicineName(String medicineName) {
                this.medicineName = medicineName;
            }
        }
        public static class Hospital {
            @SerializedName("hospitalId")
            private long hospitalId;

            @SerializedName("hospitalAddress")
            private String hospitalAddress;

            @SerializedName("hospitalName")
            private String hospitalName;

            @SerializedName("latitude")
            private double latitude;

            @SerializedName("longitude")
            private double longitude;

            @SerializedName("hospitalDescription")
            private String hospitalDescription;

            @SerializedName("status")
            private boolean status;

// Getters and setters

            public long getHospitalId() {
                return hospitalId;
            }

            public void setHospitalId(long hospitalId) {
                this.hospitalId = hospitalId;
            }

            public String getHospitalAddress() {
                return hospitalAddress;
            }

            public String getHospitalName() {
                return hospitalName;
            }

            public void setHospitalAddress(String hospitalAddress) {
                this.hospitalAddress = hospitalAddress;
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

            public String getHospitalDescription() {
                return hospitalDescription;
            }

            public void setHospitalDescription(String hospitalDescription) {
                this.hospitalDescription = hospitalDescription;
            }

            public boolean isHospitalStatus() {
                return status;
            }

            public void setHospitalStatus(boolean status) {
                this.status = status;
            }

        }

        }
}
