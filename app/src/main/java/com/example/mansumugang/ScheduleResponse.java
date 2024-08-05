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

        public static class Medicine {
            @SerializedName("status")
            private String status;

            @SerializedName("medicineIntakeTime")
            private String medicineIntakeTime;

            @SerializedName("medicineId")
            private int medicineId;

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

            public int getMedicineId() {
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
    }
}
