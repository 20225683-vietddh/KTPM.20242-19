package views.stayabsence;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.control.*;
import models.StayAbsenceRecord;
import models.Resident;
import models.Household;
import services.ResidentService;
import services.HouseholdService;
import services.StayAbsenceService;
import views.BaseScreenHandler;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TemporaryAbsenceFormHandler extends BaseScreenHandler {
    @FXML private ComboBox<Household> cbHousehold;
    @FXML private TextField tfHouseholdAddress;
    @FXML private ComboBox<Resident> cbResident;
    @FXML private TextField tfCitizenId;
    @FXML private TextField tfRelationship;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private TextField tfDestination;
    @FXML private TextArea taRequestDesc;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    
    private final Stage ownerStage;
    private StayAbsenceHandler parentHandler;
    private ResidentService residentService;
    private HouseholdService householdService;
    private StayAbsenceService stayAbsenceService;
    private List<Household> households;
    
    public TemporaryAbsenceFormHandler(Stage ownerStage, StayAbsenceHandler parentHandler) throws Exception {
        super(new Stage(), utils.Configs.TEMPORARY_ABSENCE_FORM, utils.Configs.LOGO_PATH, "Cấp tạm vắng");
        loader.setController(this);
        this.ownerStage = ownerStage;
        this.parentHandler = parentHandler;
        this.residentService = new ResidentService();
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
        // Setup default dates
        dpStartDate.setValue(LocalDate.now());
        dpEndDate.setValue(LocalDate.now().plusDays(30)); // Default 30 days
        
        // Setup household options first
        setupHouseholdOptions();
        
        // Setup prompts
        tfDestination.setPromptText("Địa chỉ nơi đến (tạm vắng)");
        taRequestDesc.setPromptText("Lý do tạm vắng (công tác, du lịch, thăm người thân...)");
        
        // Set readonly fields
        tfHouseholdAddress.setEditable(false);
        tfCitizenId.setEditable(false);
        tfRelationship.setEditable(false);
        
        // Initially disable resident selection until household is chosen
        cbResident.setDisable(true);
        cbResident.setPromptText("Chọn hộ khẩu trước");
    }
    
    private void setupEventHandlers() {
        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> handleCancel());
        
        // Listen for household selection changes
        cbHousehold.setOnAction(e -> handleHouseholdSelection());
        
        // Listen for resident selection changes  
        cbResident.setOnAction(e -> handleResidentSelection());
        
        // Date validation
        dpStartDate.setOnAction(e -> validateDates());
        dpEndDate.setOnAction(e -> validateDates());
    }
    
    private void setupHouseholdOptions() {
        try {
            households = householdService.getAllHouseholds();
            cbHousehold.getItems().clear();
            
            for (Household household : households) {
                cbHousehold.getItems().add(household);
            }
            
            // Custom cell factory to display household info
            cbHousehold.setCellFactory(listView -> new ListCell<Household>() {
                @Override
                protected void updateItem(Household household, boolean empty) {
                    super.updateItem(household, empty);
                    if (empty || household == null) {
                        setText(null);
                    } else {
                        setText("Phòng " + household.getHouseNumber() + " - " + 
                               household.getStreet() + ", " + household.getWard());
                    }
                }
            });
            
            cbHousehold.setButtonCell(new ListCell<Household>() {
                @Override
                protected void updateItem(Household household, boolean empty) {
                    super.updateItem(household, empty);
                    if (empty || household == null) {
                        setText("Chọn hộ khẩu");
                    } else {
                        setText("Phòng " + household.getHouseNumber() + " - " + 
                               household.getStreet() + ", " + household.getWard());
                    }
                }
            });
            
            cbHousehold.setPromptText("Chọn hộ khẩu");
            
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Không thể tải danh sách hộ khẩu!");
            e.printStackTrace();
        }
    }
    
    private void handleHouseholdSelection() {
        Household selectedHousehold = cbHousehold.getSelectionModel().getSelectedItem();
        if (selectedHousehold != null) {
            try {
                // Update household address
                String address = selectedHousehold.getStreet() + ", " + 
                               selectedHousehold.getWard() + ", " + 
                               selectedHousehold.getDistrict();
                tfHouseholdAddress.setText(address);
                
                // Load residents of selected household
                setupResidentOptions(selectedHousehold.getHouseholdId());
                
                // Enable resident selection
                cbResident.setDisable(false);
                cbResident.setPromptText("Chọn thành viên");
                
                // Clear previous resident selection
                cbResident.getSelectionModel().clearSelection();
                tfCitizenId.clear();
                tfRelationship.clear();
                
                System.out.println("Selected household: " + selectedHousehold.getHouseNumber() + 
                    " with " + selectedHousehold.getNumberOfResidents() + " residents");
                
            } catch (Exception e) {
                ErrorDialog.showError("Lỗi", "Không thể tải thông tin hộ khẩu!");
                e.printStackTrace();
            }
        } else {
            // Clear all dependent fields
            tfHouseholdAddress.clear();
            cbResident.getItems().clear();
            cbResident.setDisable(true);
            cbResident.setPromptText("Chọn hộ khẩu trước");
            tfCitizenId.clear();
            tfRelationship.clear();
        }
    }
    
    private void setupResidentOptions(int householdId) {
        try {
            // Get all residents of the selected household
            List<Resident> allResidents = residentService.getAllResidents();
            List<Resident> householdResidents = allResidents.stream()
                .filter(resident -> resident.getHouseholdId() == householdId)
                .collect(Collectors.toList());
            
            cbResident.getItems().clear();
            cbResident.getItems().addAll(householdResidents);
            
            // Custom cell factory to display resident info
            cbResident.setCellFactory(listView -> new ListCell<Resident>() {
                @Override
                protected void updateItem(Resident resident, boolean empty) {
                    super.updateItem(resident, empty);
                    if (empty || resident == null) {
                        setText(null);
                    } else {
                        String displayText = resident.getFullName() + " (" + 
                                           resident.getRelationshipWithHead() + ")";
                        if (resident.getCitizenId() != null) {
                            displayText += " - " + resident.getCitizenId();
                        }
                        setText(displayText);
                    }
                }
            });
            
            cbResident.setButtonCell(new ListCell<Resident>() {
                @Override
                protected void updateItem(Resident resident, boolean empty) {
                    super.updateItem(resident, empty);
                    if (empty || resident == null) {
                        setText("Chọn thành viên");
                    } else {
                        String displayText = resident.getFullName() + " (" + 
                                           resident.getRelationshipWithHead() + ")";
                        setText(displayText);
                    }
                }
            });
            
            System.out.println("Loaded " + householdResidents.size() + " residents for household " + householdId);
            
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Không thể tải danh sách thành viên hộ khẩu!");
            e.printStackTrace();
        }
    }
    
    private void handleResidentSelection() {
        Resident selectedResident = cbResident.getSelectionModel().getSelectedItem();
        if (selectedResident != null) {
            // Update citizen ID field
            tfCitizenId.setText(selectedResident.getCitizenId() != null ? 
                selectedResident.getCitizenId() : "Chưa có CCCD");
            
            // Update relationship field
            tfRelationship.setText(selectedResident.getRelationshipWithHead());
            
            System.out.println("Selected resident: " + selectedResident.getFullName() + 
                " - Relationship: " + selectedResident.getRelationshipWithHead());
        } else {
            tfCitizenId.clear();
            tfRelationship.clear();
        }
    }
    
    private void validateDates() {
        if (dpStartDate.getValue() != null && dpEndDate.getValue() != null) {
            if (dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
                ErrorDialog.showError("Lỗi ngày tháng", "Ngày kết thúc phải sau ngày bắt đầu!");
                dpEndDate.setValue(dpStartDate.getValue().plusDays(1));
            }
        }
    }
    
    private void handleSave() {
        try {
            // Validate required fields
            if (!validateForm()) {
                return;
            }
            
            // Get selected data
            Household selectedHousehold = cbHousehold.getSelectionModel().getSelectedItem();
            Resident selectedResident = cbResident.getSelectionModel().getSelectedItem();
            
            // Create StayAbsenceRecord object
            StayAbsenceRecord record = new StayAbsenceRecord();
            record.setRecordType(StayAbsenceRecord.TYPE_TEMPORARY_ABSENCE);
            
            // Resident info
            record.setResidentId(selectedResident.getResidentId());
            record.setHouseholdId(selectedResident.getHouseholdId());
            
            // Time period and destination
            record.setStartDate(dpStartDate.getValue());
            record.setEndDate(dpEndDate.getValue());
            record.setTempAddress(tfDestination.getText().trim());
            record.setRequestDesc(taRequestDesc.getText().trim());
            
            // Save to database via service
            boolean success = stayAbsenceService.addTemporaryAbsenceRecord(record);
            
            if (success) {
                InformationDialog.showNotification("Thành công", 
                    "✅ Đã cấp tạm vắng cho " + selectedResident.getFullName() + "!\n\n" +
                    "📋 Thông tin:\n" +
                    "• Hộ khẩu: Phòng " + selectedHousehold.getHouseNumber() + "\n" +
                    "• Thời gian: " + record.getPeriod() + "\n" +
                    "• Nơi đến: " + record.getTempAddress() + "\n" +
                    "• Mã hồ sơ: " + record.getRecordId());
                
                Stage stage = (Stage) btnSave.getScene().getWindow();
                stage.close();
            } else {
                ErrorDialog.showError("Lỗi", "Không thể lưu thông tin tạm vắng!");
            }
            
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi hệ thống", "Không thể cấp tạm vắng: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validateForm() {
        // Validate household selection
        if (cbHousehold.getValue() == null) {
            ErrorDialog.showError("Lỗi nhập liệu", "Vui lòng chọn hộ khẩu!");
            return false;
        }
        
        // Validate resident selection
        if (cbResident.getValue() == null) {
            ErrorDialog.showError("Lỗi nhập liệu", "Vui lòng chọn thành viên cần cấp tạm vắng!");
            return false;
        }
        
        // Validate destination
        if (tfDestination.getText() == null || tfDestination.getText().trim().isEmpty()) {
            ErrorDialog.showError("Lỗi nhập liệu", "Vui lòng nhập địa điểm đến!");
            tfDestination.requestFocus();
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
            ErrorDialog.showError("Lỗi nhập liệu", "Vui lòng nhập lý do tạm vắng!");
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