package views.resident;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import controllers.household.HouseholdController;
import controllers.resident.ResidentController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import models.Household;
import models.Resident;
import services.resident.ResidentService;
import services.resident.ResidentServiceImpl;
import services.room.RoomService;
import services.room.RoomServiceImpl;
import utils.enums.Ethnicity;
import utils.enums.Gender;
import utils.enums.PlaceOfIssue;
import utils.enums.RelationshipType;

public class ViewResidentDialogHandler {

	// You need to initialize this
	private ResidentController residentController; // You need to initialize this
	private HouseholdController householdController;

	private Runnable onSuccessCallback;
	
	public HouseholdController getHouseholdController() {
		return householdController;
	}

	public void setHouseholdController(HouseholdController householdController) {
		this.householdController = householdController;
	}

	public Runnable getOnSuccessCallback() {
		return onSuccessCallback;
	}

	public void setOnSuccessCallback(Runnable onSuccessCallback) {
		this.onSuccessCallback = onSuccessCallback;
	}

	private boolean editMode = false;

	// Data
	private Resident resident;

	public ResidentController getResidentController() {
		return residentController;
	}

	public void setResidentController(ResidentController residentController) {
		this.residentController = residentController;
	}

	// UI Components
    @FXML private TextField txtId;
    @FXML private TextField txtFullName;
    @FXML private DatePicker datePickerDateOfBirth;
    @FXML private ComboBox<Gender> cmbGender;
    @FXML private ComboBox<Ethnicity> cmbEthnicity;
    @FXML private CheckBox chkReligion;
    @FXML private TextField txtCitizenId;
    @FXML private DatePicker datePickerDateOfIssue;
    @FXML private ComboBox<PlaceOfIssue> cmbPlaceOfIssue;
    @FXML private ComboBox<RelationshipType> cmbRelationship;
    @FXML private TextField txtOccupation;
    @FXML private TextField txtAddedDate;
    @FXML private TextField txtHouseholdId;
    @FXML private CheckBox chkIsHouseholdHead;
    
    // Control Components
    @FXML private ToggleButton toggleEditMode;
    @FXML private Button btnUpdate;
    @FXML private Button btnClose;
    @FXML private Label lblRequiredNote;
    
    
    public Resident getResident() {
		return resident;
	}

	/**
	 * Set resident data to display
	 */
	public void setResident(Resident resident) {
	    this.resident = resident;
	    populateFields();
	}

	
    
    /**
     * Initialize the controller
     */
    @FXML
    private void initialize() {
        setupComboBoxes();
        setupEditMode(false);
        
        // Disable editing for some fields that shouldn't be changed
        txtId.setEditable(false);
        txtAddedDate.setEditable(false);
        txtHouseholdId.setEditable(false);
        chkIsHouseholdHead.setDisable(true);
    }
    
    /**
     * Setup ComboBoxes with enum values
     */
    private void setupComboBoxes() {
        // Setup Gender ComboBox
        cmbGender.setItems(FXCollections.observableArrayList(Gender.values()));
        
        // Setup Ethnicity ComboBox
        cmbEthnicity.setItems(FXCollections.observableArrayList(Ethnicity.values()));
        
        // Setup Place of Issue ComboBox
        cmbPlaceOfIssue.setItems(FXCollections.observableArrayList(PlaceOfIssue.values()));
        
        // Setup Relationship ComboBox
        cmbRelationship.setItems(FXCollections.observableArrayList(RelationshipType.values()));
    }
    
    /**
     * Populate form fields with resident data
     */
    private void populateFields() {
        if (resident == null) return;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        txtId.setText(String.valueOf(resident.getId()));
        txtFullName.setText(resident.getFullName());
        
        if (resident.getDateOfBirth() != null) {
            datePickerDateOfBirth.setValue(resident.getDateOfBirth());
        }
        
        cmbGender.setValue(resident.getGender());
        cmbEthnicity.setValue(resident.getEthnicity());
        chkReligion.setSelected(resident.isReligion());
        txtCitizenId.setText(resident.getCitizenId());
        
        if (resident.getDateOfIssue() != null) {
            datePickerDateOfIssue.setValue(resident.getDateOfIssue());
        }
        
        cmbPlaceOfIssue.setValue(resident.getPlaceOfIssue());
        cmbRelationship.setValue(resident.getRelationship());
        txtOccupation.setText(resident.getOccupation());
        
        if (resident.getAddedDate() != null) {
            txtAddedDate.setText(resident.getAddedDate().format(formatter));
        }
        
        txtHouseholdId.setText(String.valueOf(resident.getHouseholdId()));
        chkIsHouseholdHead.setSelected(resident.isHouseholdHead());
    }
    
    /**
     * Convert place of issue string from database to enum
     */
    
    
    /**
     * Setup edit mode - enable/disable fields based on edit mode
     */
    private void setupEditMode(boolean isEditMode) {
        this.editMode = isEditMode;
        
        // Enable/disable editable fields
        txtFullName.setEditable(isEditMode);
        datePickerDateOfBirth.setDisable(!isEditMode);
        cmbGender.setDisable(!isEditMode);
        cmbEthnicity.setDisable(!isEditMode);
        chkReligion.setDisable(!isEditMode);
        txtCitizenId.setEditable(isEditMode);
        datePickerDateOfIssue.setDisable(!isEditMode);
        cmbPlaceOfIssue.setDisable(!isEditMode);
        cmbRelationship.setDisable(!isEditMode);
        txtOccupation.setEditable(isEditMode);
        
        // Update button state
        btnUpdate.setDisable(!isEditMode);
        btnUpdate.setStyle(isEditMode 
            ? "-fx-background-color: #43A5DC; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-opacity: 1.0;"
            : "-fx-background-color: #43A5DC; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-opacity: 0.5;");
        
        // Show/hide required note
        lblRequiredNote.setVisible(isEditMode);
        
        // Update field styles
        updateFieldStyles(isEditMode);
    }
    
    /**
     * Update field styles based on edit mode
     */
    private void updateFieldStyles(boolean isEditMode) {
        String editableStyle = "-fx-background-color: white; -fx-border-color: #43A5DC; -fx-border-radius: 5; -fx-background-radius: 5;";
        String readOnlyStyle = "-fx-background-color: #f5f5f5; -fx-border-color: #d0d0d0; -fx-border-radius: 5; -fx-background-radius: 5;";
        
        String style = isEditMode ? editableStyle : readOnlyStyle;
        
        txtFullName.setStyle(style);
        datePickerDateOfBirth.setStyle(style);
        txtCitizenId.setStyle(style);
        datePickerDateOfIssue.setStyle(style);
        txtOccupation.setStyle(style);
    }
    
    /**
     * Handle toggle edit mode
     */
    @FXML
    private void handleToggleEditMode() {
        boolean newEditMode = toggleEditMode.isSelected();
        setupEditMode(newEditMode);
        
        // Update toggle button text
        toggleEditMode.setText(newEditMode ? "Bật" : "Tắt");
    }
    
    /**
     * Validate all fields before updating
     */
    private boolean validateBeforeUpdate() {
        StringBuilder errors = new StringBuilder();
        
        // Validate required fields
        if (txtFullName.getText().trim().isEmpty()) {
            errors.append("- Họ và tên không được để trống\n");
        }
        
        if (datePickerDateOfBirth.getValue() == null) {
            errors.append("- Ngày sinh không được để trống\n");
        }
        
        if (cmbGender.getValue() == null) {
            errors.append("- Giới tính không được để trống\n");
        }

        if (cmbEthnicity.getValue() == null) {
            errors.append("- Dân tộc không được để trống\n");
        }
        
        // Validate CCCD
        String citizenId = txtCitizenId.getText().trim();
        if (citizenId.isEmpty()) {
            errors.append("- Số CCCD không được để trống\n");
        } else {
            // Validate CCCD format (12 digits)
            if (!citizenId.matches("\\d{12}")) {
                errors.append("- Số CCCD phải là 12 chữ số\n");
            } else {
                // Check if citizen ID exists (only if it's different from current resident's ID)
                if (!citizenId.equals(resident.getCitizenId())) {
                    try {
                        if (residentController.getResidentByCitizenId(citizenId) != null) {
                            errors.append("- CCCD này đã tồn tại trong hệ thống\n");
                        }
                    } catch (Exception e) {
                        // Citizen ID doesn't exist, which is good in this case
                    }
                }
            }
        }

        // Validate date of issue
        if (datePickerDateOfIssue.getValue() == null) {
            errors.append("- Ngày cấp CCCD không được để trống\n");
        }

        if (cmbPlaceOfIssue.getValue() == null) {
            errors.append("- Nơi cấp CCCD không được để trống\n");
        }

        if (cmbRelationship.getValue() == null) {
            errors.append("- Quan hệ với chủ hộ không được để trống\n");
        }
        
        // Validate date logic
        if (datePickerDateOfBirth.getValue() != null) {
            // Check if date of birth is in the future
            if (datePickerDateOfBirth.getValue().isAfter(LocalDate.now())) {
                errors.append("- Ngày sinh không thể là ngày trong tương lai\n");
            }
            
            // Check if date of issue is before date of birth
            if (datePickerDateOfIssue.getValue() != null && 
                datePickerDateOfIssue.getValue().isBefore(datePickerDateOfBirth.getValue())) {
                errors.append("- Ngày cấp CCCD không thể trước ngày sinh\n");
            }
        }
        
        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", 
                     "Vui lòng kiểm tra lại thông tin:\n" + errors.toString());
            return false;
        }
        
        return true;
    }

    @FXML
    private void handleUpdate() {
        if (!validateBeforeUpdate()) {
            return;
        }

        try {
            // Update resident object with form values
            updateResidentFromForm();
            
            // Update in database
            residentController.updateResident(resident);
            
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Thành công", 
                     "Đã cập nhật thông tin thành công!");
            
            // Disable edit mode
            toggleEditMode.setSelected(false);
            setupEditMode(false);
            
            // Run callback if provided
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", 
                     "Không thể cập nhật thông tin: " + e.getMessage());
        }
    }
    
    /**
     * Update resident object with form data
     */
    private void updateResidentFromForm() {
        resident.setFullName(txtFullName.getText().trim());
        resident.setDateOfBirth(datePickerDateOfBirth.getValue());
        resident.setGender(cmbGender.getValue());
        resident.setEthnicity(cmbEthnicity.getValue().name());
        resident.setReligion(chkReligion.isSelected());
        resident.setCitizenId(txtCitizenId.getText().trim());
        resident.setDateOfIssue(datePickerDateOfIssue.getValue());
        
        // Convert PlaceOfIssue enum back to string for database
        if (cmbPlaceOfIssue.getValue() != null) {
            resident.setPlaceOfIssue(cmbPlaceOfIssue.getValue().name());
        }
        
        resident.setRelationship(cmbRelationship.getValue());
        resident.setOccupation(txtOccupation.getText().trim());
    }
    
    /**
     * Handle close button click
     */
    @FXML
    private void handleClose() {
        // Check if there are unsaved changes
        if (editMode && hasUnsavedChanges()) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xác nhận");
            confirmAlert.setHeaderText("Có thay đổi chưa được lưu");
            confirmAlert.setContentText("Bạn có muốn thoát mà không lưu các thay đổi?");
            
            if (confirmAlert.showAndWait().orElse(null) != javafx.scene.control.ButtonType.OK) {
                return;
            }
        }
        
        // Close the dialog
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Check if there are unsaved changes
     */
    private boolean hasUnsavedChanges() {
        if (resident == null) return false;
        
        // Compare current form values with original resident data
        return !txtFullName.getText().trim().equals(resident.getFullName() != null ? resident.getFullName() : "") ||
               !datePickerDateOfBirth.getValue().equals(resident.getDateOfBirth()) ||
               !cmbGender.getValue().equals(resident.getGender()) ||
               !cmbEthnicity.getValue().equals(resident.getEthnicity()) ||
               chkReligion.isSelected() != resident.isReligion() ||
               !txtCitizenId.getText().trim().equals(resident.getCitizenId() != null ? resident.getCitizenId() : "") ||
               !datePickerDateOfIssue.getValue().equals(resident.getDateOfIssue()) ||
               !cmbRelationship.getValue().equals(resident.getRelationship()) ||
               !txtOccupation.getText().trim().equals(resident.getOccupation() != null ? resident.getOccupation() : "") ||
               chkIsHouseholdHead.isSelected() != resident.isHouseholdHead();
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}