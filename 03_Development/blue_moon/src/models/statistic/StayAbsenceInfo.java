package models.statistic;

import java.time.LocalDate;

public class StayAbsenceInfo {
    private int residentId;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private int householdId;
    private LocalDate createdDate;
    private String period;
    private String requestDesc;

    public StayAbsenceInfo(int residentId, String fullName, String gender, LocalDate dateOfBirth,
                           int householdId, LocalDate createdDate, String period, String requestDesc) {

        this.residentId = residentId;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.householdId = householdId;
        this.createdDate = createdDate;
        this.period = period;
        this.requestDesc = requestDesc;
    }

    
    public int getResidentId() { return residentId; }
    public String getFullName() { return fullName; }
    public String getGender() { return gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public int getHouseholdId() { return householdId; }
    public LocalDate getCreatedDate() { return createdDate; }
    public String getPeriod() { return period; }
    public String getRequestDesc() { return requestDesc; }

    
    public void setResidentId(int residentId) { this.residentId = residentId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setGender(String gender) { this.gender = gender; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setHouseholdId(int householdId) { this.householdId = householdId; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
    public void setPeriod(String period) { this.period = period; }
    public void setRequestDesc(String requestDesc) { this.requestDesc = requestDesc; }
}