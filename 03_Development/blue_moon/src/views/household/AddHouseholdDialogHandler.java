package views.household;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import controllers.household.HouseholdController;

import java.lang.Object;
import java.lang.String;

import exception.HouseholdAlreadyExistsException;
import exception.HouseholdNotExist;
import exception.InvalidHouseholdDataException;
import exception.ResidentNotFoundException;
import exception.ServiceException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Household;
import models.Resident;
import services.resident.ResidentService;
import services.room.RoomService;
import services.room.RoomServiceImpl;
import utils.FieldVerifier;
import utils.RelationshipHelper;
import utils.SceneUtils.HouseholdDialogHandler;
import utils.enums.RelationshipType;
import utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

public class AddHouseholdDialogHandler implements HouseholdDialogHandler {
    private Runnable onSuccessCallback;
    
    private HouseholdController householdController;
    private ResidentService residentService;
    private RoomServiceImpl roomService;
    
    // Lists to store dynamic resident fields and their error labels
    private List<TextField> residentCitizenIdFields = new ArrayList<>();
    private List<Label> residentCitizenIdErrorLabels = new ArrayList<>();
    private List<ComboBox<RelationshipType>> relationshipFields = new ArrayList<>();
    
    
    // Main form fields
    @FXML private TextField txtOwnerCitizenId;
    @FXML private ComboBox<String> cmbRoom;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtResidentCount;
    
    // Error labels
    @FXML private Label lblOwnerCitizenIdError;
    @FXML private Label lblRoomError;
    @FXML private Label lblPhoneError;
    @FXML private Label lblEmailError;
    @FXML private Label lblResidentCountError;
    
    // Dynamic fields container
    @FXML private VBox residentFieldsContainer;
    @FXML private VBox contentVBox;
    
    // Buttons
    @FXML private Button btnAdd;
    @FXML private Button btnCancel;
    @FXML private Button btnGenerateFields;
    
    
    
    // Constructor không tham số cho FXML
    public AddHouseholdDialogHandler() {
        this.householdController = new HouseholdController();
        // Initialize services - you might want to inject these
        // this.residentService = new ResidentServiceImpl();
        // this.roomService = new RoomServiceImpl();
    }
    
    //getter + setter
    public HouseholdController getHouseholdController() {
        return householdController;
    }

    public void setHouseholdController(HouseholdController householdController) {
        this.householdController = householdController;
    }
    
    public ResidentService getResidentService() {
        return residentService;
    }

    public void setResidentService(ResidentService residentService) {
        this.residentService = residentService;
    }

    public RoomService getRoomService() {
        return roomService;
    }

    public void setRoomService(RoomServiceImpl roomService) {
        this.roomService = roomService;
    }
    
    public AddHouseholdDialogHandler(HouseholdController householdController) {
        super();
        this.householdController = householdController;
    }

    @Override
    public void setOnSuccessCallback(Runnable callback) {
        this.onSuccessCallback = callback;
    }
    
    @FXML
    private void initialize() {
        // Add real-time validation listeners
        addValidationListeners();
        // Initialize room combo box
        initializeRoomComboBox();
    }
    
    private void initializeRoomComboBox() {
        ObservableList<String> rooms = FXCollections.observableArrayList();
        // Add available rooms
        rooms.addAll("301", "302", "303", "401", "402", "403", "501", "502", "503");
        cmbRoom.setItems(rooms);
        cmbRoom.setPromptText("Chọn phòng");
    }
    
    private void addValidationListeners() {
        // Owner Citizen ID validation
        txtOwnerCitizenId.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                hideError(lblOwnerCitizenIdError);
            }
        });
        
        // Room validation
        cmbRoom.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                hideError(lblRoomError);
                // Validate room availability
                validateRoomAvailability(newVal);
            }
        });
        
        // Phone validation
        txtPhone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                hideError(lblPhoneError);
            }
        });
        
        // Email validation
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                hideError(lblEmailError);
            }
        });
        
        // Resident count validation
        txtResidentCount.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                hideError(lblResidentCountError);
            }
        });
    }
    
    private void validateRoomAvailability(String roomNumber) {
        try {
            if (roomService != null && !roomService.isRoomAvailable(roomNumber)) {
                showError(lblRoomError, "Phòng " + roomNumber + " đã có người thuê");
            }
        } catch (Exception e) {
            showError(lblRoomError, "Lỗi kiểm tra phòng: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleGenerateResidentFields() {
        String residentCountText = txtResidentCount.getText().trim();
        
        if (!FieldVerifier.isValidResidentCount(residentCountText)) {
            showError(lblResidentCountError, FieldVerifier.getInvalidResidentCountErrorMessage());
            return;
        }
        
        int residentCount = Integer.parseInt(residentCountText);
        generateResidentCitizenIdFields(residentCount);
    }
    
    private void generateResidentCitizenIdFields(int residentCount) {
        // Clear existing fields
        residentFieldsContainer.getChildren().clear();
        residentCitizenIdFields.clear();
        residentCitizenIdErrorLabels.clear();
        relationshipFields.clear();
        
        // Show the container
        residentFieldsContainer.setVisible(true);
        
        // Add a header for resident IDs section
        Label headerLabel = new Label("CCCD các thành viên trong hộ:");
        headerLabel.setTextFill(javafx.scene.paint.Color.web("#43a5dc"));
        headerLabel.setFont(Font.font("System Bold", 14.0));
        VBox.setMargin(headerLabel, new Insets(10, 0, 5, 0));
        residentFieldsContainer.getChildren().add(headerLabel);
        
        // Generate resident citizen ID input fields
        for (int i = 0; i < residentCount; i++) {
            VBox residentFieldBox = new VBox(5.0);
            
            // Resident label
            Label residentLabel = new Label("Thành viên " + (i + 1) + ": *");
            residentLabel.setTextFill(javafx.scene.paint.Color.web("#43a5dc"));
            residentLabel.setFont(Font.font("System Bold", 12.0));
            
            // Create HBox for citizen ID field and relationship dropdown
            HBox inputContainer = new HBox(10.0);
            
            // Resident Citizen ID text field
            TextField residentCitizenIdField = new TextField();
            residentCitizenIdField.setPrefHeight(35.0);
            residentCitizenIdField.setPrefWidth(250.0);
            residentCitizenIdField.setPromptText("Nhập CCCD thành viên " + (i + 1));
            residentCitizenIdField.setStyle("-fx-border-color: #d0d0d0; -fx-border-radius: 5; -fx-background-radius: 5;");
            residentCitizenIdField.setFont(Font.font(14.0));
            
            // Relationship dropdown
            ComboBox<RelationshipType> relationshipCombo = new ComboBox<>();
            relationshipCombo.setPrefHeight(35.0);
            relationshipCombo.setPrefWidth(150.0);
            relationshipCombo.setPromptText("Chọn quan hệ");
            relationshipCombo.setStyle("-fx-background-radius: 5; -fx-border-color: #ced4da; -fx-border-radius: 5;");
            
            // Set up relationship combo box items
            ObservableList<RelationshipType> relationships = FXCollections.observableArrayList();
            for (RelationshipType type : RelationshipType.values()) {
                if (type != RelationshipType.UNKNOWN) {
                    relationships.add(type);
                }
            }
            relationshipCombo.setItems(relationships);
            
            // Set up string converter for relationship combo box
            relationshipCombo.setConverter(new StringConverter<RelationshipType>() {
                @Override
                public String toString(RelationshipType type) {
                    String displayText = RelationshipHelper.getDisplayText(type);
                    System.out.println("Converting enum to display: " + type + " -> " + displayText);
                    return displayText;
                }

                @Override
                public RelationshipType fromString(String string) {
                    System.out.println("Converting string to enum: " + string);
                    if (string == null || string.isEmpty()) {
                        System.out.println("Empty string, returning null");
                        return null;
                    }
                    RelationshipType type = RelationshipHelper.fromDisplayText(string);
                    System.out.println("Converted to enum: " + type);
                    return type;
                }
            });
            
            // Add fields to HBox
            inputContainer.getChildren().addAll(residentCitizenIdField, relationshipCombo);
            
            // Error label for this resident field
            Label errorLabel = new Label();
            errorLabel.setTextFill(javafx.scene.paint.Color.web("#dc3545"));
            errorLabel.setFont(Font.font(12.0));
            errorLabel.setVisible(false);
            
            // Add real-time validation for resident citizen ID
            final int residentIndex = i;
            residentCitizenIdField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    hideError(errorLabel);
                }
            });
            
            residentFieldBox.getChildren().addAll(residentLabel, inputContainer, errorLabel);
            residentFieldsContainer.getChildren().add(residentFieldBox);
            
            // Store references
            residentCitizenIdFields.add(residentCitizenIdField);
            residentCitizenIdErrorLabels.add(errorLabel);
            relationshipFields.add(relationshipCombo);
        }
    }
    
    private boolean validateRequiredFields() {
        boolean isValid = true;
        
        // Validate owner citizen ID
        if (!FieldVerifier.isValidCitizenId(txtOwnerCitizenId.getText())) {
            showError(lblOwnerCitizenIdError, txtOwnerCitizenId.getText().trim().isEmpty() ? 
                FieldVerifier.getEmptyFieldErrorMessage() : FieldVerifier.getInvalidCitizenIdErrorMessage());
            isValid = false;
        }
        
        // Validate room selection
        if (cmbRoom.getValue() == null || cmbRoom.getValue().isEmpty()) {
            showError(lblRoomError, "Vui lòng chọn phòng");
            isValid = false;
        } else {
            // Check room availability
            try {
                if (roomService != null && !roomService.isRoomAvailable(cmbRoom.getValue())) {
                    showError(lblRoomError, "Phòng " + cmbRoom.getValue() + " đã có người thuê");
                    isValid = false;
                }
            } catch (Exception e) {
                showError(lblRoomError, "Lỗi kiểm tra phòng: " + e.getMessage());
                isValid = false;
            }
        }
        
        // Validate phone (optional)
        String phone = txtPhone.getText().trim();
        if (!phone.isEmpty() && !FieldVerifier.isValidPhoneNumber(phone)) {
            showError(lblPhoneError, FieldVerifier.getPhoneErrorMessage());
            isValid = false;
        }
        
        // Validate email (optional)
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !FieldVerifier.isValidEmail(email)) {
            showError(lblEmailError, FieldVerifier.getEmailErrorMessage());
            isValid = false;
        }
        
        // Validate resident count
        if (!FieldVerifier.isValidResidentCount(txtResidentCount.getText())) {
            showError(lblResidentCountError, txtResidentCount.getText().trim().isEmpty() ? 
                FieldVerifier.getEmptyFieldErrorMessage() : FieldVerifier.getInvalidResidentCountErrorMessage());
            isValid = false;
        }
        
        // Validate resident fields if they are visible
        if (residentFieldsContainer.isVisible()) {
            String ownerCitizenId = txtOwnerCitizenId.getText().trim();
            boolean ownerFound = false;
            
            for (int i = 0; i < residentCitizenIdFields.size(); i++) {
                TextField residentField = residentCitizenIdFields.get(i);
                Label errorLabel = residentCitizenIdErrorLabels.get(i);
                ComboBox<RelationshipType> relationshipCombo = relationshipFields.get(i);
                
                if (!FieldVerifier.isValidCitizenId(residentField.getText())) {
                    showError(errorLabel, residentField.getText().trim().isEmpty() ? 
                        FieldVerifier.getEmptyFieldErrorMessage() : FieldVerifier.getInvalidCitizenIdErrorMessage());
                    isValid = false;
                }
                
                if (relationshipCombo.getValue() == null) {
                    showError(errorLabel, "Vui lòng chọn quan hệ với chủ hộ");
                    isValid = false;
                }
                
                // Check if owner is in the resident list
                if (residentField.getText().trim().equals(ownerCitizenId)) {
                    ownerFound = true;
                }
            }
            
            // Validate that owner is included in resident list
            if (!ownerFound && isValid) {
                showError(lblOwnerCitizenIdError, "Chủ hộ phải được bao gồm trong danh sách thành viên");
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void hideError(Label errorLabel) {
        errorLabel.setVisible(false);
    }
    
    private Household createHouseholdFromInput() throws ServiceException, SQLException {
        Household household = new Household();
        
        // Auto-generated fields
        household.setCreationDate(LocalDate.now());
        
        // Get owner by citizen ID and set owner info
        String ownerCitizenId = txtOwnerCitizenId.getText().trim();
        Resident owner = residentService.getResidentByCitizenId(ownerCitizenId);
        household.setOwnerId(owner.getId());
        household.setOwnerName(owner.getFullName());
        
        // Set address info - auto-filled as requested
        household.setHouseNumber(cmbRoom.getValue()); // Room number as house number
        household.setStreet("Wall Street");
        household.setWard("Wall ward");
        household.setDistrict("Q2");
        household.setArea(0.0f); // You might want to set this based on room
        
        // User input fields
        household.setPhone(txtPhone.getText().trim().replaceAll("\\s+", "")); // Remove spaces
        household.setEmail(txtEmail.getText().trim());
        household.setHouseholdSize(Integer.parseInt(txtResidentCount.getText().trim()));
        
        // Collect resident citizen IDs and their relationships
        List<String> residentCitizenIds = new ArrayList<>();
        List<RelationshipType> relationships = new ArrayList<>();
        
        // First, collect all residents from the dynamic fields
        for (int i = 0; i < residentCitizenIdFields.size(); i++) {
            String residentCitizenId = residentCitizenIdFields.get(i).getText().trim();
            RelationshipType relationship = relationshipFields.get(i).getValue();
            
            if (!residentCitizenId.isEmpty() && relationship != null) {
                residentCitizenIds.add(residentCitizenId);
                relationships.add(relationship);
            }
        }
        
        // Check if owner citizen ID is in the resident list, if not add it with default relationship
        boolean ownerFound = false;
        for (int i = 0; i < residentCitizenIds.size(); i++) {
            if (residentCitizenIds.get(i).equals(ownerCitizenId)) {
                ownerFound = true;
                break;
            }
        }
        
        // If owner is not in the resident list, add them
        if (!ownerFound) {
            residentCitizenIds.add(0, ownerCitizenId); // Add at beginning
            relationships.add(0, RelationshipType.FATHER); // Default relationship for household head
        }
        
        // Get residents by citizen IDs and set their relationships
        List<Resident> residents = residentService.getResidentsByCitizenIds(residentCitizenIds);
        for (int i = 0; i < residents.size() && i < relationships.size(); i++) {
            Resident resident = residents.get(i);
            RelationshipType relationship;
            
            // If this is the owner, set relationship to FATHER or appropriate value
            if (resident.getCitizenId().equals(ownerCitizenId)) {
                relationship = RelationshipType.FATHER; // Or determine based on gender/age
            } else {
                relationship = relationships.get(i);
            }
            
            System.out.println("Setting relationship for resident " + resident.getId() + 
                " (isOwner=" + resident.getCitizenId().equals(ownerCitizenId) + "): " + relationship);
            
            resident.setRelationship(relationship);
            resident.setHouseholdHead(resident.getCitizenId().equals(ownerCitizenId));
            
            // Update resident in database immediately
            try {
                residentService.updateResident(resident);
                System.out.println("Successfully updated resident " + resident.getId() + 
                    " with relationship " + resident.getRelationship());
            } catch (Exception e) {
                System.err.println("Error updating resident " + resident.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        household.setResidents(residents);
        
        return household;
    }
    

    @FXML
    private void handleAdd() {
        System.out.println("Add button clicked");
        
        // Validate all fields
        if (!validateRequiredFields()) {
            System.out.println("Validation failed");
            return;
        }
        
        // Check if resident fields are generated
        if (!residentFieldsContainer.isVisible()) {
            showError(lblResidentCountError, "Vui lòng tạo trường nhập cho các thành viên");
            return;
        }
        
        try {
            // Create household object from input
            Household household = null;
            try {
                household = createHouseholdFromInput();
            } catch (ServiceException e1) {
                e1.printStackTrace();
                utils.AlertUtils.showErrorAlert("Lỗi", "Không thể tạo hộ khẩu", e1.getMessage());
                return;
            } catch (SQLException e1) {
                e1.printStackTrace();
                utils.AlertUtils.showErrorAlert("Lỗi", "Lỗi cơ sở dữ liệu", e1.getMessage());
                return;
            }
            
            // Add household through controller - this will handle resident updates
            try {
                householdController.addHousehold(household);
                
                // Mark room as occupied
                if (roomService != null) {
                    roomService.occupyRoom(cmbRoom.getValue(), household.getId());
                }
                
            } catch (HouseholdNotExist e) {
                e.printStackTrace();
                utils.AlertUtils.showErrorAlert("Lỗi", "Hộ khẩu không tồn tại", e.getMessage());
                return;
            } catch (SQLException e) {
                e.printStackTrace();
                utils.AlertUtils.showErrorAlert("Lỗi", "Lỗi cơ sở dữ liệu", e.getMessage());
                return;
            } catch (ServiceException e) {
                e.printStackTrace();
                utils.AlertUtils.showErrorAlert("Lỗi", "Lỗi dịch vụ", e.getMessage());
                return;
            }
            
            System.out.println("Household added successfully");
            
            // Show success message
            utils.AlertUtils.showInfoAlert("Thành công", "Thêm hộ khẩu thành công", 
                "Hộ khẩu đã được thêm vào hệ thống và phòng " + cmbRoom.getValue() + " đã được đánh dấu là có người thuê.");
            
            // Call success callback and close dialog
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            
            ((Stage) btnAdd.getScene().getWindow()).close();
            
        } catch (HouseholdAlreadyExistsException | ResidentNotFoundException | InvalidHouseholdDataException e) {
            utils.AlertUtils.showErrorAlert("Lỗi", "Không thể thêm hộ khẩu", e.getMessage());
        } 
    }

    @FXML
    private void handleCancel() {
        System.out.println("Cancel add");
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    @Override
    public void setHousehold(Household household) {
        // This method is for edit mode,
    }
    
}