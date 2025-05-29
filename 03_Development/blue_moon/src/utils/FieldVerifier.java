package utils;

import java.util.regex.Pattern;

public class FieldVerifier {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Phone number pattern (9-10 digits, no spaces)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9,10}$");
    
    // ID pattern (assuming alphanumeric)
    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    
    /**
     * Validates if a string is not null and not empty after trimming
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validates phone number (9-10 digits, no spaces)
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (!isNotEmpty(phone)) {
            return false;
        }
        // Remove all spaces and check if it matches the pattern
        String cleanPhone = phone.replaceAll("\\s+", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }
    
    /**
     * Validates ID format (alphanumeric)
     */
    public static boolean isValidId(String id) {
        if (!isNotEmpty(id)) {
            return false;
        }
        return ID_PATTERN.matcher(id.trim()).matches();
    }
    
    /**
     * Validates name (not empty, contains only letters and spaces)
     */
    public static boolean isValidName(String name) {
        if (!isNotEmpty(name)) {
            return false;
        }
        // Allow letters, spaces, and Vietnamese characters
        return name.trim().matches("^[a-zA-ZÀ-ỹ\\s]+$");
    }
    
    /**
     * Validates address (not empty)
     */
    public static boolean isValidAddress(String address) {
        return isNotEmpty(address);
    }
    
    /**
     * Validates area (not empty)
     */
    public static boolean isValidArea(String area) {
        return isNotEmpty(area);
    }
    
    /**
     * Validates household number (positive integer)
     */
    public static boolean isValidHouseholdNumber(String householdNumber) {
        if (!isNotEmpty(householdNumber)) {
            return false;
        }
        try {
            int number = Integer.parseInt(householdNumber.trim());
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates member count (positive integer, reasonable range)
     */
    public static boolean isValidMemberCount(String memberCount) {
        if (!isNotEmpty(memberCount)) {
            return false;
        }
        try {
            int count = Integer.parseInt(memberCount.trim());
            return count > 0 && count <= 20; // Reasonable limit for household size
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Get error message for email validation
     */
    public static String getEmailErrorMessage() {
        return "Email không đúng định dạng";
    }
    
    /**
     * Get error message for phone validation
     */
    public static String getPhoneErrorMessage() {
        return "Số điện thoại phải có 9-10 chữ số, không có khoảng trắng";
    }
    
    /**
     * Get error message for empty field
     */
    public static String getEmptyFieldErrorMessage() {
        return "Trường này không được để trống";
    }
    
    /**
     * Get error message for invalid ID
     */
    public static String getInvalidIdErrorMessage() {
        return "ID không hợp lệ (chỉ chứa chữ và số)";
    }
    
    /**
     * Get error message for invalid name
     */
    public static String getInvalidNameErrorMessage() {
        return "Tên không hợp lệ (chỉ chứa chữ cái và khoảng trắng)";
    }
    
    /**
     * Get error message for invalid member count
     */
    public static String getInvalidMemberCountErrorMessage() {
        return "Số thành viên phải là số nguyên dương (1-20)";
    }
}