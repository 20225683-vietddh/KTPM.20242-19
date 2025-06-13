package views.stayabsence;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.control.*;
import models.StayAbsenceRecord;
import models.Household;
import services.HouseholdService;
import services.StayAbsenceService;
import views.BaseScreenHandler;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;
import java.time.LocalDate;
import java.util.List;

public class TemporaryStayFormHandler extends BaseScreenHandler {
    @FXML private TextField tfFullName;
    @FXML private TextField tfCccd;
    @FXML private DatePicker dpBirthDate;
    @FXML private ComboBox<String> cbGender;
    @FXML private TextField tfPhone;
    @FXML private TextField tfHometown;
    @FXML private ComboBox<String> cbHousehold;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private TextField tfTempAddress;
    @FXML private TextArea taRequestDesc;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    
    private final Stage ownerStage;
    private StayAbsenceHandler parentHandler;
    private HouseholdService householdService;
    private StayAbsenceService stayAbsenceService;
    private List<Household> households;
    
    public TemporaryStayFormHandler(Stage ownerStage, StayAbsenceHandler parentHandler) throws Exception {
        super(new Stage(), utils.Configs.TEMPORARY_STAY_FORM, utils.Configs.LOGO_PATH, "Cấp tạm trú");
        loader.setController(this);
        this.ownerStage = ownerStage;
        this.parentHandler = parentHandler;
        this.householdService = new HouseholdService();
        this.stayAbsenceService = new StayAbsenceService();
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
        setupEventHandlers();
    }
    
    private void setupForm() {
        // Setup gender options
        cbGender.getItems().addAll("Nam", "Nữ", "Khác");
        cbGender.setValue("Nam");
        
        // Setup default dates
        dpStartDate.setValue(LocalDate.now());
        dpEndDate.setValue(LocalDate.now().plusMonths(3)); // Default 3 months
        
        // Setup household options
        setupHouseholdOptions();
        
        // Setup validation
        tfFullName.setPromptText("Nhập họ tên người tạm trú");
        tfCccd.setPromptText("Số CCCD (không bắt buộc)");
        tfPhone.setPromptText("Số điện thoại liên hệ");
        tfHometown.setPromptText("Quê quán/Địa chỉ thường trú");
        tfTempAddress.setPromptText("Địa chỉ tạm trú (có thể để trống)");
        taRequestDesc.setPromptText("Lý do xin tạm trú, mối quan hệ với chủ hộ...");
    }
    
    private void setupEventHandlers() {
        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> handleCancel());
        
        // Date validation
        dpStartDate.setOnAction(e -> validateDates());
        dpEndDate.setOnAction(e -> validateDates());
    }
    
    private void validateDates() {
        if (dpStartDate.getValue() != null && dpEndDate.getValue() != null) {
            if (dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
                ErrorDialog.showError("Lỗi ngày tháng", "Ngày kết thúc phải sau ngày bắt đầu!");
                dpEndDate.setValue(dpStartDate.getValue().plusDays(1));
            }
        }
    }
    
    private void setupHouseholdOptions() {
        try {
            households = householdService.getAllHouseholds();
            cbHousehold.getItems().clear();
            
            for (Household household : households) {
                String option = "Phòng " + household.getHouseNumber() + " - " + 
                               household.getStreet() + ", " + household.getWard() +
                               " (" + household.getNumberOfResidents() + " người)";
                cbHousehold.getItems().add(option);
            }
            
            cbHousehold.setPromptText("Chọn hộ khẩu tiếp nhận");
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Không thể tải danh sách hộ khẩu!");
            e.printStackTrace();
        }
    }
    
    private void handleSave() {
        try {
            // Validate required fields
            if (!validateForm()) {
                return;
            }
            
            // Create StayAbsenceRecord object
            StayAbsenceRecord record = new StayAbsenceRecord();
            record.setRecordType(StayAbsenceRecord.TYPE_TEMPORARY_STAY);
            
            // Temporary resident info
            record.setTempResidentName(tfFullName.getText().trim());
            
            String cccd = tfCccd.getText().trim();
            if (!cccd.isEmpty()) {
                record.setTempResidentCccd(cccd);
            }
            
            record.setTempResidentBirthDate(dpBirthDate.getValue());
            record.setTempResidentGender(cbGender.getValue());
            
            String phone = tfPhone.getText().trim();
            if (!phone.isEmpty()) {
                record.setTempResidentPhone(phone);
            }
            
            String hometown = tfHometown.getText().trim();
            if (!hometown.isEmpty()) {
                record.setTempResidentHometown(hometown);
            }
            
            // Household info
            int selectedIndex = cbHousehold.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < households.size()) {
                record.setHouseholdId(households.get(selectedIndex).getHouseholdId());
            }
            
            // Time period
            record.setStartDate(dpStartDate.getValue());
            record.setEndDate(dpEndDate.getValue());
            
            String tempAddress = tfTempAddress.getText().trim();
            if (!tempAddress.isEmpty()) {
                record.setTempAddress(tempAddress);
            }
            
            record.setRequestDesc(taRequestDesc.getText().trim());
            
            // Save to database via service
            boolean success = stayAbsenceService.addTemporaryStayRecord(record);
            
            if (success) {
                InformationDialog.showNotification("Thành công", 
                    "✅ Đã cấp tạm trú cho " + record.getTempResidentName() + "!\n\n" +
                    "📋 Thông tin:\n" +
                    "• Thời gian: " + record.getPeriod() + "\n" +
                    "• Hộ khẩu: " + cbHousehold.getValue() + "\n" +
                    "• Mã hồ sơ: " + record.getRecordId());
                
                Stage stage = (Stage) btnSave.getScene().getWindow();
                stage.close();
            } else {
                ErrorDialog.showError("Lỗi", "Không thể lưu thông tin tạm trú!");
            }
            
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi hệ thống", "Không thể cấp tạm trú: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validateForm() {
        // Validate name
        if (tfFullName.getText() == null || tfFullName.getText().trim().isEmpty()) {
            ErrorDialog.showError("Lỗi nhập liệu", "Vui lòng nhập họ tên người tạm trú!");
            tfFullName.requestFocus();
            return false;
        }
        
        // Validate birth date
        if (dpBirthDate.getValue() == null) {
            ErrorDialog.showError("Lỗi nhập liệu", "Vui lòng chọn ngày sinh!");
            return false;
        }
        
        // Validate birth date not in future
        if (dpBirthDate.getValue().isAfter(LocalDate.now())) {
            ErrorDialog.showError("Lỗi ngày tháng", "Ngày sinh không thể trong tương lai!");
            return false;
        }
        
        // Validate household selection
        if (cbHousehold.getValue() == null) {
            ErrorDialog.showError("Lỗi nhập liệu", "Vui lòng chọn hộ khẩu tiếp nhận!");
            return false;
        }
        
        // Validate dates
        if (dpStartDate.getValue() == null) {
            ErrorDialog.showError("Lỗi nhập liệu", "Vui lòng chọn ngày bắt đầu!");
            return false;
        }
        
        if (dpEndDate.getValue() == null) {
            ErrorDialog.showError("Lỗi nhập liệu", "Vui lòng chọn ngày kết thúc!");
            return false;
        }
        
        if (dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
            ErrorDialog.showError("Lỗi ngày tháng", "Ngày kết thúc phải sau ngày bắt đầu!");
            return false;
        }
        
        // Validate start date (not in the past)
        if (dpStartDate.getValue().isBefore(LocalDate.now())) {
            ErrorDialog.showError("Lỗi ngày tháng", "Ngày bắt đầu không thể trong quá khứ!");
            return false;
        }
        
        // Validate request description
        if (taRequestDesc.getText() == null || taRequestDesc.getText().trim().isEmpty()) {
            ErrorDialog.showError("Lỗi nhập liệu", "Vui lòng nhập lý do xin tạm trú!");
            taRequestDesc.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void handleCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
} 