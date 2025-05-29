package models;

import java.sql.Date;

import utils.Gender;
import utils.RelationshipType;
import utils.Role;

public class Member {
    private String id;
    private int householdId;
    private String fullName;
    private Gender gender;
    private Date dateOfBirth;
    private String idCard;
    private RelationshipType relationship;
    private String occupation;
    private boolean isHouseholdHead;

    public Member() {
    }

    public Member(String id, int householdId, String fullName, Gender gender, Date dateOfBirth, 
                 String idCard, RelationshipType relationship, String occupation, boolean isHouseholdHead) {
        this.id = id;
        this.householdId = householdId;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.idCard = idCard;
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
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public RelationshipType getRelationship() {
        return relationship;
    }
    
    public String getRelationshipString() {
    	return relationship.toString();
    }

    public void setRelationship(RelationshipType relationship) {
        this.relationship = relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = RelationshipType.valueOf(relationship.toUpperCase());
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
