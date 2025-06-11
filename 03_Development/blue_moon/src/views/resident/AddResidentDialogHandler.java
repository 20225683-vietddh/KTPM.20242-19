package views.resident;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import controllers.resident.ResidentController;
import exception.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Resident;
import services.resident.ResidentService;
import services.resident.ResidentServiceImpl;
import utils.Configs;
import utils.enums.Gender;
import utils.enums.RelationshipType;
import utils.FieldVerifier;
import utils.FieldVerifier.ValidationResult;
import utils.AlertUtils;
import utils.enums.Ethnicity;
import utils.enums.PlaceOfIssue;

public class AddResidentDialogHandler {
    
    private ResidentController residentController;
    private Runnable onSuccessCallback;
    
    // Text fields
    @FXML private TextField tfId;
    @FXML private TextField tfFullName;
    @FXML private DatePicker dpDateOfBirth;
    @FXML private ComboBox<Gender> cbGender;
    @FXML private ComboBox<Ethnicity> cbEthnicity;
    @FXML private CheckBox cbReligion; // Changed from TextField to CheckBox
    @FXML private TextField tfCitizenId;
    @FXML private DatePicker dpDateOfIssue;
    @FXML private ComboBox<PlaceOfIssue> cbPlaceOfIssue;
    @FXML private TextField tfOccupation;
    
    @FXML private ToggleButton toggleEditMode;
    @FXML private Button btnSave;
    @FXML private Button btnClose;
    @FXML private Label lblRequiredNote;
    @FXML private Label lblError;
    @FXML private VBox contentVBox;
    
    // Error labels
    @FXML private Label lblFullNameError;
    @FXML private Label lblDateOfBirthError;
    @FXML private Label lblGenderError;
    @FXML private Label lblEthnicityError;
    @FXML private Label lblReligionError;
    @FXML private Label lblCitizenIdError;
    @FXML private Label lblDateOfIssueError;
    @FXML private Label lblPlaceOfIssueError;
    @FXML private Label lblOccupationError;
    
    // Buttons
    @FXML private Button btnAdd;
    @FXML private Button btnCancel;
    
    // Constructor không tham số cho FXML
    public AddResidentDialogHandler() {
        // Initialize services if needed
    }
    
    // Constructor with controller
    public AddResidentDialogHandler(ResidentController residentController) {
        this.residentController = residentController;
    }
    
    @FXML
    private void initialize() {
        
        // Add real-time validation listeners
        addValidationListeners();
        
        // Initialize combo boxes
        initializeComboBoxes();
    }
    
    private void addValidationListeners() {
        // Full name validation
        tfFullName.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                hideError(lblFullNameError);
            }
        });
        
        // Date of birth validation
        dpDateOfBirth.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                hideError(lblDateOfBirthError);
            }
        });
        
        // Gender validation
        cbGender.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null ) {
                hideError(lblGenderError);
            }
        });
        
        // Ethnicity validation
        cbEthnicity.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                hideError(lblEthnicityError);
            }
        });
        
        // Citizen ID validation
        tfCitizenId.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                hideError(lblCitizenIdError);
            }
        });
        
        // Date of issue validation
        dpDateOfIssue.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                hideError(lblDateOfIssueError);
            }
        });
        
        // Place of issue validation
        cbPlaceOfIssue.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null ) {
                hideError(lblPlaceOfIssueError);
            }
        });
    }
    
    private void initializeComboBoxes() {
        // Initialize gender combo box
        cbGender.getItems().addAll(Gender.values());
        
        // Initialize ethnicity combo box
        cbEthnicity.getItems().addAll(Ethnicity.values());
        
        // Initialize place of issue combo box
        cbPlaceOfIssue.getItems().addAll(PlaceOfIssue.values());
        
    }
    
    private boolean validateRequiredFields() {
        boolean isValid = true;
        
        // Clear all previous errors
        hideAllErrors();
        
        // Validate full name
        ValidationResult fullNameResult = FieldVerifier.verifyNotEmpty(tfFullName.getText().trim(), "Họ và tên");
        if (!fullNameResult.isValid()) {
            showError(lblFullNameError, fullNameResult.getMessage());
            isValid = false;
        }
        
        // Validate date of birth
        if (dpDateOfBirth.getValue() == null) {
            showError(lblDateOfBirthError, "Ngày sinh không được để trống");
            isValid = false;
        } else {
            // Check if date is not in the future
            if (dpDateOfBirth.getValue().isAfter(LocalDate.now())) {
                showError(lblDateOfBirthError, "Ngày sinh không được là ngày trong tương lai");
                isValid = false;
            }
        }
        
        // Validate gender
        ValidationResult genderResult = FieldVerifier.verifyNotEmpty(cbGender.getValue().toString(), "Giới tính");
        if (!genderResult.isValid()) {
            showError(lblGenderError, genderResult.getMessage());
            isValid = false;
        }
        
        // Validate ethnicity
        ValidationResult ethnicityResult = FieldVerifier.verifyNotEmpty(cbEthnicity.getValue().toString(), "Dân tộc");
        if (!ethnicityResult.isValid()) {
            showError(lblEthnicityError, ethnicityResult.getMessage());
            isValid = false;
        }
        
        // Validate citizen ID
        ValidationResult citizenIdResult = FieldVerifier.verifyCitizenId(tfCitizenId.getText().trim());
        if (!citizenIdResult.isValid()) {
            showError(lblCitizenIdError, citizenIdResult.getMessage());
            isValid = false;
        } else {
            // Check if citizen ID already exists
            try {
                if (residentController.getResidentByCitizenId(tfCitizenId.getText().trim()) != null) {
                    showError(lblCitizenIdError, "CCCD/CMND này đã tồn tại trong hệ thống");
                    isValid = false;
                }
            } catch (Exception e) {
                // Citizen ID doesn't exist, which is good for new resident
            }
        }
        
        // Validate date of issue
        if (dpDateOfIssue.getValue() == null) {
            showError(lblDateOfIssueError, "Ngày cấp không được để trống");
            isValid = false;
        } else {
            // Check if date of issue is not in the future
            if (dpDateOfIssue.getValue().isAfter(LocalDate.now())) {
                showError(lblDateOfIssueError, "Ngày cấp không được là ngày trong tương lai");
                isValid = false;
            }
            // Check if date of issue is not before date of birth
            if (dpDateOfBirth.getValue() != null && dpDateOfIssue.getValue().isBefore(dpDateOfBirth.getValue())) {
                showError(lblDateOfIssueError, "Ngày cấp không được trước ngày sinh");
                isValid = false;
            }
        }
        
        // Validate place of issue
        ValidationResult placeOfIssueResult = FieldVerifier.verifyNotEmpty(cbPlaceOfIssue.getValue().toString(), "Nơi cấp");
        if (!placeOfIssueResult.isValid()) {
            showError(lblPlaceOfIssueError, placeOfIssueResult.getMessage());
            isValid = false;
        }
        
        return isValid;
    }
    
    private void hideAllErrors() {
        hideError(lblFullNameError);
        hideError(lblDateOfBirthError);
        hideError(lblGenderError);
        hideError(lblEthnicityError);
        hideError(lblReligionError);
        hideError(lblCitizenIdError);
        hideError(lblDateOfIssueError);
        hideError(lblPlaceOfIssueError);
        hideError(lblOccupationError);
    }
    
    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void hideError(Label errorLabel) {
        errorLabel.setVisible(false);
    }
    
    private Resident createResidentFromInput() {
        Resident resident = new Resident();
        
        resident.setFullName(tfFullName.getText().trim());
        resident.setDateOfBirth(dpDateOfBirth.getValue());
        resident.setGender(cbGender.getValue().name());
        resident.setEthnicity(cbEthnicity.getValue().name());
        resident.setReligion(cbReligion.isSelected() ? true : false); // Store as "Yes" or "No"
        resident.setCitizenId(tfCitizenId.getText().trim());
        resident.setDateOfIssue(dpDateOfIssue.getValue());
        resident.setPlaceOfIssue(cbPlaceOfIssue.getValue().name());
        resident.setOccupation(tfOccupation.getText().trim());
        resident.setRelationship(RelationshipType.UNKNOWN); // Set default relationship to UNKNOWN
        resident.setAddedDate(LocalDate.now());
        
        return resident;
    }
    
    @FXML
    private void handleAdd() {
        try {
            // Validate all required fields first
            if (!validateRequiredFields()) {
                return;
            }
            
            // Create resident from validated input
            Resident newResident = createResidentFromInput();
            
            residentController.addResident(newResident);
            
            // Show success message
            AlertUtils.showSuccessDialog("Thêm nhân khẩu thành công", 
                "Nhân khẩu mới đã được thêm vào hệ thống.\n" +
                "Bạn có thể chuyển sang quản lý hộ khẩu để thêm nhân khẩu này vào hộ khẩu cụ thể.");
            
            // Call success callback
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            
            // Close dialog
            ((Stage) btnAdd.getScene().getWindow()).close();
            
        } catch (Exception e) {
            AlertUtils.showErrorAlert("Lỗi", "Lỗi hệ thống", "Có lỗi xảy ra khi thêm nhân khẩu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
    
    // Getters and setters
    public void setOnSuccessCallback(Runnable callback) {
        this.onSuccessCallback = callback;
    }
    
    public ResidentController getResidentController() {
        return residentController;
    }
    
    public void setResidentController(ResidentController residentController) {
        this.residentController = residentController;
    }
    
}