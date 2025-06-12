package models;

import java.time.LocalDate;

public class Resident {
    private int residentId;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String ethnicity;
    private String religion;
    private String citizenId;
    private LocalDate dateOfIssue;
    private String placeOfIssue;
    private String occupation;
    private String notes;
    private LocalDate addedDate;
    private String relationshipWithHead;
    private int householdId;
    
    public Resident() {}
    
    public int getResidentId() {
        return residentId;
    }
    
    public void setResidentId(int residentId) {
        this.residentId = residentId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getEthnicity() {
        return ethnicity;
    }
    
    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }
    
    public String getReligion() {
        return religion;
    }
    
    public void setReligion(String religion) {
        this.religion = religion;
    }
    
    public String getCitizenId() {
        return citizenId;
    }
    
    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }
    
    public LocalDate getDateOfIssue() {
        return dateOfIssue;
    }
    
    public void setDateOfIssue(LocalDate dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }
    
    public String getPlaceOfIssue() {
        return placeOfIssue;
    }
    
    public void setPlaceOfIssue(String placeOfIssue) {
        this.placeOfIssue = placeOfIssue;
    }
    
    public String getOccupation() {
        return occupation;
    }
    
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDate getAddedDate() {
        return addedDate;
    }
    
    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }
    
    public String getRelationshipWithHead() {
        return relationshipWithHead;
    }
    
    public void setRelationshipWithHead(String relationshipWithHead) {
        this.relationshipWithHead = relationshipWithHead;
    }
    
    public int getHouseholdId() {
        return householdId;
    }
    
    public void setHouseholdId(int householdId) {
        this.householdId = householdId;
    }
} 