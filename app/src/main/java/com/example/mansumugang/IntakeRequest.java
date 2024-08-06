package com.example.mansumugang;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IntakeRequest {

    @SerializedName("medicineIds")
    private List<Long> medicineIds;

    @SerializedName("medicineIntakeTime")
    private String medicineIntakeTime;

    @SerializedName("scheduledMedicineIntakeDate")
    private String scheduledMedicineIntakeDate;

    // Constructors
    public IntakeRequest(List<Long> medicineIds, String medicineIntakeTime, String scheduledMedicineIntakeDate) {
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
