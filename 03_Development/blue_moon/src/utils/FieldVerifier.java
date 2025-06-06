package utils;

import java.util.regex.Pattern;

import services.resident.ResidentService;
import services.resident.ResidentServiceImpl;
import exception.ServiceException;
import models.Resident;

public class FieldVerifier {
    
    // Regular expression patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9,10}$");
    private static final Pattern CITIZEN_ID_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZÀ-ỹ\\s]+$");
    
    public static class ValidationResult {
        private final boolean isValid;
        private final String message;

        public ValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getMessage() {
            return message;
        }
        
        // Helper method to create success result
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        // Helper method to create error result
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
    }

    /**
     * Kiểm tra một trường có bị trống không
     */
    public static ValidationResult verifyNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(fieldName + " không được để trống!");
        }
        return ValidationResult.success();
    }

    /**
     * Kiểm tra email có hợp lệ không
     */
    public static ValidationResult verifyEmail(String email, String fieldName) {
        // Kiểm tra trống
        ValidationResult emptyCheck = verifyNotEmpty(email, fieldName);
        if (!emptyCheck.isValid()) {
            return emptyCheck;
        }

        // Kiểm tra định dạng
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return ValidationResult.error("Email không đúng định dạng!");
        }

        return ValidationResult.success();
    }

    /**
     * Kiểm tra số điện thoại có hợp lệ không
     */
    public static ValidationResult verifyPhoneNumber(String phone, String fieldName) {
        // Kiểm tra trống
        ValidationResult emptyCheck = verifyNotEmpty(phone, fieldName);
        if (!emptyCheck.isValid()) {
            return emptyCheck;
        }

        // Xóa khoảng trắng và kiểm tra định dạng
        String cleanPhone = phone.trim().replaceAll("\\s+", "");
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            return ValidationResult.error("Số điện thoại phải có 9-10 chữ số!");
        }

        return ValidationResult.success();
    }

    /**
     * Kiểm tra tên có hợp lệ không
     */
    public static ValidationResult verifyName(String name, String fieldName) {
        // Kiểm tra trống
        ValidationResult emptyCheck = verifyNotEmpty(name, fieldName);
        if (!emptyCheck.isValid()) {
            return emptyCheck;
        }

        // Kiểm tra định dạng
        if (!NAME_PATTERN.matcher(name.trim()).matches()) {
            return ValidationResult.error("Tên chỉ được chứa chữ cái và khoảng trắng!");
        }

        return ValidationResult.success();
    }

    /**
     * Kiểm tra số có hợp lệ không
     */
    public static ValidationResult verifyNumber(String value, String fieldName) {
        // Kiểm tra trống
        ValidationResult emptyCheck = verifyNotEmpty(value, fieldName);
        if (!emptyCheck.isValid()) {
            return emptyCheck;
        }

        try {
            Double.parseDouble(value.trim());
            return ValidationResult.success();
        } catch (NumberFormatException e) {
            return ValidationResult.error(fieldName + " phải là số hợp lệ!");
        }
    }

    /**
     * Kiểm tra số nguyên dương có hợp lệ không và nằm trong khoảng cho phép
     */
    public static ValidationResult verifyPositiveInteger(String value, String fieldName, int minValue, int maxValue) {
        // Kiểm tra trống
        ValidationResult emptyCheck = verifyNotEmpty(value, fieldName);
        if (!emptyCheck.isValid()) {
            return emptyCheck;
        }

        try {
            int number = Integer.parseInt(value.trim());
            if (number < minValue || number > maxValue) {
                return ValidationResult.error(fieldName + " phải nằm trong khoảng " + minValue + " đến " + maxValue + "!");
            }
            return ValidationResult.success();
        } catch (NumberFormatException e) {
            return ValidationResult.error(fieldName + " phải là số nguyên!");
        }
    }

    /**
     * Kiểm tra số thành viên trong hộ khẩu có hợp lệ không
     */
    public static ValidationResult verifyResidentCount(String value) {
        return verifyPositiveInteger(value, "Số thành viên", 1, 20);
    }

    /**
     * Kiểm tra CCCD có hợp lệ không
     */
    public static ValidationResult verifyCitizenId(String citizenId) {
        // Kiểm tra trống
        ValidationResult emptyCheck = verifyNotEmpty(citizenId, "CCCD");
        if (!emptyCheck.isValid()) {
            return emptyCheck;
        }

        // Kiểm tra định dạng
        if (!CITIZEN_ID_PATTERN.matcher(citizenId.trim()).matches()) {
            return ValidationResult.error("CCCD phải có đúng 12 chữ số!");
        }
            return ValidationResult.success();
    }

}