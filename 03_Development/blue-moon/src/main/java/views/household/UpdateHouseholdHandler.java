package views.household;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import models.Household;
import services.HouseholdService;
import views.BaseScreenHandler;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;
import java.time.LocalDate;

public class UpdateHouseholdHandler extends BaseScreenHandler {
    @FXML private Label lblHouseholdId;
    @FXML private TextField tfHouseNumber;
    @FXML private TextField tfStreet;
    @FXML private TextField tfWard;
    @FXML private TextField tfDistrict;
    @FXML private DatePicker dpRegistrationDate;
    @FXML private Label lblNumberOfResidents;
    @FXML private TextField tfAreas;
    @FXML private TextField tfHeadResidentId;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    
    private final Stage ownerStage;
    private Household household;
    private HouseholdHandler parentHandler;
    
    public UpdateHouseholdHandler(Stage ownerStage, Household household, HouseholdHandler parentHandler) throws Exception {
        super(new Stage(), utils.Configs.HOUSEHOLD_UPDATE_FORM, utils.Configs.LOGO_PATH, "Cập nhật thông tin hộ khẩu");
        loader.setController(this);
        this.ownerStage = ownerStage;
        this.household = household;
        this.parentHandler = parentHandler;
        this.setContent();
        this.setScene();

        Parent parentRoot = ownerStage.getScene().getRoot();
        GaussianBlur blur = new GaussianBlur(10);
        parentRoot.setEffect(blur);

        this.stage.setOnHidden(e -> parentRoot.setEffect(null));
        this.showPopup(ownerStage);
    }
    
    @FXML
    public void initialize() {
        setupForm();
        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> handleCancel());
    }
    
    private void setupForm() {
        lblHouseholdId.setText("ID: " + household.getHouseholdId());
        tfHouseNumber.setText(household.getHouseNumber());
        tfStreet.setText(household.getStreet());
        tfWard.setText(household.getWard());
        tfDistrict.setText(household.getDistrict());
        
        if (household.getRegistrationDate() != null) {
            dpRegistrationDate.setValue(household.getRegistrationDate());
        }
        
        lblNumberOfResidents.setText(String.valueOf(household.getNumberOfResidents()) + " người");
        tfAreas.setText(String.valueOf(household.getAreas()));
        tfHeadResidentId.setText(String.valueOf(household.getHeadResidentId()));
    }
    
    private void handleSave() {
        try {
            // Validate inputs
            if (tfHouseNumber.getText().trim().isEmpty()) {
                ErrorDialog.showError("Lỗi", "Vui lòng nhập số nhà!");
                return;
            }
            
            if (tfStreet.getText().trim().isEmpty()) {
                ErrorDialog.showError("Lỗi", "Vui lòng nhập tên đường!");
                return;
            }
            
            if (tfWard.getText().trim().isEmpty()) {
                ErrorDialog.showError("Lỗi", "Vui lòng nhập phường/xã!");
                return;
            }
            
            if (tfDistrict.getText().trim().isEmpty()) {
                ErrorDialog.showError("Lỗi", "Vui lòng nhập quận/huyện!");
                return;
            }
            
            // Update household object
            household.setHouseNumber(tfHouseNumber.getText().trim());
            household.setStreet(tfStreet.getText().trim());
            household.setWard(tfWard.getText().trim());
            household.setDistrict(tfDistrict.getText().trim());
            
            if (dpRegistrationDate.getValue() != null) {
                household.setRegistrationDate(dpRegistrationDate.getValue());
            }
            
            // Số nhân khẩu được tính tự động từ database, không cần cập nhật
            
            try {
                int areas = Integer.parseInt(tfAreas.getText().trim());
                household.setAreas(areas);
            } catch (NumberFormatException e) {
                ErrorDialog.showError("Lỗi", "Diện tích phải là số nguyên hợp lệ!");
                return;
            }
            
            try {
                int headResidentId = Integer.parseInt(tfHeadResidentId.getText().trim());
                household.setHeadResidentId(headResidentId);
            } catch (NumberFormatException e) {
                ErrorDialog.showError("Lỗi", "ID chủ hộ phải là số nguyên hợp lệ!");
                return;
            }
            
            // Save to database using HouseholdService
            HouseholdService householdService = new HouseholdService();
            boolean success = householdService.updateHousehold(household);
            
            if (success) {
                InformationDialog.showNotification("Thành công", "Cập nhật thông tin hộ khẩu thành công!");
                
                // Refresh parent list
                if (parentHandler != null) {
                    parentHandler.loadHouseholdList("");
                }
            } else {
                ErrorDialog.showError("Lỗi", "Không thể cập nhật thông tin hộ khẩu vào cơ sở dữ liệu!");
                return;
            }
            
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
            
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi hệ thống", "Không thể cập nhật thông tin hộ khẩu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
} 