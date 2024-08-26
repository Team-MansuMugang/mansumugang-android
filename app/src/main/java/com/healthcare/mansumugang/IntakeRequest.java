package com.healthcare.mansumugang;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * IntakeRequest 클래스는 약 복용 요청을 나타냅니다.
 * 이 클래스는 서버에 약 복용 정보를 전송하는 데 사용됩니다.
 */
public class IntakeRequest {

    private Long hospitalId; // 병원 ID

    private Medicine medicine; // 약 정보

    // 생성자
    public IntakeRequest(List<Long> medicineIds, String medicineIntakeTime, String scheduledMedicineIntakeDate) {
        // 약 정보를 담는 Medicine 객체를 생성하여 할당합니다.
        this.medicine = new Medicine(medicineIds, medicineIntakeTime, scheduledMedicineIntakeDate);
    }

    public IntakeRequest(Long hospitalId) {
        // 병원 ID를 설정합니다.
        this.hospitalId = hospitalId;
    }

    /**
     * Medicine 클래스는 약의 세부 정보를 담고 있습니다.
     */
    public static class Medicine {
        private List<Long> medicineIds; // 약의 ID 목록

        private String medicineIntakeTime; // 약 복용 시간

        private String scheduledMedicineIntakeDate; // 예정된 약 복용 날짜

        // 생성자
        public Medicine(List<Long> medicineIds, String medicineIntakeTime, String scheduledMedicineIntakeDate) {
            this.medicineIds = medicineIds;
            this.medicineIntakeTime = medicineIntakeTime;
            this.scheduledMedicineIntakeDate = scheduledMedicineIntakeDate;
        }
    }
}
