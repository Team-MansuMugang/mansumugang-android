// MedicineSchedule.java
package com.example.mansumugang;

import java.util.List;

public class MedicineSchedule {
    private String time;
    private List<Medicine> medicines;

    // 기본 생성자
    public MedicineSchedule() {}

    // 생성자
    public MedicineSchedule(String time, List<Medicine> medicines) {
        this.time = time;
        this.medicines = medicines;
    }

    // Getter와 Setter
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
}


