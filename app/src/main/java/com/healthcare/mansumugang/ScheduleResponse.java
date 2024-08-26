package com.healthcare.mansumugang;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 서버로부터 받은 일정 응답 데이터를 담는 클래스입니다.
 */
public class ScheduleResponse {

    private String imageApiUrlPrefix; // 이미지 API의 URL 접두사

    private String date; // 일정 날짜

    private List<Schedule> medicineSchedules; // 약물 일정 목록

    // Getter 메소드: imageApiUrlPrefix 값을 반환합니다.
    public String getImageApiUrlPrefix() {
        return imageApiUrlPrefix;
    }

    // Getter 메소드: date 값을 반환합니다.
    public String getDate() {
        return date;
    }

    // Getter 메소드: medicineSchedules 리스트를 반환합니다.
    public List<Schedule> getMedicineSchedules() {
        return medicineSchedules;
    }

    /**
     * 개별 일정 항목을 나타내는 내부 클래스입니다.
     */
    public static class Schedule {
        private String time; // 일정 시간

        private List<Medicine> medicines; // 약물 목록

        private Hospital hospital; // 병원 정보

        // Getter 메소드: 일정 시간을 반환합니다.
        public String getTime() {
            return time;
        }

        // Getter 메소드: 약물 목록을 반환합니다.
        public List<Medicine> getMedicines() {
            return medicines;
        }

        // Getter 메소드: 병원 정보를 반환합니다.
        public Hospital getHospital() {
            return hospital;
        }

        /**
         * 약물 정보를 나타내는 내부 클래스입니다.
         */
        public static class Medicine {
            private String status; // 약물 상태

            private String medicineIntakeTime; // 약물 복용 시간

            private long medicineId; // 약물 ID

            private String medicineImageName; // 약물 이미지 파일 이름

            private String hospitalName; // 병원 이름

            private String medicineDescription; // 약물 설명

            private String medicineName; // 약물 이름

            private String imageApiUrlPrefix; // 이미지 API의 URL 접두사

            // Getter 메소드: 약물 상태를 반환합니다.
            public String isStatus() {
                return status;
            }

            // Getter 메소드: 약물 ID를 반환합니다.
            public long getMedicineId() {
                return medicineId;
            }

            // Getter 메소드: 약물 이미지 파일 이름을 반환합니다.
            public String getMedicineImageName() {
                return medicineImageName;
            }

            // Getter 메소드: 병원 이름을 반환합니다.
            public String getHospitalName() {
                return hospitalName;
            }

            // Getter 메소드: 약물 설명을 반환합니다.
            public String getMedicineDescription() {
                return medicineDescription;
            }

            // Getter 메소드: 약물 이름을 반환합니다.
            public String getMedicineName() {
                return medicineName;
            }
        }

        /**
         * 병원 정보를 나타내는 내부 클래스입니다.
         */
        public static class Hospital {
            private long hospitalId; // 병원 ID

            private String hospitalAddress; // 병원 주소

            private String hospitalName; // 병원 이름

            private double latitude; // 병원 위도

            private double longitude; // 병원 경도

            private String hospitalDescription; // 병원 설명

            private boolean status; // 병원 방문 상태

            // Getter 메소드: 병원 ID를 반환합니다.
            public long getHospitalId() {
                return hospitalId;
            }

            // Getter 메소드: 병원 주소를 반환합니다.
            public String getHospitalAddress() {
                return hospitalAddress;
            }

            // Getter 메소드: 병원 이름을 반환합니다.
            public String getHospitalName() {
                return hospitalName;
            }

            // Getter 메소드: 병원 위도를 반환합니다.
            public double getLatitude() {
                return latitude;
            }

            // Getter 메소드: 병원 경도를 반환합니다.
            public double getLongitude() {
                return longitude;
            }

            // Getter 메소드: 병원 설명을 반환합니다.
            public String getHospitalDescription() {
                return hospitalDescription;
            }

            // Getter 메소드: 병원 방문 상태를 반환합니다.
            public boolean isHospitalStatus() {
                return status;
            }
        }
    }
}
