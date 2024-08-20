package com.healthcare.mansumugang;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IntakeResponse {

    @SerializedName("toggleResponses")
    private List<ToggleResponse> toggleResponses;

    // Getters and Setters
    public List<ToggleResponse> getToggleResponses() {
        return toggleResponses;
    }

    public void setToggleResponses(List<ToggleResponse> toggleResponses) {
        this.toggleResponses = toggleResponses;
    }

    // Inner class for ToggleResponse
    public static class ToggleResponse {

        @SerializedName("medicineId")
        private Long medicineId;

        @SerializedName("status")
        private String status;

        // Constructors
        public ToggleResponse(Long medicineId, String status) {
            this.medicineId = medicineId;
            this.status = status;
        }

        // Getters and Setters
        public Long getMedicineId() {
            return medicineId;
        }

        public void setMedicineId(Long medicineId) {
            this.medicineId = medicineId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}