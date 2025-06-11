package views.resident;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import controllers.resident.ResidentController;
import exception.ServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Resident;
import services.resident.ResidentService;
import services.resident.ResidentServiceImpl;
import utils.Configs;
import utils.enums.Gender;
import utils.enums.RelationshipType;
import utils.AlertUtils;

public class AddResidentDialogHandler {
    
    private ResidentController residentController;
    private ResidentService residentService;
    private Resident currentResident;
    private Runnable onSuccessCallback;
    private boolean isEditMode = false;
    
    // Text fields
    @FXML private TextField tfId;
    @FXML private TextField tfFullName;
    @FXML private DatePicker dpDateOfBirth;
    @FXML private ComboBox<String> cbGender;
    @FXML private ComboBox<String> cbEthnicity;
    @FXML private TextField tfReligion;
    @FXML private TextField tfCitizenId;
    @FXML private DatePicker dpDateOfIssue;
    @FXML private ComboBox<String> cbPlaceOfIssue;
    @FXML private TextField tfOccupation;
    @FXML private ComboBox<RelationshipType> cbRelationship;
    @FXML private TextField tfHouseholdId;
    
    // Controls
    @FXML private ToggleButton toggleEditMode;
    @FXML private Button btnSave;
    @FXML private Button btnClose;
    @FXML private Label lblRequiredNote;
    @FXML private Label lblError;
    
    private static final String VIEW_MODE_STYLE = "-fx-background-color: #f5f5f5; -fx-border-color: #d0d0d0; -fx-border-radius: 5; -fx-background-radius: 5;";
    
    public AddResidentDialogHandler(ResidentController residentController) {
        this.residentController = residentController;
        this.residentService = new ResidentServiceImpl();
    }
    
    @FXML
    private void initialize() {
        // Initialize services if not already initialized
        if (residentService == null) {
            residentService = new ResidentServiceImpl();
        }
        
        // Add real-time validation listeners
        addValidationListeners();
        
        // Initialize combo boxes
        initializeComboBoxes();
        
        // Setup toggle button
        setupToggleButton();
        
        // Setup buttons
        setupButtons();
    }
    
    private void addValidationListeners() {
        // Add validation listeners to required fields
        tfFullName.textProperty().addListener((obs, oldVal, newVal) -> validateInput());
        dpDateOfBirth.valueProperty().addListener((obs, oldVal, newVal) -> validateInput());
        cbGender.valueProperty().addListener((obs, oldVal, newVal) -> validateInput());
        cbEthnicity.valueProperty().addListener((obs, oldVal, newVal) -> validateInput());
        tfCitizenId.textProperty().addListener((obs, oldVal, newVal) -> validateInput());
        dpDateOfIssue.valueProperty().addListener((obs, oldVal, newVal) -> validateInput());
        cbPlaceOfIssue.valueProperty().addListener((obs, oldVal, newVal) -> validateInput());
        cbRelationship.valueProperty().addListener((obs, oldVal, newVal) -> validateInput());
        tfHouseholdId.textProperty().addListener((obs, oldVal, newVal) -> validateInput());
    }
    
    private void initializeComboBoxes() {
        // Initialize gender combo box
        cbGender.getItems().addAll(Configs.GENDER);
        
        // Initialize ethnicity combo box
        cbEthnicity.getItems().addAll(Configs.ETHNICITY);
        
        // Initialize place of issue combo box
        cbPlaceOfIssue.getItems().addAll(Configs.PLACEOFISSUE);
        
        // Initialize relationship combo box
        cbRelationship.getItems().addAll(RelationshipType.values());
    }
    
    private void setupToggleButton() {
        toggleEditMode.setSelected(false);
        toggleEditMode.selectedProperty().addListener((obs, oldVal, newVal) -> {
            isEditMode = newVal;
            if (isEditMode) {
                setEditMode();
            } else {
                setViewMode();
            }
        });
    }
    
    private void setupButtons() {
        btnSave.setOnAction(e -> handleSave());
        btnClose.setOnAction(e -> handleClose());
    }
    
    private void setEditMode() {
        // Enable all editable fields
        tfFullName.setEditable(true);
        dpDateOfBirth.setDisable(false);
        cbGender.setDisable(false);
        cbEthnicity.setDisable(false);
        tfReligion.setEditable(true);
        tfCitizenId.setEditable(true);
        dpDateOfIssue.setDisable(false);
        cbPlaceOfIssue.setDisable(false);
        tfOccupation.setEditable(true);
        cbRelationship.setDisable(false);
        tfHouseholdId.setEditable(true);
        
        // Show required note and enable save button
        lblRequiredNote.setVisible(true);
        btnSave.setVisible(true);
        
        // Remove view mode style
        removeViewModeStyle();
    }
    
    private void setViewMode() {
        // Disable all fields
        tfFullName.setEditable(false);
        dpDateOfBirth.setDisable(true);
        cbGender.setDisable(true);
        cbEthnicity.setDisable(true);
        tfReligion.setEditable(false);
        tfCitizenId.setEditable(false);
        dpDateOfIssue.setDisable(true);
        cbPlaceOfIssue.setDisable(true);
        tfOccupation.setEditable(false);
        cbRelationship.setDisable(true);
        tfHouseholdId.setEditable(false);
        
        // Hide required note and save button
        lblRequiredNote.setVisible(false);
        btnSave.setVisible(false);
        
        // Apply view mode style
        applyViewModeStyle();
    }
    
    private void applyViewModeStyle() {
        tfFullName.setStyle(VIEW_MODE_STYLE);
        dpDateOfBirth.setStyle(VIEW_MODE_STYLE);
        cbGender.setStyle(VIEW_MODE_STYLE);
        cbEthnicity.setStyle(VIEW_MODE_STYLE);
        tfReligion.setStyle(VIEW_MODE_STYLE);
        tfCitizenId.setStyle(VIEW_MODE_STYLE);
        dpDateOfIssue.setStyle(VIEW_MODE_STYLE);
        cbPlaceOfIssue.setStyle(VIEW_MODE_STYLE);
        tfOccupation.setStyle(VIEW_MODE_STYLE);
        cbRelationship.setStyle(VIEW_MODE_STYLE);
        tfHouseholdId.setStyle(VIEW_MODE_STYLE);
    }
    
    private void removeViewModeStyle() {
        tfFullName.setStyle(null);
        dpDateOfBirth.setStyle(null);
        cbGender.setStyle(null);
        cbEthnicity.setStyle(null);
        tfReligion.setStyle(null);
        tfCitizenId.setStyle(null);
        dpDateOfIssue.setStyle(null);
        cbPlaceOfIssue.setStyle(null);
        tfOccupation.setStyle(null);
        cbRelationship.setStyle(null);
        tfHouseholdId.setStyle(null);
    }
    
    private void validateInput() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();
        
        // Validate required fields
        if (tfFullName.getText().trim().isEmpty()) {
            isValid = false;
            errorMessage.append("Họ và tên không được để trống\n");
        }
        
        if (dpDateOfBirth.getValue() == null) {
            isValid = false;
            errorMessage.append("Ngày sinh không được để trống\n");
        }
        
        if (cbGender.getValue() == null) {
            isValid = false;
            errorMessage.append("Giới tính không được để trống\n");
        }
        
        if (cbEthnicity.getValue() == null) {
            isValid = false;
            errorMessage.append("Dân tộc không được để trống\n");
        }
        
        if (tfCitizenId.getText().trim().isEmpty()) {
            isValid = false;
            errorMessage.append("CCCD/CMND không được để trống\n");
        }
        
        if (dpDateOfIssue.getValue() == null) {
            isValid = false;
            errorMessage.append("Ngày cấp không được để trống\n");
        }
        
        if (cbPlaceOfIssue.getValue() == null) {
            isValid = false;
            errorMessage.append("Nơi cấp không được để trống\n");
        }
        
        if (cbRelationship.getValue() == null) {
            isValid = false;
            errorMessage.append("Quan hệ với chủ hộ không được để trống\n");
        }
        
        if (tfHouseholdId.getText().trim().isEmpty()) {
            isValid = false;
            errorMessage.append("Mã hộ khẩu không được để trống\n");
        }
        
        // Update UI
        btnSave.setDisable(!isValid);
        lblError.setText(errorMessage.toString());
    }
    
    private void handleSave() {
        try {
            // Create or update resident
            Resident resident = createResidentFromInput();
            
            if (currentResident == null) {
                // Add new resident
                residentController.addResident(resident);
                AlertUtils.showSuccessDialog("Thành công", "Thêm nhân khẩu thành công!");
            } else {
                // Update existing resident
                residentController.updateResident(resident);
                AlertUtils.showSuccessDialog("Thành công", "Cập nhật thông tin nhân khẩu thành công!");
            }
            
            // Call success callback
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            
            // Close dialog
            handleClose();
            
        } catch (Exception e) {
            AlertUtils.showErrorAlert("Lỗi", "Không thể lưu thông tin nhân khẩu", e.getMessage());
        }
    }
    
    private Resident createResidentFromInput() {
        Resident resident = new Resident();
        
        // Set ID if updating
        if (currentResident != null) {
            resident.setId(currentResident.getId());
        }
        
        // Set basic info
        resident.setFullName(tfFullName.getText().trim());
        resident.setDateOfBirth(dpDateOfBirth.getValue());
        resident.setGender(Gender.valueOf(cbGender.getValue().toUpperCase()));
        resident.setEthnicity(cbEthnicity.getValue());
        resident.setReligion(tfReligion.getText().trim());
        resident.setCitizenId(tfCitizenId.getText().trim());
        resident.setDateOfIssue(dpDateOfIssue.getValue());
        resident.setPlaceOfIssue(cbPlaceOfIssue.getValue());
        resident.setOccupation(tfOccupation.getText().trim());
        resident.setRelationship(cbRelationship.getValue());
        resident.setHouseholdId(Integer.parseInt(tfHouseholdId.getText().trim()));
        
        // Set added date
        if (currentResident == null) {
            resident.setAddedDate(LocalDate.now());
        } else {
            resident.setAddedDate(currentResident.getAddedDate());
        }
        
        return resident;
    }
    
    private void handleClose() {
        ((Stage) btnClose.getScene().getWindow()).close();
    }
    
    public void setResident(Resident resident) {
        this.currentResident = resident;
        
        if (resident != null) {
            // Populate fields
            tfId.setText(String.valueOf(resident.getId()));
            tfFullName.setText(resident.getFullName());
            dpDateOfBirth.setValue(resident.getDateOfBirth());
            cbGender.setValue(resident.getGender().toString());
            cbEthnicity.setValue(resident.getEthnicity());
            tfReligion.setText(resident.getReligion());
            tfCitizenId.setText(resident.getCitizenId());
            dpDateOfIssue.setValue(resident.getDateOfIssue());
            cbPlaceOfIssue.setValue(resident.getPlaceOfIssue());
            tfOccupation.setText(resident.getOccupation());
            cbRelationship.setValue(resident.getRelationship());
            tfHouseholdId.setText(String.valueOf(resident.getHouseholdId()));
            
            // Set view mode by default
            toggleEditMode.setSelected(false);
            setViewMode();
        } else {
            // New resident, set edit mode
            toggleEditMode.setSelected(true);
            setEditMode();
        }
    }
    
    public void setOnSuccessCallback(Runnable callback) {
        this.onSuccessCallback = callback;
    }
    
    public ResidentController getResidentController() {
        return residentController;
    }
    
    public void setResidentController(ResidentController residentController) {
        this.residentController = residentController;
    }
    
    public ResidentService getResidentService() {
        return residentService;
    }
    
    public void setResidentService(ResidentService residentService) {
        this.residentService = residentService;
    }
} 