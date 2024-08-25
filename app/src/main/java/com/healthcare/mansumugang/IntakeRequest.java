package com.healthcare.mansumugang;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IntakeRequest {

    private Long hospitalId;

    private Medicine medicine;

    // Constructors
    public IntakeRequest(List<Long> medicineIds, String medicineIntakeTime, String scheduledMedicineIntakeDate) {
        this.medicine = new Medicine(medicineIds, medicineIntakeTime, scheduledMedicineIntakeDate);
    }

    public IntakeRequest(Long hospitalId) {
        this.hospitalId = hospitalId;
    }


    public static class Medicine {
        private List<Long> medicineIds;

        private String medicineIntakeTime;

        private String scheduledMedicineIntakeDate;

        // Constructors
        public Medicine(List<Long> medicineIds, String medicineIntakeTime, String scheduledMedicineIntakeDate) {
            this.medicineIds = medicineIds;
            this.medicineIntakeTime = medicineIntakeTime;
            this.scheduledMedicineIntakeDate = scheduledMedicineIntakeDate;
        }


    }
}
