package models;

import java.time.LocalDate;

public class ChangeHistoryRecord {
    private int recordId;
    private String changeType;
    private LocalDate changeDate;
    private int residentId;
    private int householdId;
    
    // Thêm thông tin hiển thị thân thiện
    private String residentName;
    private String householdName;
    
    // Constructors
    public ChangeHistoryRecord() {}
    
    public ChangeHistoryRecord(String changeType, LocalDate changeDate, int residentId, int householdId) {
        this.changeType = changeType;
        this.changeDate = changeDate;
        this.residentId = residentId;
        this.householdId = householdId;
    }
    
    public ChangeHistoryRecord(int recordId, String changeType, LocalDate changeDate, int residentId, int householdId) {
        this.recordId = recordId;
        this.changeType = changeType;
        this.changeDate = changeDate;
        this.residentId = residentId;
        this.householdId = householdId;
    }
    
    public ChangeHistoryRecord(int recordId, String changeType, LocalDate changeDate, 
                              int residentId, int householdId, String residentName, String householdName) {
        this.recordId = recordId;
        this.changeType = changeType;
        this.changeDate = changeDate;
        this.residentId = residentId;
        this.householdId = householdId;
        this.residentName = residentName;
        this.householdName = householdName;
    }
    
    // Getters and Setters
    public int getRecordId() {
        return recordId;
    }
    
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }
    
    public String getChangeType() {
        return changeType;
    }
    
    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
    
    public LocalDate getChangeDate() {
        return changeDate;
    }
    
    public void setChangeDate(LocalDate changeDate) {
        this.changeDate = changeDate;
    }
    
    public int getResidentId() {
        return residentId;
    }
    
    public void setResidentId(int residentId) {
        this.residentId = residentId;
    }
    
    public int getHouseholdId() {
        return householdId;
    }
    
    public void setHouseholdId(int householdId) {
        this.householdId = householdId;
    }
    
    public String getResidentName() {
        return residentName;
    }
    
    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }
    
    public String getHouseholdName() {
        return householdName;
    }
    
    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }
    
    @Override
    public String toString() {
        return "ChangeHistoryRecord{" +
                "recordId=" + recordId +
                ", changeType='" + changeType + '\'' +
                ", changeDate=" + changeDate +
                ", residentId=" + residentId +
                ", householdId=" + householdId +
                '}';
    }
    
    // Constants for change types
    public static final String CHANGE_TYPE_ADD = "ADD";
    public static final String CHANGE_TYPE_UPDATE = "UPDATE";
    public static final String CHANGE_TYPE_DELETE = "DELETE";
} 