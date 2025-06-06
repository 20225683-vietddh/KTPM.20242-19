package views.household;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import models.Room;
import services.resident.ResidentService;
import services.resident.ResidentServiceImpl;
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
import utils.FieldVerifier.ValidationResult;
public class AddHouseholdDialogHandler implements HouseholdDialogHandler {
    private Runnable onSuccessCallback;
    
    private HouseholdController householdController;
    private ResidentService residentService;
    private RoomService roomService;
    
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

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }
    
    public AddHouseholdDialogHandler(HouseholdController householdController) {
        super();
        this.householdController = householdController;
        this.roomService = new RoomServiceImpl();
        this.residentService = new ResidentServiceImpl();
    }

    @Override
    public void setOnSuccessCallback(Runnable callback) {
        this.onSuccessCallback = callback;
    }
    
    @FXML
    private void initialize() {
        // Initialize services if not already initialized
        if (roomService == null) {
            roomService = new RoomServiceImpl();
        }
        if (residentService == null) {
            residentService = new ResidentServiceImpl();
        }
        
        // Add real-time validation listeners
        addValidationListeners();
        
        // Initialize room combo box
        initializeRoomComboBox();
    }
    
    private void initializeRoomComboBox() {
        try {
            ObservableList<String> rooms = FXCollections.observableArrayList();
            // Get available rooms from service
            List<Room> availableRooms = roomService.getAvailableRooms();
            rooms.addAll(availableRooms.stream()
                .map(Room::getRoomNumber)
                .collect(Collectors.toList()));
            cmbRoom.setItems(rooms);
            cmbRoom.setPromptText("Chọn phòng");
        } catch (Exception e) {
            System.err.println("Error initializing room combo box: " + e.getMessage());
            AlertUtils.showErrorAlert("Lỗi", "Không thể tải danh sách phòng", e.getMessage());
        }
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
            if (roomService == null) {
                System.err.println("RoomService is null");
                return;
            }
            
            // First check if room exists
            Room room = roomService.getRoomByNumber(roomNumber);
            if (room == null) {
                showError(lblRoomError, "Phòng " + roomNumber + " không tồn tại");
                return;
            }
            
            // Then check if it's available
            if (room.isOccupied()) {
                showError(lblRoomError, "Phòng " + roomNumber + " đã có người thuê");
            }
        } catch (ServiceException e) {
            showError(lblRoomError, "Lỗi kiểm tra phòng: " + e.getMessage());
        } catch (SQLException e) {
            showError(lblRoomError, "Lỗi database: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleGenerateResidentFields() {
        String residentCountText = txtResidentCount.getText().trim();
        
        ValidationResult countResult = FieldVerifier.verifyResidentCount(residentCountText);
        if (!countResult.isValid()) {
            showError(lblResidentCountError, countResult.getMessage());
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
        Label headerLabel = new Label("CCCD cac thanh vien trong ho:");
        headerLabel.setTextFill(javafx.scene.paint.Color.web("#43a5dc"));
        headerLabel.setFont(Font.font("System Bold", 14.0));
        VBox.setMargin(headerLabel, new Insets(10, 0, 5, 0));
        residentFieldsContainer.getChildren().add(headerLabel);
        
        // Generate resident citizen ID input fields
        for (int i = 0; i < residentCount; i++) {
            VBox residentFieldBox = new VBox(5.0);
            
            // Resident label
            Label residentLabel = new Label("Thanh vien " + (i + 1) + ": *");
            residentLabel.setTextFill(javafx.scene.paint.Color.web("#43a5dc"));
            residentLabel.setFont(Font.font("System Bold", 12.0));
            
            // Create HBox for citizen ID field and relationship dropdown
            HBox inputContainer = new HBox(10.0);
            
            // Resident Citizen ID text field
            TextField residentCitizenIdField = new TextField();
            residentCitizenIdField.setPrefHeight(35.0);
            residentCitizenIdField.setPrefWidth(250.0);
            residentCitizenIdField.setPromptText("Nhap CCCD thanh vien " + (i + 1));
            residentCitizenIdField.setStyle("-fx-border-color: #d0d0d0; -fx-border-radius: 5; -fx-background-radius: 5;");
            residentCitizenIdField.setFont(Font.font(14.0));
            
            // Relationship dropdown
            ComboBox<RelationshipType> relationshipCombo = new ComboBox<>();
            relationshipCombo.setPrefHeight(35.0);
            relationshipCombo.setPrefWidth(150.0);
            relationshipCombo.setPromptText("Chon quan he");
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
        ValidationResult ownerIdResult = FieldVerifier.verifyCitizenId(txtOwnerCitizenId.getText().trim());
        if (!ownerIdResult.isValid()) {
            showError(lblOwnerCitizenIdError, ownerIdResult.getMessage());
            isValid = false;
        }

        // Validate room selection
        ValidationResult roomResult = FieldVerifier.verifyNotEmpty(cmbRoom.getValue(), "Phòng");
        if (!roomResult.isValid()) {
            showError(lblRoomError, roomResult.getMessage());
            isValid = false;
        } else {
            try {
                if (!roomService.isRoomAvailable(cmbRoom.getValue())) {
                    showError(lblRoomError, "Phòng này đã có người thuê");
                    isValid = false;
                }
            } catch (Exception e) {
                showError(lblRoomError, "Lỗi kiểm tra phòng: " + e.getMessage());
                isValid = false;
            }
        }

        // Validate phone number
		String phone = txtPhone.getText().trim();
		ValidationResult phoneResult = FieldVerifier.verifyPhoneNumber(phone, "Số điện thoại");
		if (!phoneResult.isValid()) {
			showError(lblPhoneError, phoneResult.getMessage());
			isValid = false;
        }

        // Validate email
		String email = txtEmail.getText().trim();

		ValidationResult emailResult = FieldVerifier.verifyEmail(email, "Email");
		if (!emailResult.isValid()) {
			showError(lblEmailError, emailResult.getMessage());
			isValid = false;
		}

        // Validate resident count
        ValidationResult countResult = FieldVerifier.verifyResidentCount(txtResidentCount.getText().trim());
        if (!countResult.isValid()) {
            showError(lblResidentCountError, countResult.getMessage());
            isValid = false;
        }

        // Validate resident citizen IDs
        for (int i = 0; i < residentCitizenIdFields.size(); i++) {
            TextField field = residentCitizenIdFields.get(i);
            Label errorLabel = residentCitizenIdErrorLabels.get(i);
            ComboBox<RelationshipType> relationshipField = relationshipFields.get(i);
            
            String citizenId = field.getText().trim();
            ValidationResult residentResult = FieldVerifier.verifyCitizenId(citizenId);
            
            if (!residentResult.isValid()) {
                showError(errorLabel, residentResult.getMessage());
                isValid = false;
                continue;
            }
            
            // Validate relationship selection
            ValidationResult relationshipResult = FieldVerifier.verifyNotEmpty(
                relationshipField.getValue() != null ? relationshipField.getValue().toString() : null,
                "Quan hệ với chủ hộ"
            );
            if (!relationshipResult.isValid()) {
                showError(errorLabel, relationshipResult.getMessage());
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
        
        // Set owner citizen ID (already validated)
        String ownerCitizenId = txtOwnerCitizenId.getText().trim();
        Resident owner = residentService.getResidentByCitizenId(ownerCitizenId);
        household.setOwnerId(owner.getId());
        
        // Set room number (already validated)
        household.setHouseNumber(cmbRoom.getValue().trim());
        
        // Set default address info
        household.setStreet("Wall Street");
        household.setWard("Wall ward");
        household.setDistrict("Q2");
        
        // Set phone number if provided
		String phone = txtPhone.getText().trim();
		household.setPhone(phone);
        
		// Set email if provided
		String email = txtEmail.getText().trim();
		household.setEmail(email);
        
        // Set creation date
        household.setCreationDate(LocalDate.now());
        
        // Add residents and their relationships
        List<String> residentCitizenIds = new ArrayList<>();
        List<RelationshipType> relationships = new ArrayList<>();
        
        // Add other residents
        for (int i = 0; i < residentCitizenIdFields.size(); i++) {
            String citizenId = residentCitizenIdFields.get(i).getText().trim();
            RelationshipType relationship = relationshipFields.get(i).getValue();
            
            // Add to lists if valid
            residentCitizenIds.add(citizenId);
            relationships.add(relationship);
        }
        
        // Set residents first
        household.setResidentCitizenIds(residentCitizenIds);
        
        // Then update their relationships
        household.setRelationships(relationships);
        
        return household;
    }
    

    @FXML
    private void handleAdd() {
        // Clear previous errors
        hideError(lblOwnerCitizenIdError);
        hideError(lblRoomError);
        hideError(lblPhoneError);
        hideError(lblEmailError);
        hideError(lblResidentCountError);
        for (Label label : residentCitizenIdErrorLabels) {
            hideError(label);
        }

        try {
            // Validate all required fields first
            if (!validateRequiredFields()) {
                return;
            }

            // Create household from validated input
            Household newHousehold = createHouseholdFromInput();
            
            // Try to add the household
            householdController.addHousehold(newHousehold);
            
            // Show success message
            AlertUtils.showSuccessDialog("Them ho khau thanh cong", "Ho khau moi da duoc them vao he thong");
            
            // Close dialog and refresh parent view
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            
            ((Stage) btnAdd.getScene().getWindow()).close();
            
        } catch (HouseholdAlreadyExistsException e) {
            AlertUtils.showErrorAlert("Loi","Loi", "Ho khau da ton tai trong he thong");
        } catch (ResidentNotFoundException e) {
            AlertUtils.showErrorAlert("Loi","Loi", "Khong tim thay nguoi dan: " + e.getMessage());
        } catch (InvalidHouseholdDataException e) {
            AlertUtils.showErrorAlert("Loi","Loi", "Du lieu ho khau khong hop le: " + e.getMessage());
        } catch (Exception e) {
            AlertUtils.showErrorAlert("Loi","Loi he thong", "Co loi xay ra: " + e.getMessage());
            e.printStackTrace();
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