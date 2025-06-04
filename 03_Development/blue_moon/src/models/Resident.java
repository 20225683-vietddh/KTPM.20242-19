package models;

import java.sql.Date;
import java.time.LocalDate;

import utils.enums.Gender;
import utils.enums.RelationshipType;
import utils.enums.Role;

public class Resident {
    private String id;
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
    
    private RelationshipType relationship;
    
    private boolean isHouseholdHead;
    private int householdId;
    
    

    public Resident() {
    }

    public Resident(String id, int householdId, String fullName, Gender gender, Date dateOfBirth, 
                 String idCard, RelationshipType relationship, String occupation, boolean isHouseholdHead) {
        this.id = id;
        this.householdId = householdId;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.citizenId = idCard;
        this.relationship = relationship;
        this.occupation = occupation;
        this.isHouseholdHead = isHouseholdHead;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(int householdId) {
        this.householdId = householdId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Gender getGender() {
        return gender;
    }
    
    public String getGenderString() {
    	return gender.toString();
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public void setGender(String gender) {
    	this.gender = Gender.valueOf(gender.toUpperCase());
    
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getIdCard() {
        return citizenId;
    }

    public void setIdCard(String idCard) {
        this.citizenId = idCard;
    }

    public RelationshipType getRelationship() {
        return relationship;
    }
    
    public String getRelationshipString() {
    	return relationship != null ? relationship.name() : RelationshipType.UNKNOWN.name();
    }

    public void setRelationship(String relationshipStr) {
        try {
            this.relationship = RelationshipType.valueOf(relationshipStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            // If the string doesn't match any enum value, set to UNKNOWN
            this.relationship = RelationshipType.UNKNOWN;
        }
    }

    public void setRelationship(RelationshipType relationship) {
        this.relationship = relationship != null ? relationship : RelationshipType.UNKNOWN;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public boolean isHouseholdHead() {
        return isHouseholdHead;
    }

    public void setHouseholdHead(boolean householdHead) {
        isHouseholdHead = householdHead;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                "householdId=" + householdId +
                ", fullName='" + fullName + '\'' +
                ", relationship='" + relationship + '\'' +
                ", isHouseholdHead=" + isHouseholdHead +
                '}';
    }
}
