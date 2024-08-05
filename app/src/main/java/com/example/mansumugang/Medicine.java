package com.example.mansumugang;
public class Medicine {
    private String status;
    private String medicineIntakeTime;
    private int medicineId;
    private String medicineImageName;
    private String hospitalName;
    private String medicineDescription;
    private String medicineName;
    // 기본 생성자
    public Medicine() {}

    // 생성자
    public Medicine(String status, String medicineIntakeTime, int medicineId, String medicineImageName,
                    String hospitalName, String medicineDescription, String medicineName, String imageApiUrlPrefix) {
        this.status = status;
        this.medicineIntakeTime = medicineIntakeTime;
        this.medicineId = medicineId;
        this.medicineImageName = medicineImageName;
        this.hospitalName = hospitalName;
        this.medicineDescription = medicineDescription;
        this.medicineName = medicineName;
    }

    // Getter와 Setter
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
