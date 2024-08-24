package com.healthcare.mansumugang;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ScheduleResponse {

    private String imageApiUrlPrefix;

    private String date;

    private List<Schedule> medicineSchedules;


    // Getters and setters
    public String getImageApiUrlPrefix() {
        return imageApiUrlPrefix;
    }


    public String getDate() {
        return date;
    }


    public List<Schedule> getMedicineSchedules() {
        return medicineSchedules;
    }


    public static class Schedule {
        private String time;

        private List<Medicine> medicines;

        private Hospital hospital;

        // Getters and setters
        public String getTime() {
            return time;
        }


        public List<Medicine> getMedicines() {
            return medicines;
        }


        public Hospital getHospital() { // Getter 추가
            return hospital;
        }

        public static class Medicine {
            private String status;

            private String medicineIntakeTime;

            private long medicineId;

            private String medicineImageName;

            private String hospitalName;

            private String medicineDescription;

            private String medicineName;

            private String imageApiUrlPrefix;


            // Getters and setters
            public String isStatus() {
                return status;
            }


            public long getMedicineId() {
                return medicineId;
            }



            public String getMedicineImageName() {
                return medicineImageName;
            }



            public String getHospitalName() {
                return hospitalName;
            }


            public String getMedicineDescription() {
                return medicineDescription;
            }



            public String getMedicineName() {
                return medicineName;
            }


        }
        public static class Hospital {

            private long hospitalId;


            private String hospitalAddress;


            private String hospitalName;


            private double latitude;

            private double longitude;


            private String hospitalDescription;

            private boolean status;

// Getters and setters

            public long getHospitalId() {
                return hospitalId;
            }


            public String getHospitalAddress() {
                return hospitalAddress;
            }

            public String getHospitalName() {
                return hospitalName;
            }


            public double getLatitude() {
                return latitude;
            }


            public double getLongitude() {
                return longitude;
            }


            public String getHospitalDescription() {
                return hospitalDescription;
            }


            public boolean isHospitalStatus() {
                return status;
            }


        }

        }
}
