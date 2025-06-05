package utils;

import java.util.regex.Pattern;

import services.resident.ResidentService;
import services.resident.ResidentServiceImpl;

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
     * Validates member count (positive integer, reasonable range)
     */
    public static boolean isValidResidentCount(String residentCount) {
        if (!isNotEmpty(residentCount)) {
            return false;
        }
        try {
            int count = Integer.parseInt(residentCount.trim());
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
    public static String getInvalidResidentCountErrorMessage() {
        return "Số thành viên phải là số nguyên dương (1-20)";
    }
    
    public static String getInvalidCitizenIdErrorMessage() {
        return "CCCD không hợp lệ (phải có 12 chữ số) hoặc đã tồn tại trong hệ thống";
    }
    
    public static boolean isValidCitizenId(String citizenId) {
        if (citizenId == null || citizenId.trim().isEmpty()) {
            return false;
        }
        
        // Check format: 12 digits
        if (!citizenId.matches("\\d{12}")) {
            return false;
        }
        
        // Check if exists in database
        ResidentService residentService = new ResidentServiceImpl();
        return !residentService.citizenIdExists(citizenId); // Return false if already exists
    }
}