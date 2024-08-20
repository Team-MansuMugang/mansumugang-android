package com.healthcare.mansumugang;

public class SaveResponse {
    private String message;
    private String recordFileName;
    private int recordDuration;
    private String name;

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecordFileName() {
        return recordFileName;
    }

    public void setRecordFileName(String recordFileName) {
        this.recordFileName = recordFileName;
    }

    public int getRecordDuration() {
        return recordDuration;
    }

    public void setRecordDuration(int recordDuration) {
        this.recordDuration = recordDuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
