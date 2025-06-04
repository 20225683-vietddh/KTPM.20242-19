package utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import utils.enums.Gender;
import utils.enums.RelationshipType;

public class RelationshipHelper {

    public static RelationshipType determineRelationship(String birthDate, Gender gender) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dob = LocalDate.parse(birthDate, formatter);
        int age = Period.between(dob, LocalDate.now()).getYears();

        if (age >= 60) {
            if (gender == Gender.MALE) {
                return RelationshipType.GRANDFATHER;
            } else if (gender == Gender.FEMALE) {
                return RelationshipType.GRANDMOTHER;
            }
        } else if (age >= 30) {
            if (gender == Gender.MALE) {
                return RelationshipType.FATHER;
            } else if (gender == Gender.FEMALE) {
                return RelationshipType.MOTHER;
            }
        } else if (age >= 0) {
            if (gender == Gender.MALE) {
                return RelationshipType.SON;
            } else if (gender == Gender.FEMALE) {
                return RelationshipType.DAUGHTER;
            }
        }

        return RelationshipType.UNKNOWN;
    }

    public static String getDisplayText(RelationshipType type) {
        if (type == null) return "";
        return type.name();
    }
    
    public static RelationshipType fromDisplayText(String displayText) {
        if (displayText == null || displayText.isEmpty()) {
            return null;
        }
        
        try {
            return RelationshipType.valueOf(displayText.toUpperCase());
        } catch (IllegalArgumentException e) {
            return RelationshipType.UNKNOWN;
        }
    }
}