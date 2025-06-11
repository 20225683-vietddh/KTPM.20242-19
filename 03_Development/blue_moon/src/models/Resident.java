package models;


import java.time.LocalDate;

import utils.enums.Ethnicity;
import utils.enums.Gender;
import utils.enums.PlaceOfIssue;
import utils.enums.RelationshipType;
import utils.enums.Role;

public class Resident {
    private int id;
    private String fullName;    
    private LocalDate dateOfBirth;
    private Gender gender;
    private Ethnicity ethnicity;
    private boolean religion;    
    private String citizenId;      
    private LocalDate dateOfIssue;
    private PlaceOfIssue placeOfIssue;
    private RelationshipType relationship;    
    private String occupation;
    private LocalDate addedDate;    
    private int householdId;    
    private boolean isHouseholdHead;
    
    

    public Resident() {
    }

    //full construtor
	public Resident(int id, 
			String fullName, 
			LocalDate dateOfBirth, 
			Gender gender, 
			Ethnicity ethnicity, 
			boolean religion,
			String citizenId, 
			LocalDate dateOfIssue, 
			PlaceOfIssue placeOfIssue, 
			RelationshipType relationship,
			String occupation, 
			LocalDate addedDate, 
			int householdId,
			boolean isHouseholdHead ) {
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
		this.addedDate = addedDate;
		this.relationship = relationship;
		this.isHouseholdHead = isHouseholdHead;
		this.householdId = householdId;
	}

	
	
	//TODO: ????
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
            this.addedDate = other.addedDate;
            this.relationship = other.relationship;
            this.isHouseholdHead = other.isHouseholdHead;
            this.householdId = other.householdId;
        }
    }


	// Getters and Setters
    public int getId() {
        return id;
    }
    public String getIdString() {
        return id+"";
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public Ethnicity getEthnicity() {
		return ethnicity;
	}
    
    public String getEthnicityString() {
		return ethnicity.toString();
	}

	public void setEthnicity(Ethnicity ethnicity) {
		this.ethnicity = ethnicity;
	}
	
	public void setEthnicity(String ethnicityStr) {
		try {
			this.ethnicity = Ethnicity.valueOf(ethnicityStr.toUpperCase());
		}
		catch (IllegalArgumentException e) {
            // If the string doesn't match any enum value, set to UNKNOWN
            this.ethnicity = Ethnicity.OTHER;
        }
		
	}

	public boolean isReligion() {
		return religion;
	}

	public void setReligion(boolean religion) {
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

	public PlaceOfIssue getPlaceOfIssue() {
		return placeOfIssue;
	}

	public String getPlaceOfIssueString() {
		return placeOfIssue.toString();
	}

	
	public void setPlaceOfIssue(PlaceOfIssue placeOfIssue) {
		this.placeOfIssue = placeOfIssue;
	}
	
	public void setPlaceOfIssue(String placeStr) {
		try {
			this.placeOfIssue = PlaceOfIssue.valueOf(placeStr.toUpperCase());
		}
		catch (IllegalArgumentException e) {
            // If the string doesn't match any enum value, set to UNKNOWN
            this.placeOfIssue = PlaceOfIssue.OTHER;
        }
		
	}


	public LocalDate getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(LocalDate addedDate) {
		this.addedDate = addedDate;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
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
    
    public String getRelationshipDisplayName() {
    	return relationship != null ? relationship.toString() : RelationshipType.UNKNOWN.toString();
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

//    @Override
//    public String toString() {
//        return "Member{" +
//                "id=" + id +
//                "householdId=" + householdId +
//                ", fullName='" + fullName + '\'' +
//                ", relationship='" + relationship + '\'' +
//                ", isHouseholdHead=" + isHouseholdHead +
//                '}';
//    }
    
    @Override
    public String toString() {
        return "Resident{" +
               "id=" + id +
               ", fullName='" + fullName + '\'' +
               ", dateOfBirth=" + dateOfBirth +
               ", gender='" + gender.toString() + '\'' +
               ", ethnicity='" + ethnicity + '\'' +
               ", religion='" + religion + '\'' +
               ", citizenId='" + citizenId + '\'' +
               ", dateOfIssue=" + dateOfIssue +
               ", placeOfIssue='" + placeOfIssue + '\'' +
               ", occupation='" + occupation + '\'' +
               ", addedDate=" + addedDate +
               ", relationship='" + relationship.toString() + '\'' +
               ", isHouseholdHead=" + isHouseholdHead +
               ", householdId=" + householdId + 
               '}';
    }
}

