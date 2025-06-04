package models;

import java.time.LocalDate;

public class Resident {
    private int id;
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
    
    public Resident(int id, String fullName, LocalDate dateOfBirth, String gender, String ethnicity, 
                    String religion, String citizenId, LocalDate dateOfIssue, String placeOfIssue, 
                    String occupation, String notes, LocalDate addedDate, String relationshipWithHead, int householdId) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.ethnicity = ethnicity;
        this.religion = religion;
        this.citizenId = citizenId;
        this.dateOfIssue = dateOfIssue;
        this.placeOfIssue = placeOfIssue;
        this.occupation = occupation;
        this.notes = notes;
        this.addedDate = addedDate;
        this.relationshipWithHead = relationshipWithHead;
        this.householdId = householdId;
    }

    public Resident() {}
    
 // Thêm copy constructor
    public Resident(Resident other) {
        if (other != null) {
            this.id = other.id;
            this.fullName = other.fullName;
            this.dateOfBirth = other.dateOfBirth;
            this.gender = other.gender;
            this.ethnicity = other.ethnicity;
            this.religion = other.religion;
            this.citizenId = other.citizenId;
            this.dateOfIssue = other.dateOfIssue;
            this.placeOfIssue = other.placeOfIssue;
            this.occupation = other.occupation;
            this.notes = other.notes;
            this.addedDate = other.addedDate;
            this.relationshipWithHead = other.relationshipWithHead;
            this.householdId = other.householdId;
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEthnicity() { return ethnicity; }
    public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }

    public LocalDate getDateOfIssue() { return dateOfIssue; }
    public void setDateOfIssue(LocalDate dateOfIssue) { this.dateOfIssue = dateOfIssue; }

    public String getPlaceOfIssue() { return placeOfIssue; }
    public void setPlaceOfIssue(String placeOfIssue) { this.placeOfIssue = placeOfIssue; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDate addedDate) { this.addedDate = addedDate; }

    public String getRelationshipWithHead() { return relationshipWithHead; }
    public void setRelationshipWithHead(String relationshipWithHead) { this.relationshipWithHead = relationshipWithHead; }

    public int getHouseholdId() { return householdId; }
    public void setHouseholdId(int householdId) { this.householdId = householdId; }

    @Override
    public String toString() {
        return "Resident{" +
               "id=" + id +
               ", fullName='" + fullName + '\'' +
               ", dateOfBirth=" + dateOfBirth +
               ", gender='" + gender + '\'' +
               ", ethnicity='" + ethnicity + '\'' +
               ", religion='" + religion + '\'' +
               ", citizenId='" + citizenId + '\'' +
               ", dateOfIssue=" + dateOfIssue +
               ", placeOfIssue='" + placeOfIssue + '\'' +
               ", occupation='" + occupation + '\'' +
               ", notes='" + notes + '\'' +
               ", addedDate=" + addedDate +
               ", relationshipWithHead='" + relationshipWithHead + '\'' +
               ", householdId=" + householdId +
               '}';
    }
}
