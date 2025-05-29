package utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class RelationshipHelper {

    public static RelationshipType determineRelationship(String birthDate, Gender gender) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dob = LocalDate.parse(birthDate, formatter);
        int age = Period.between(dob, LocalDate.now()).getYears();

        if (age >= 60) {
            if (gender == Gender.MALE) {
                return RelationshipType.GRAND_FATHER;
            } else if (gender == Gender.FEMALE) {
                return RelationshipType.GRAND_MOTHER;
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
}