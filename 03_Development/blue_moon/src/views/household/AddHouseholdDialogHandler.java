    package views.household;

    import java.sql.SQLException;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;
    import java.lang.Object;
    import java.lang.String;

    import controllers.HouseholdController;
    import exception.HouseholdAlreadyExistsException;
    import exception.HouseholdNotExist;
    import exception.InvalidHouseholdDataException;
    import exception.MemberNotFoundException;
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
    import services.MemberService;
    import utils.FieldVerifier;
    import utils.RelationshipHelper;
    import utils.SceneUtils.HouseholdDialogHandler;
    import utils.enums.RelationshipType;
    import utils.AlertUtils;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.util.StringConverter;

    public class AddHouseholdDialogHandler implements HouseholdDialogHandler {
        
        // Main form fields
        @FXML private TextField txtLeaderId;
        @FXML private TextField txtAddress;
        @FXML private TextField txtArea;
        @FXML private TextField txtPhone;
        @FXML private TextField txtEmail;
        @FXML private TextField txtMemberCount;
        @FXML private TextField txtOwnerId;
        
        // Error labels
        @FXML private Label lblLeaderIdError;
        @FXML private Label lblAddressError;
        @FXML private Label lblAreaError;
        @FXML private Label lblPhoneError;
        @FXML private Label lblEmailError;
        @FXML private Label lblMemberCountError;
        @FXML private Label lblOwnerIdError;
        
        // Dynamic fields container
        @FXML private VBox memberFieldsContainer;
        @FXML private VBox contentVBox;
        
        // Buttons
        @FXML private Button btnAdd;
        @FXML private Button btnCancel;
        @FXML private Button btnGenerateFields;
        
        private Runnable onSuccessCallback;
        public MemberService getMemberService() {
            return memberService;
        }

        public void setMemberService(MemberService memberService) {
            this.memberService = memberService;
        }

        private HouseholdController householdController;
        private MemberService memberService;
        
        // Lists to store dynamic member fields and their error labels
        private List<TextField> memberIdFields = new ArrayList<>();
        private List<Label> memberIdErrorLabels = new ArrayList<>();
        private List<ComboBox<RelationshipType>> relationshipFields = new ArrayList<>();
        
        // Constructor không tham số cho FXML
        public AddHouseholdDialogHandler() {
            this.householdController = new HouseholdController(); // hoặc lấy từ singleton
        }
        
        //getter + setter
        public HouseholdController getHouseholdController() {
            return householdController;
        }

        public void setHouseholdController(HouseholdController householdController) {
            this.householdController = householdController;
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
        }
        
        private void addValidationListeners() {
            // Leader ID validation
            txtLeaderId.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    hideError(lblLeaderIdError);
                }
            });
            
            // Address validation
            txtAddress.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    hideError(lblAddressError);
                }
            });
            
            // Area validation
            txtArea.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    hideError(lblAreaError);
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
            
            // Member count validation
            txtMemberCount.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    hideError(lblMemberCountError);
                }
            });
        }
        
        
        @FXML
        private void handleGenerateMemberFields() {
            String memberCountText = txtMemberCount.getText().trim();
            
            if (!FieldVerifier.isValidMemberCount(memberCountText)) {
                showError(lblMemberCountError, FieldVerifier.getInvalidMemberCountErrorMessage());
                return;
            }
            
            int memberCount = Integer.parseInt(memberCountText);
            generateMemberIdFields(memberCount);
        }
        
        private void generateMemberIdFields(int memberCount) {
            // Clear existing fields
            memberFieldsContainer.getChildren().clear();
            memberIdFields.clear();
            memberIdErrorLabels.clear();
            relationshipFields.clear();
            
            // Show the container
            memberFieldsContainer.setVisible(true);
            
            // Add a header for member IDs section
            Label headerLabel = new Label("ID các thành viên trong hộ:");
            headerLabel.setTextFill(javafx.scene.paint.Color.web("#43a5dc"));
            headerLabel.setFont(Font.font("System Bold", 14.0));
            VBox.setMargin(headerLabel, new Insets(10, 0, 5, 0));
            memberFieldsContainer.getChildren().add(headerLabel);
            
            // Generate member ID input fields
            for (int i = 0; i < memberCount; i++) {
                VBox memberFieldBox = new VBox(5.0);
                
                // Member label
                Label memberLabel = new Label("Thành viên " + (i + 1) + ": *");
                memberLabel.setTextFill(javafx.scene.paint.Color.web("#43a5dc"));
                memberLabel.setFont(Font.font("System Bold", 12.0));
                
                // Create HBox for ID field and relationship dropdown
                HBox inputContainer = new HBox(10.0);
                
                // Member ID text field
                TextField memberIdField = new TextField();
                memberIdField.setPrefHeight(35.0);
                memberIdField.setPrefWidth(250.0);
                memberIdField.setPromptText("Nhập ID thành viên " + (i + 1));
                memberIdField.setStyle("-fx-border-color: #d0d0d0; -fx-border-radius: 5; -fx-background-radius: 5;");
                memberIdField.setFont(Font.font(14.0));
                
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
                inputContainer.getChildren().addAll(memberIdField, relationshipCombo);
                
                // Error label for this member field
                Label errorLabel = new Label();
                errorLabel.setTextFill(javafx.scene.paint.Color.web("#dc3545"));
                errorLabel.setFont(Font.font(12.0));
                errorLabel.setVisible(false);
                
                // Add real-time validation for member ID
                final int memberIndex = i;
                memberIdField.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.isEmpty()) {
                        hideError(errorLabel);
                    }
                });
                
                memberFieldBox.getChildren().addAll(memberLabel, inputContainer, errorLabel);
                memberFieldsContainer.getChildren().add(memberFieldBox);
                
                // Store references
                memberIdFields.add(memberIdField);
                memberIdErrorLabels.add(errorLabel);
                relationshipFields.add(relationshipCombo);
            }
        }
        
        private boolean validateRequiredFields() {
            boolean isValid = true;
            
            // Validate leader ID
            if (!FieldVerifier.isValidId(txtLeaderId.getText())) {
                showError(lblLeaderIdError, txtLeaderId.getText().trim().isEmpty() ? 
                    FieldVerifier.getEmptyFieldErrorMessage() : FieldVerifier.getInvalidIdErrorMessage());
                isValid = false;
            }
            
            // Validate address
            if (txtAddress.getText().trim().isEmpty()) {
                showError(lblAddressError, FieldVerifier.getEmptyFieldErrorMessage());
                isValid = false;
            }
            
            // Validate area
            if (txtArea.getText().trim().isEmpty()) {
                showError(lblAreaError, FieldVerifier.getEmptyFieldErrorMessage());
                isValid = false;
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
            
            // Validate member count
            if (!FieldVerifier.isValidMemberCount(txtMemberCount.getText())) {
                showError(lblMemberCountError, txtMemberCount.getText().trim().isEmpty() ? 
                    FieldVerifier.getEmptyFieldErrorMessage() : FieldVerifier.getInvalidMemberCountErrorMessage());
                isValid = false;
            }
            
            // Validate member fields if they are visible
            if (memberFieldsContainer.isVisible()) {
                String ownerId = txtLeaderId.getText().trim();
                boolean ownerFound = false;
                
                for (int i = 0; i < memberIdFields.size(); i++) {
                    TextField memberField = memberIdFields.get(i);
                    Label errorLabel = memberIdErrorLabels.get(i);
                    ComboBox<RelationshipType> relationshipCombo = relationshipFields.get(i);
                    
                    if (!FieldVerifier.isValidId(memberField.getText())) {
                        showError(errorLabel, memberField.getText().trim().isEmpty() ? 
                            FieldVerifier.getEmptyFieldErrorMessage() : FieldVerifier.getInvalidIdErrorMessage());
                        isValid = false;
                    }
                    
                    if (relationshipCombo.getValue() == null) {
                        showError(errorLabel, "Vui lòng chọn quan hệ với chủ hộ");
                        isValid = false;
                    }
                    
                    // Check if owner is in the member list
                    if (memberField.getText().trim().equals(ownerId)) {
                        ownerFound = true;
                    }
                }
                
                // Validate that owner is included in member list
                if (!ownerFound && isValid) {
                    showError(lblLeaderIdError, "Chủ hộ phải được bao gồm trong danh sách thành viên");
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
            household.setCreationDate(new java.sql.Date(System.currentTimeMillis()));
            
            // Get owner ID and set owner name
            String ownerId = txtLeaderId.getText().trim();
            household.setOwnerId(ownerId);
            Resident owner = memberService.getMemberById(ownerId);
            household.setOwnerName(owner.getFullName());
            
            // User input fields
            household.setAddress(txtAddress.getText().trim());
            household.setArea(txtArea.getText().trim());
            household.setPhone(txtPhone.getText().trim().replaceAll("\\s+", "")); // Remove spaces
            household.setEmail(txtEmail.getText().trim());
            household.setHouseholdSize(Integer.parseInt(txtMemberCount.getText().trim()));
            
            // Collect member IDs and their relationships
            List<String> memberIds = new ArrayList<>();
            List<RelationshipType> relationships = new ArrayList<>();
            
            // First, collect all members from the dynamic fields
            for (int i = 0; i < memberIdFields.size(); i++) {
                String memberId = memberIdFields.get(i).getText().trim();
                RelationshipType relationship = relationshipFields.get(i).getValue();
                
                if (!memberId.isEmpty() && relationship != null) {
                    memberIds.add(memberId);
                    relationships.add(relationship);
                }
            }
            
            // Check if owner ID is in the member list, if not add it with default relationship
            boolean ownerFound = false;
            for (int i = 0; i < memberIds.size(); i++) {
                if (memberIds.get(i).equals(ownerId)) {
                    ownerFound = true;
                    break;
                }
            }
            
            // If owner is not in the member list, add them
            if (!ownerFound) {
                memberIds.add(0, ownerId); // Add at beginning
                relationships.add(0, RelationshipType.FATHER); // Default relationship for household head
            }
            
            // Get members and set their relationships
            List<Resident> members = memberService.getMembersByMemberIds(memberIds);
            for (int i = 0; i < members.size() && i < relationships.size(); i++) {
                Resident member = members.get(i);
                RelationshipType relationship;
                
                // If this is the owner, set relationship to FATHER or appropriate value
                if (member.getId().equals(ownerId)) {
                    relationship = RelationshipType.FATHER; // Or determine based on gender/age
                } else {
                    relationship = relationships.get(i);
                }
                
                System.out.println("Setting relationship for member " + member.getId() + 
                    " (isOwner=" + member.getId().equals(ownerId) + "): " + relationship);
                
                member.setRelationship(relationship);
                member.setHouseholdHead(member.getId().equals(ownerId));
                
                // Update member in database immediately
                try {
                    memberService.updateMember(member);
                    System.out.println("Successfully updated member " + member.getId() + 
                        " with relationship " + member.getRelationship());
                } catch (Exception e) {
                    System.err.println("Error updating member " + member.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            household.setMembers(members);
            
            return household;
        }
        

        @FXML
        private void handleAdd() {
            System.out.println("Add button clicked");
            
            // Validate all fields
            if ( !validateRequiredFields()) {
                System.out.println("Validation failed");
                return;
            }
            
            // Check if member fields are generated
            if (!memberFieldsContainer.isVisible()) {
                showError(lblMemberCountError, "Vui lòng tạo trường nhập cho các thành viên");
                return;
            }
            
            try {
                // Create household object from input
                Household household = null;
                try {
                    household = createHouseholdFromInput();
                } catch (ServiceException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
                // Add household through controller - this will handle member updates
                try {
                    householdController.addHousehold(household);
                } catch (HouseholdNotExist e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ServiceException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                System.out.println("Household added successfully");
                
                // Show success message
                utils.AlertUtils.showInfoAlert("Thành công", "Thêm hộ khẩu thành công", 
                    "Hộ khẩu đã được thêm vào hệ thống.");
                
                // Call success callback and close dialog
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
                
                ((Stage) btnAdd.getScene().getWindow()).close();
                
            } catch (HouseholdAlreadyExistsException | MemberNotFoundException | InvalidHouseholdDataException e) {
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