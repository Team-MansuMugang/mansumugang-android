package com.healthcare.mansumugang;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IntakeRequest {

    @SerializedName("hospitalId")
    private Long hospitalId;

    @SerializedName("medicine")
    private Medicine medicine;

    // Constructors
    public IntakeRequest(List<Long> medicineIds, String medicineIntakeTime, String scheduledMedicineIntakeDate) {
        this.medicine = new Medicine(medicineIds, medicineIntakeTime, scheduledMedicineIntakeDate);
    }

    public IntakeRequest(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    // Getters and Setters
    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public static class Medicine {
        @SerializedName("medicineIds")
        private List<Long> medicineIds;

        @SerializedName("medicineIntakeTime")
        private String medicineIntakeTime;

        @SerializedName("scheduledMedicineIntakeDate")
        private String scheduledMedicineIntakeDate;

        // Constructors
        public Medicine(List<Long> medicineIds, String medicineIntakeTime, String scheduledMedicineIntakeDate) {
            this.medicineIds = medicineIds;
            this.medicineIntakeTime = medicineIntakeTime;
            this.scheduledMedicineIntakeDate = scheduledMedicineIntakeDate;
        }

        // Getters and Setters
        public List<Long> getMedicineIds() {
            return medicineIds;
        }

        public void setMedicineIds(List<Long> medicineIds) {
            this.medicineIds = medicineIds;
        }

        public String getMedicineIntakeTime() {
            return medicineIntakeTime;
        }

        public void setMedicineIntakeTime(String medicineIntakeTime) {
            this.medicineIntakeTime = medicineIntakeTime;
        }

        public String getScheduledMedicineIntakeDate() {
            return scheduledMedicineIntakeDate;
        }

        public void setScheduledMedicineIntakeDate(String scheduledMedicineIntakeDate) {
            this.scheduledMedicineIntakeDate = scheduledMedicineIntakeDate;
        }
    }
}
