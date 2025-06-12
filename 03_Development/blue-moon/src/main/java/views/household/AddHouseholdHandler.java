package views.household;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Household;
import models.Resident;
import models.Room;
import services.HouseholdService;
import services.ResidentService;
import services.RoomService;
import views.BaseScreenHandler;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddHouseholdHandler extends BaseScreenHandler {
    // Household fields
    @FXML private ComboBox<String> cbRoom;
    @FXML private TextField tfStreet;
    @FXML private TextField tfWard;
    @FXML private TextField tfDistrict;
    @FXML private DatePicker dpRegistrationDate;
    @FXML private TextField tfAreas;
    
    // Resident management
    @FXML private VBox vbResidentList;
    @FXML private Button btnAddResident;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    
    // Current resident form fields
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
    @FXML private Button btnAddToList;
    @FXML private Button btnClearForm;
    
    private final Stage ownerStage;
    private HouseholdHandler parentHandler;
    private HouseholdService householdService;
    private ResidentService residentService;
    private RoomService roomService;
    private List<Room> availableRooms;
    private List<Resident> residentList;
    private boolean hasHeadResident = false;
    
    public AddHouseholdHandler(Stage ownerStage, HouseholdHandler parentHandler) throws Exception {
        super(new Stage(), utils.Configs.HOUSEHOLD_ADD_FORM, utils.Configs.LOGO_PATH, "Thêm hộ khẩu mới");
        loader.setController(this);
        this.ownerStage = ownerStage;
        this.parentHandler = parentHandler;
        this.householdService = new HouseholdService();
        try {
            this.residentService = new ResidentService();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Không thể khởi tạo ResidentService: " + e.getMessage());
        }
        this.roomService = new RoomService();
        this.residentList = new ArrayList<>();
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
        loadAvailableRooms();
        updateResidentList();
    }
    
    private void setupForm() {
        // Setup gender options
        cbGender.getItems().addAll("Nam", "Nữ", "Khác");
        cbGender.setValue("Nam");
        
        // Setup relationship options
        cbRelationshipWithHead.getItems().addAll("Chủ hộ", "Vợ", "Chồng", "Con", "Cha", "Mẹ", "Anh", "Chị", "Em", "Khác");
        cbRelationshipWithHead.setValue("Chủ hộ");
        
        // Set default values
        tfEthnicity.setText("Kinh");
        tfReligion.setText("Không");
        dpRegistrationDate.setValue(LocalDate.now());
        tfDistrict.setText("Hà Đông");
        tfWard.setText("Phú La");
        tfStreet.setText("Hà Nội");
    }
    
    private void setupEventHandlers() {
        btnAddToList.setOnAction(e -> handleAddResident());
        btnClearForm.setOnAction(e -> handleClearForm());
        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> handleCancel());
        
        // Auto-update relationship when adding first resident
        cbRelationshipWithHead.setOnAction(e -> {
            if ("Chủ hộ".equals(cbRelationshipWithHead.getValue()) && hasHeadResident) {
                ErrorDialog.showError("Lỗi", "Hộ khẩu đã có chủ hộ!");
                cbRelationshipWithHead.setValue("Con");
            }
        });
    }
    
    private void loadAvailableRooms() {
        try {
            availableRooms = roomService.getEmptyRooms();
            cbRoom.getItems().clear();
            
            for (Room room : availableRooms) {
                cbRoom.getItems().add(room.getRoomNumber());
            }
            
            if (!availableRooms.isEmpty()) {
                cbRoom.setValue(availableRooms.get(0).getRoomNumber());
            } else {
                ErrorDialog.showError("Thông báo", "Hiện tại không có phòng trống nào!");
                handleCancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi", "Không thể tải danh sách phòng trống!");
        }
    }
    
    private void handleAddResident() {
        try {
            // Validate resident input
            if (tfFullName.getText().trim().isEmpty()) {
                ErrorDialog.showError("Lỗi", "Vui lòng nhập họ tên!");
                return;
            }
            
            if (dpDateOfBirth.getValue() == null) {
                ErrorDialog.showError("Lỗi", "Vui lòng chọn ngày sinh!");
                return;
            }
            
            if (tfCitizenId.getText().trim().isEmpty()) {
                ErrorDialog.showError("Lỗi", "Vui lòng nhập số CCCD!");
                return;
            }
            
            // Check if citizen ID already exists in current list
            for (Resident r : residentList) {
                if (r.getCitizenId().equals(tfCitizenId.getText().trim())) {
                    ErrorDialog.showError("Lỗi", "Số CCCD đã tồn tại trong danh sách!");
                    return;
                }
            }
            
            // Check head resident constraint
            String relationship = cbRelationshipWithHead.getValue();
            if ("Chủ hộ".equals(relationship)) {
                if (hasHeadResident) {
                    ErrorDialog.showError("Lỗi", "Hộ khẩu chỉ có thể có 1 chủ hộ!");
                    return;
                }
                hasHeadResident = true;
            }
            
            // Create resident object
            Resident resident = new Resident();
            resident.setFullName(tfFullName.getText().trim());
            resident.setDateOfBirth(dpDateOfBirth.getValue());
            resident.setGender(cbGender.getValue());
            resident.setEthnicity(tfEthnicity.getText().trim());
            resident.setReligion(tfReligion.getText().trim());
            resident.setCitizenId(tfCitizenId.getText().trim());
            
            if (dpDateOfIssue.getValue() != null) {
                resident.setDateOfIssue(dpDateOfIssue.getValue());
            }
            
            resident.setPlaceOfIssue(tfPlaceOfIssue.getText().trim());
            resident.setOccupation(tfOccupation.getText().trim());
            resident.setNotes(taNotes.getText().trim());
            resident.setRelationshipWithHead(relationship);
            resident.setAddedDate(LocalDate.now());
            
            // Add to list
            residentList.add(resident);
            updateResidentList();
            handleClearForm();
            
            InformationDialog.showNotification("Thành công", "Đã thêm thành viên: " + resident.getFullName());
            
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi", "Không thể thêm thành viên!");
        }
    }
    
    private void updateResidentList() {
        vbResidentList.getChildren().clear();
        
        if (residentList.isEmpty()) {
            Label lblEmpty = new Label("Chưa có thành viên nào. Vui lòng thêm ít nhất 1 chủ hộ.");
            lblEmpty.setStyle("-fx-text-fill: #e74c3c; -fx-font-style: italic;");
            vbResidentList.getChildren().add(lblEmpty);
        } else {
            for (int i = 0; i < residentList.size(); i++) {
                Resident resident = residentList.get(i);
                ResidentListItem item = new ResidentListItem(resident, i, this);
                vbResidentList.getChildren().add(item);
            }
        }
    }
    
    public void removeResident(int index) {
        if (index >= 0 && index < residentList.size()) {
            Resident removed = residentList.remove(index);
            if ("Chủ hộ".equals(removed.getRelationshipWithHead())) {
                hasHeadResident = false;
            }
            updateResidentList();
            InformationDialog.showNotification("Thành công", "Đã xóa thành viên: " + removed.getFullName());
        }
    }
    
    private void handleClearForm() {
        tfFullName.clear();
        dpDateOfBirth.setValue(null);
        cbGender.setValue("Nam");
        tfEthnicity.setText("Kinh");
        tfReligion.setText("Không");
        tfCitizenId.clear();
        dpDateOfIssue.setValue(null);
        tfPlaceOfIssue.clear();
        tfOccupation.clear();
        taNotes.clear();
        
        // Set relationship based on current state
        if (!hasHeadResident) {
            cbRelationshipWithHead.setValue("Chủ hộ");
        } else {
            cbRelationshipWithHead.setValue("Con");
        }
    }
    
    private void handleSave() {
        try {
            // Validate household input
            if (cbRoom.getValue() == null) {
                ErrorDialog.showError("Lỗi", "Vui lòng chọn phòng!");
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
            
            if (dpRegistrationDate.getValue() == null) {
                ErrorDialog.showError("Lỗi", "Vui lòng chọn ngày đăng ký!");
                return;
            }
            
            // Validate residents
            if (residentList.isEmpty()) {
                ErrorDialog.showError("Lỗi", "Vui lòng thêm ít nhất 1 thành viên!");
                return;
            }
            
            if (!hasHeadResident) {
                ErrorDialog.showError("Lỗi", "Hộ khẩu phải có 1 chủ hộ!");
                return;
            }
            
            // Create household object
            Household household = new Household();
            household.setHouseNumber(cbRoom.getValue());
            household.setStreet(tfStreet.getText().trim());
            household.setWard(tfWard.getText().trim());
            household.setDistrict(tfDistrict.getText().trim());
            household.setRegistrationDate(dpRegistrationDate.getValue());
            
            try {
                int areas = Integer.parseInt(tfAreas.getText().trim());
                household.setAreas(areas);
            } catch (NumberFormatException e) {
                household.setAreas(0);
            }
            
            // Save household and residents
            boolean success = householdService.addHouseholdWithResidents(household, residentList);
            
            if (success) {
                // Update room status
                roomService.updateRoomStatus(cbRoom.getValue(), false);
                
                InformationDialog.showNotification("Thành công", 
                    "Đã thêm hộ khẩu mới với " + residentList.size() + " thành viên!");
                
                // Refresh parent list
                if (parentHandler != null) {
                    parentHandler.loadHouseholdList("");
                }
                
                Stage stage = (Stage) btnSave.getScene().getWindow();
                stage.close();
            } else {
                ErrorDialog.showError("Lỗi", "Không thể thêm hộ khẩu!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi", "Đã xảy ra lỗi khi lưu hộ khẩu!");
        }
    }
    
    private void handleCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
} 