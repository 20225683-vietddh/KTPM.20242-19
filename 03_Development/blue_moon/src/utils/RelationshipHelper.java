package utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import utils.enums.Gender;
import utils.enums.RelationshipType;

public class RelationshipHelper {

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