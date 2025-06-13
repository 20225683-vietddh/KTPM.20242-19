package models;

import java.time.LocalDate;

public class StayAbsenceRecord {
    private int recordId;
    private String recordType;
    private LocalDate createdDate;
    private String tempAddress;
    private String period;
    private String requestDesc;
    
    // Link to existing resident (for temporary absence)
    private Integer residentId;
    private String residentName; // For display
    
    // Link to household (for both types)
    private Integer householdId;
    private String householdName; // For display
    
    // Temporary resident info (only for TEMPORARY_STAY)
    private String tempResidentName;
    private String tempResidentCccd;
    private LocalDate tempResidentBirthDate;
    private String tempResidentGender;
    private String tempResidentPhone;
    private String tempResidentHometown;
    
    // Validity period
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    
    // Constants
    public static final String TYPE_TEMPORARY_STAY = "TEMPORARY_STAY";
    public static final String TYPE_TEMPORARY_ABSENCE = "TEMPORARY_ABSENCE";
    
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_EXPIRED = "EXPIRED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    
    // Constructors
    public StayAbsenceRecord() {
        this.createdDate = LocalDate.now();
        this.status = STATUS_ACTIVE;
    }
    
    public StayAbsenceRecord(String recordType, String requestDesc, Integer householdId, 
                           LocalDate startDate, LocalDate endDate) {
        this();
        this.recordType = recordType;
        this.requestDesc = requestDesc;
        this.householdId = householdId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Getters and Setters
    public int getRecordId() {
        return recordId;
    }
    
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }
    
    public String getRecordType() {
        return recordType;
    }
    
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getTempAddress() {
        return tempAddress;
    }
    
    public void setTempAddress(String tempAddress) {
        this.tempAddress = tempAddress;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public String getRequestDesc() {
        return requestDesc;
    }
    
    public void setRequestDesc(String requestDesc) {
        this.requestDesc = requestDesc;
    }
    
    public Integer getResidentId() {
        return residentId;
    }
    
    public void setResidentId(Integer residentId) {
        this.residentId = residentId;
    }
    
    public String getResidentName() {
        return residentName;
    }
    
    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }
    
    public Integer getHouseholdId() {
        return householdId;
    }
    
    public void setHouseholdId(Integer householdId) {
        this.householdId = householdId;
    }
    
    public String getHouseholdName() {
        return householdName;
    }
    
    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }
    
    public String getTempResidentName() {
        return tempResidentName;
    }
    
    public void setTempResidentName(String tempResidentName) {
        this.tempResidentName = tempResidentName;
    }
    
    public String getTempResidentCccd() {
        return tempResidentCccd;
    }
    
    public void setTempResidentCccd(String tempResidentCccd) {
        this.tempResidentCccd = tempResidentCccd;
    }
    
    public LocalDate getTempResidentBirthDate() {
        return tempResidentBirthDate;
    }
    
    public void setTempResidentBirthDate(LocalDate tempResidentBirthDate) {
        this.tempResidentBirthDate = tempResidentBirthDate;
    }
    
    public String getTempResidentGender() {
        return tempResidentGender;
    }
    
    public void setTempResidentGender(String tempResidentGender) {
        this.tempResidentGender = tempResidentGender;
    }
    
    public String getTempResidentPhone() {
        return tempResidentPhone;
    }
    
    public void setTempResidentPhone(String tempResidentPhone) {
        this.tempResidentPhone = tempResidentPhone;
    }
    
    public String getTempResidentHometown() {
        return tempResidentHometown;
    }
    
    public void setTempResidentHometown(String tempResidentHometown) {
        this.tempResidentHometown = tempResidentHometown;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Helper methods
    public boolean isTemporaryStay() {
        return TYPE_TEMPORARY_STAY.equals(recordType);
    }
    
    public boolean isTemporaryAbsence() {
        return TYPE_TEMPORARY_ABSENCE.equals(recordType);
    }
    
    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }
    
    public boolean isExpired() {
        return STATUS_EXPIRED.equals(status) || 
               (endDate != null && endDate.isBefore(LocalDate.now()));
    }
    
    @Override
    public String toString() {
        return "StayAbsenceRecord{" +
                "recordId=" + recordId +
                ", recordType='" + recordType + '\'' +
                ", createdDate=" + createdDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                '}';
    }
} 