package models.statistic;

import java.time.LocalDate;

public class ChangeHistoryInfo {
    private int residentId;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private int householdId;
    private String changeType;
    private LocalDate changeDate;

    public ChangeHistoryInfo(int residentId, String fullName, LocalDate dateOfBirth, String gender, int householdId, String changeType, LocalDate changeDate) {
        this.residentId = residentId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.householdId = householdId;
        this.changeType = changeType;
        this.changeDate = changeDate;
    }

    public int getResidentId() { return residentId; }
    public String getFullName() { return fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public int getHouseholdId() { return householdId; }
    public String getChangeType() { return changeType; }
    public LocalDate getChangeDate() { return changeDate; }
}
