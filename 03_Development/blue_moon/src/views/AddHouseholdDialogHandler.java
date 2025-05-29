package views;

import controllers.HouseholdController;
import exception.HouseholdAlreadyExistsException;
import exception.InvalidHouseholdDataException;
import exception.MemberNotFoundException;
import exception.ServiceException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Household;
import models.Member;
import services.MemberService;
import utils.FieldVerifier;
import utils.RelationshipHelper;
import utils.RelationshipType;
import utils.SceneUtils.HouseholdDialogHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JOptionPane;

import utils.AlertUtils;

public class AddHouseholdDialogHandler implements HouseholdDialogHandler {
    
    // Main form fields
    @FXML private TextField txtLeaderId;
    @FXML private TextField txtAddress;
    @FXML private TextField txtArea;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtMemberCount;
    
    // Error labels
    @FXML private Label lblLeaderIdError;
    @FXML private Label lblAddressError;
    @FXML private Label lblAreaError;
    @FXML private Label lblPhoneError;
    @FXML private Label lblEmailError;
    @FXML private Label lblMemberCountError;
    
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
            
            // Member ID text field
            TextField memberIdField = new TextField();
            memberIdField.setPrefHeight(35.0);
            memberIdField.setPromptText("Nhập ID thành viên " + (i + 1));
            memberIdField.setStyle("-fx-border-color: #d0d0d0; -fx-border-radius: 5; -fx-background-radius: 5;");
            memberIdField.setFont(Font.font(14.0));
            
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
            
            memberFieldBox.getChildren().addAll(memberLabel, memberIdField, errorLabel);
            memberFieldsContainer.getChildren().add(memberFieldBox);
            
            // Store references
            memberIdFields.add(memberIdField);
            memberIdErrorLabels.add(errorLabel);
        }
    }
    
    private boolean validateAllFields() {
        boolean isValid = true;
        
        // Validate Leader ID
        if (!FieldVerifier.isValidId(txtLeaderId.getText())) {
            showError(lblLeaderIdError, txtLeaderId.getText().trim().isEmpty() ? 
                FieldVerifier.getEmptyFieldErrorMessage() : FieldVerifier.getInvalidIdErrorMessage());
            isValid = false;
        }
        
        // Validate Address
        if (!FieldVerifier.isValidAddress(txtAddress.getText())) {
            showError(lblAddressError, FieldVerifier.getEmptyFieldErrorMessage());
            isValid = false;
        }
        
        // Validate Area
        if (!FieldVerifier.isValidArea(txtArea.getText())) {
            showError(lblAreaError, FieldVerifier.getEmptyFieldErrorMessage());
            isValid = false;
        }
        
        // Validate Phone
        if (!FieldVerifier.isValidPhoneNumber(txtPhone.getText())) {
            showError(lblPhoneError, txtPhone.getText().trim().isEmpty() ? 
                FieldVerifier.getEmptyFieldErrorMessage() : FieldVerifier.getPhoneErrorMessage());
            isValid = false;
        }
        
        // Validate Email
        if (!FieldVerifier.isValidEmail(txtEmail.getText())) {
            showError(lblEmailError, txtEmail.getText().trim().isEmpty() ? 
                FieldVerifier.getEmptyFieldErrorMessage() : FieldVerifier.getEmailErrorMessage());
            isValid = false;
        }
        
        // Validate Member Count
        if (!FieldVerifier.isValidMemberCount(txtMemberCount.getText())) {
            showError(lblMemberCountError, txtMemberCount.getText().trim().isEmpty() ? 
                FieldVerifier.getEmptyFieldErrorMessage() : FieldVerifier.getInvalidMemberCountErrorMessage());
            isValid = false;
        }
        
        // Validate Member IDs if fields are generated
        if (memberFieldsContainer.isVisible()) {
            for (int i = 0; i < memberIdFields.size(); i++) {
                TextField memberField = memberIdFields.get(i);
                Label errorLabel = memberIdErrorLabels.get(i);
                
                if (!FieldVerifier.isValidId(memberField.getText())) {
                    showError(errorLabel, memberField.getText().trim().isEmpty() ? 
                        FieldVerifier.getEmptyFieldErrorMessage() : FieldVerifier.getInvalidIdErrorMessage());
                    isValid = false;
                }
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
        household.setCreationDate( new java.sql.Date(System.currentTimeMillis())); // Auto-generated creation date
        // User input fields
        household.setOwnerId(txtLeaderId.getText().trim());
        household.setAddress(txtAddress.getText().trim());
        household.setArea(txtArea.getText().trim());
        household.setPhone(txtPhone.getText().trim().replaceAll("\\s+", "")); // Remove spaces
        household.setEmail(txtEmail.getText().trim());
        household.setHouseholdSize(Integer.parseInt(txtMemberCount.getText().trim()));
        
        // Collect member IDs
        List<String> memberIds = new ArrayList<>();
        memberIds.add(txtLeaderId.getText().trim()); // Leader is also a member
        
        for (TextField memberField : memberIdFields) {
            String memberId = memberField.getText().trim();
            if (!memberId.isEmpty() && !memberIds.contains(memberId)) {
                memberIds.add(memberId);
            }
            
        }
        //caan suy nghi them o cho nay , co chuyen huong sang member ko ? tuc la phai co member san roi moi them duoc vao household ? 
        household.setMemberIds(memberIds);
        
        return household;
    }
    

    @FXML
    private void handleAdd() {
        System.out.println("Add button clicked");
        
        // Validate all fields
        if (!validateAllFields()) {
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
            Household household = createHouseholdFromInput();
            
            
            // Add household through controller
            householdController.addHousehold(household);
            
            //update member 
            List<Member> members = new ArrayList<>();
            for (Member m : household.getMembers()) {
            	RelationshipType relationship = RelationshipHelper.determineRelationship(m.getDateOfBirth().toString(), m.getGender());
            	
            	m.setHouseholdHead(household.getOwnerId().equals(m.getId()));
            	m.setRelationship(relationship);
            	m.setHouseholdId(household.getId());
            	members.add(m);
            }
            memberService.updateMembers(members);
            
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
        } catch (Exception e) {
            e.printStackTrace();
            utils.AlertUtils.showErrorAlert("Lỗi hệ thống", "Đã xảy ra lỗi không mong muốn", "Vui lòng thử lại sau hoặc liên hệ hỗ trợ.");
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