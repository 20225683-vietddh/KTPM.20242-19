package views.resident;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import models.Resident;
import models.Household;
import services.HouseholdService;
import services.ResidentService;
import views.BaseScreenHandler;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;
import java.time.LocalDate;
import java.util.List;

public class UpdateResidentHandler extends BaseScreenHandler {
    @FXML private TextField tfFullName;
    @FXML private DatePicker dpDateOfBirth;
    @FXML private ComboBox<String> cbGender;
    @FXML private TextField tfEthnicity;
    @FXML private TextField tfReligion;
    @FXML private TextField tfCitizenId;
    @FXML private DatePicker dpDateOfIssue;
    @FXML private TextField tfPlaceOfIssue;
    @FXML private TextField tfOccupation;
    @FXML private TextArea taNotes;
    @FXML private ComboBox<String> cbRelationshipWithHead;
    @FXML private ComboBox<String> cbHousehold;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    
    private final Stage ownerStage;
    private ResidentHandler parentHandler;
    private HouseholdService householdService;
    private ResidentService residentService;
    private List<Household> households;
    private Resident resident;
    
    public UpdateResidentHandler(Stage ownerStage, Resident resident, ResidentHandler parentHandler) throws Exception {
        super(new Stage(), utils.Configs.RESIDENT_UPDATE_FORM, utils.Configs.LOGO_PATH, "Chỉnh sửa nhân khẩu");
        loader.setController(this);
        this.ownerStage = ownerStage;
        this.resident = resident;
        this.parentHandler = parentHandler;
        this.householdService = new HouseholdService();
        try {
            this.residentService = new ResidentService();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Không thể khởi tạo ResidentService: " + e.getMessage());
        }
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
        populateFields();
        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> handleCancel());
    }
    
    private void setupForm() {
        // Setup gender options
        cbGender.getItems().addAll("Nam", "Nữ", "Khác");
        
        // Setup relationship options
        cbRelationshipWithHead.getItems().addAll("Chủ hộ", "Vợ", "Chồng", "Con", "Cha", "Mẹ", "Anh", "Chị", "Em", "Khác");
        
        // Setup household options
        setupHouseholdOptions();
    }
    
    private void setupHouseholdOptions() {
        try {
            households = householdService.getAllHouseholds();
            cbHousehold.getItems().clear();
            
            for (Household household : households) {
                String option = "Hộ " + household.getHouseholdId() + " - " + household.getHouseNumber();
                cbHousehold.getItems().add(option);
            }
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Không thể tải danh sách hộ khẩu!");
        }
    }
    
    private void populateFields() {
        if (resident != null) {
            tfFullName.setText(resident.getFullName() != null ? resident.getFullName() : "");
            dpDateOfBirth.setValue(resident.getDateOfBirth());
            cbGender.setValue(resident.getGender() != null ? resident.getGender() : "Nam");
            tfEthnicity.setText(resident.getEthnicity() != null ? resident.getEthnicity() : "");
            tfReligion.setText(resident.getReligion() != null ? resident.getReligion() : "");
            tfCitizenId.setText(resident.getCitizenId() != null ? resident.getCitizenId() : "");
            dpDateOfIssue.setValue(resident.getDateOfIssue());
            tfPlaceOfIssue.setText(resident.getPlaceOfIssue() != null ? resident.getPlaceOfIssue() : "");
            tfOccupation.setText(resident.getOccupation() != null ? resident.getOccupation() : "");
            taNotes.setText(resident.getNotes() != null ? resident.getNotes() : "");
            cbRelationshipWithHead.setValue(resident.getRelationshipWithHead() != null ? 
                resident.getRelationshipWithHead() : "Con");
            
            // Set household selection
            if (resident.getHouseholdId() > 0 && households != null) {
                for (int i = 0; i < households.size(); i++) {
                    if (households.get(i).getHouseholdId() == resident.getHouseholdId()) {
                        cbHousehold.getSelectionModel().select(i);
                        break;
                    }
                }
            }
        }
    }
    
    private void handleSave() {
        try {
            // Validate input
            if (tfFullName.getText().trim().isEmpty()) {
                ErrorDialog.showError("Lỗi", "Vui lòng nhập họ tên!");
                return;
            }
            
            if (dpDateOfBirth.getValue() == null) {
                ErrorDialog.showError("Lỗi", "Vui lòng chọn ngày sinh!");
                return;
            }
            
            if (cbGender.getValue() == null) {
                ErrorDialog.showError("Lỗi", "Vui lòng chọn giới tính!");
                return;
            }
            
            // CCCD không bắt buộc
            String citizenIdInput = tfCitizenId.getText().trim();
            
            if (cbHousehold.getValue() == null) {
                ErrorDialog.showError("Lỗi", "Vui lòng chọn hộ khẩu!");
                return;
            }
            
            if (cbRelationshipWithHead.getValue() == null) {
                ErrorDialog.showError("Lỗi", "Vui lòng chọn quan hệ với chủ hộ!");
                return;
            }
            
            // Update resident object
            resident.setFullName(tfFullName.getText().trim());
            resident.setDateOfBirth(dpDateOfBirth.getValue());
            resident.setGender(cbGender.getValue());
            resident.setEthnicity(tfEthnicity.getText().trim());
            resident.setReligion(tfReligion.getText().trim());
            
            // Xử lý CCCD: nếu rỗng thì set null
            if (citizenIdInput.isEmpty()) {
                resident.setCitizenId(null);
            } else {
                resident.setCitizenId(citizenIdInput);
            }
            
            if (dpDateOfIssue.getValue() != null) {
                resident.setDateOfIssue(dpDateOfIssue.getValue());
            }
            
            resident.setPlaceOfIssue(tfPlaceOfIssue.getText().trim());
            resident.setOccupation(tfOccupation.getText().trim());
            resident.setNotes(taNotes.getText().trim());
            resident.setRelationshipWithHead(cbRelationshipWithHead.getValue());
            
            // Get household ID from selection
            int selectedIndex = cbHousehold.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < households.size()) {
                resident.setHouseholdId(households.get(selectedIndex).getHouseholdId());
            }
            
            // Save to database
            boolean success = residentService.updateResident(resident);
            
            if (success) {
                InformationDialog.showNotification("Thành công", "Cập nhật thông tin nhân khẩu thành công!");
                
                // Refresh parent list
                if (parentHandler != null) {
                    parentHandler.loadResidentList("", "Tất cả hộ khẩu");
                }
                
                Stage stage = (Stage) btnSave.getScene().getWindow();
                stage.close();
            } else {
                ErrorDialog.showError("Lỗi", "Không thể cập nhật thông tin nhân khẩu!");
            }
            
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi hệ thống", "Không thể cập nhật nhân khẩu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
} 