package views.stayabsence;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.StayAbsenceRecord;
import services.StayAbsenceService;
import views.BaseScreenWithLogoutAndGoBackHandler;
import views.messages.ConfirmationDialog;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StayAbsenceListHandler extends BaseScreenWithLogoutAndGoBackHandler {
    @FXML private Label lblUserName;
    @FXML private TableView<StayAbsenceRecord> tvRecords;
    @FXML private TableColumn<StayAbsenceRecord, Integer> colRecordId;
    @FXML private TableColumn<StayAbsenceRecord, String> colRecordType;
    @FXML private TableColumn<StayAbsenceRecord, String> colPersonName;
    @FXML private TableColumn<StayAbsenceRecord, String> colHousehold;
    @FXML private TableColumn<StayAbsenceRecord, String> colPeriod;
    @FXML private TableColumn<StayAbsenceRecord, String> colStartDate;
    @FXML private TableColumn<StayAbsenceRecord, String> colEndDate;
    @FXML private TableColumn<StayAbsenceRecord, String> colStatus;
    @FXML private TableColumn<StayAbsenceRecord, Void> colActions;
    
    @FXML private ComboBox<String> cbRecordType;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextField tfSearch;
    @FXML private Button btnSearch;
    @FXML private Button btnRefresh;
    
    private StayAbsenceService stayAbsenceService;
    private ObservableList<StayAbsenceRecord> recordList;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public StayAbsenceListHandler(Stage stage, String userName) throws Exception {
        super(stage, utils.Configs.STAY_ABSENCE_LIST_SCREEN, utils.Configs.LOGO_PATH, "Danh sách tạm trú & tạm vắng");
        this.stayAbsenceService = new StayAbsenceService();
        this.recordList = FXCollections.observableArrayList();
        
        loader.setController(this);
        this.setContent();
        this.setScene();
        this.setUsername(userName);
    }
    
    @FXML
    public void initialize() {
    	super.initialize();
        setupFilters();
        setupTableColumns();
        setupEventHandlers();
        loadRecords();
    }
    
    private void setUsername(String userName) {
        lblUserName.setText(userName);
    }
    
    private void setupFilters() {
        cbRecordType.getItems().addAll("Tất cả", "Tạm trú", "Tạm vắng");
        cbRecordType.setValue("Tất cả");
        
        cbStatus.getItems().addAll("Tất cả", "Đang hoạt động", "Đã hết hạn", "Đã hủy");
        cbStatus.setValue("Tất cả");
        
        tfSearch.setPromptText("Nhập tên, CCCD, phòng để tìm kiếm...");
    }
    
    private void setupTableColumns() {
        colRecordId.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        
        colRecordType.setCellValueFactory(cellData -> {
            String type = cellData.getValue().getRecordType();
            String displayType = StayAbsenceRecord.TYPE_TEMPORARY_STAY.equals(type) ? "Tạm trú" : "Tạm vắng";
            return new SimpleStringProperty(displayType);
        });
        
        colPersonName.setCellValueFactory(cellData -> {
            StayAbsenceRecord record = cellData.getValue();
            String name = record.getTempResidentName() != null ? 
                         record.getTempResidentName() : record.getResidentName();
            return new SimpleStringProperty(name != null ? name : "N/A");
        });
        
        colHousehold.setCellValueFactory(cellData -> {
            String household = cellData.getValue().getHouseholdName();
            return new SimpleStringProperty(household != null ? "Phòng " + household : "N/A");
        });
        
        colPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
        
        colStartDate.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(
                cellData.getValue().getStartDate() != null ? 
                cellData.getValue().getStartDate().format(dateFormatter) : "N/A"
            );
        });
        
        colEndDate.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(
                cellData.getValue().getEndDate() != null ? 
                cellData.getValue().getEndDate().format(dateFormatter) : "N/A"
            );
        });
        
        colStatus.setCellValueFactory(cellData -> {
            String status = cellData.getValue().getStatus();
            String displayStatus = "N/A";
            if (StayAbsenceRecord.STATUS_ACTIVE.equals(status)) {
                displayStatus = "Đang hoạt động";
            } else if (StayAbsenceRecord.STATUS_EXPIRED.equals(status)) {
                displayStatus = "Đã hết hạn";
            } else if (StayAbsenceRecord.STATUS_CANCELLED.equals(status)) {
                displayStatus = "Đã hủy";
            }
            return new SimpleStringProperty(displayStatus);
        });
        
        colActions.setCellFactory(col -> new TableCell<StayAbsenceRecord, Void>() {
            private final Button btnDetails = new Button("Chi tiết");
            private final Button btnCancel = new Button("Hủy");
            private final HBox actionBox = new HBox(5);
            
            {
                btnDetails.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");
                btnCancel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");
                
                actionBox.setAlignment(Pos.CENTER);
                actionBox.getChildren().addAll(btnDetails, btnCancel);
                
                btnDetails.setOnAction(e -> {
                    StayAbsenceRecord record = getTableView().getItems().get(getIndex());
                    showRecordDetails(record);
                });
                
                btnCancel.setOnAction(e -> {
                    StayAbsenceRecord record = getTableView().getItems().get(getIndex());
                    cancelRecord(record);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    StayAbsenceRecord record = getTableView().getItems().get(getIndex());
                    
                    if (StayAbsenceRecord.STATUS_ACTIVE.equals(record.getStatus())) {
                        actionBox.getChildren().clear();
                        actionBox.getChildren().addAll(btnDetails, btnCancel);
                    } else {
                        actionBox.getChildren().clear();
                        actionBox.getChildren().add(btnDetails);
                    }
                    
                    setGraphic(actionBox);
                }
            }
        });
        
        tvRecords.setItems(recordList);
    }
    
    private void setupEventHandlers() {
        btnSearch.setOnAction(e -> performSearch());
        btnRefresh.setOnAction(e -> loadRecords());
        tfSearch.setOnAction(e -> performSearch());
        cbRecordType.setOnAction(e -> performSearch());
        cbStatus.setOnAction(e -> performSearch());
        
        // Thêm tooltip cho button refresh
        Tooltip refreshTooltip = new Tooltip("Làm mới danh sách và tự động cập nhật hồ sơ hết hạn");
        Tooltip.install(btnRefresh, refreshTooltip);
    }
    
    private void loadRecords() {
        try {
            // 🔄 Auto-check và update expired records trước khi load
            int expiredCount = stayAbsenceService.updateExpiredRecords();
            if (expiredCount > 0) {
                System.out.println("📅 Auto-updated " + expiredCount + " expired records");
                // Hiển thị thông báo cho user
                InformationDialog.showNotification("Cập nhật tự động", 
                    "📅 Đã tự động cập nhật " + expiredCount + " hồ sơ hết hạn.\n" +
                    "Các hồ sơ này đã được chuyển sang trạng thái 'Đã hết hạn'.");
            }
            
            List<StayAbsenceRecord> records = stayAbsenceService.getAllRecords();
            recordList.clear();
            recordList.addAll(records);
            
            System.out.println("✅ Loaded " + records.size() + " records");
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Không thể tải danh sách hồ sơ: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void performSearch() {
        try {
            // 🔄 Auto-check expired records trước khi search
            stayAbsenceService.updateExpiredRecords();
            
            String searchKeyword = tfSearch.getText().trim();
            String selectedType = cbRecordType.getValue();
            String selectedStatus = cbStatus.getValue();
            
            List<StayAbsenceRecord> allRecords;
            
            if (searchKeyword.isEmpty()) {
                allRecords = stayAbsenceService.getAllRecords();
            } else {
                allRecords = stayAbsenceService.searchRecords(searchKeyword);
            }
            
            if (!"Tất cả".equals(selectedType)) {
                String filterType = "Tạm trú".equals(selectedType) ? 
                                  StayAbsenceRecord.TYPE_TEMPORARY_STAY : 
                                  StayAbsenceRecord.TYPE_TEMPORARY_ABSENCE;
                allRecords = allRecords.stream()
                    .filter(record -> filterType.equals(record.getRecordType()))
                    .toList();
            }
            
            if (!"Tất cả".equals(selectedStatus)) {
                String filterStatus;
                switch (selectedStatus) {
                    case "Đang hoạt động" -> filterStatus = StayAbsenceRecord.STATUS_ACTIVE;
                    case "Đã hết hạn" -> filterStatus = StayAbsenceRecord.STATUS_EXPIRED;
                    case "Đã hủy" -> filterStatus = StayAbsenceRecord.STATUS_CANCELLED;
                    default -> filterStatus = "";
                }
                allRecords = allRecords.stream()
                    .filter(record -> filterStatus.equals(record.getStatus()))
                    .toList();
            }
            
            recordList.clear();
            recordList.addAll(allRecords);
            
            System.out.println("🔍 Search results: " + allRecords.size() + " records found");
            
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi tìm kiếm", "Không thể thực hiện tìm kiếm: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showRecordDetails(StayAbsenceRecord record) {
        try {
            StringBuilder details = new StringBuilder();
            details.append("📋 CHI TIẾT HỒ SƠ #").append(record.getRecordId()).append("\n\n");
            
            if (StayAbsenceRecord.TYPE_TEMPORARY_STAY.equals(record.getRecordType())) {
                details.append("🏠 THÔNG TIN TẠM TRÚ\n");
                details.append("• Họ tên: ").append(record.getTempResidentName()).append("\n");
                if (record.getTempResidentCccd() != null) {
                    details.append("• CCCD: ").append(record.getTempResidentCccd()).append("\n");
                }
                details.append("• Ngày sinh: ").append(
                    record.getTempResidentBirthDate() != null ? 
                    record.getTempResidentBirthDate().format(dateFormatter) : "N/A"
                ).append("\n");
                details.append("• Giới tính: ").append(record.getTempResidentGender()).append("\n");
                if (record.getTempResidentPhone() != null) {
                    details.append("• SĐT: ").append(record.getTempResidentPhone()).append("\n");
                }
                if (record.getTempResidentHometown() != null) {
                    details.append("• Quê quán: ").append(record.getTempResidentHometown()).append("\n");
                }
            } else {
                details.append("✈️ THÔNG TIN TẠM VẮNG\n");
                details.append("• Thành viên: ").append(record.getResidentName()).append("\n");
                details.append("• Địa điểm đến: ").append(record.getTempAddress()).append("\n");
            }
            
            details.append("\n🏡 THÔNG TIN HỘ KHẨU\n");
            details.append("• Hộ khẩu: Phòng ").append(record.getHouseholdName()).append("\n");
            
            details.append("\n⏰ THỜI GIAN\n");
            details.append("• Từ ngày: ").append(
                record.getStartDate() != null ? record.getStartDate().format(dateFormatter) : "N/A"
            ).append("\n");
            details.append("• Đến ngày: ").append(
                record.getEndDate() != null ? record.getEndDate().format(dateFormatter) : "N/A"
            ).append("\n");
            details.append("• Thời gian: ").append(record.getPeriod()).append("\n");
            
            details.append("\n📝 THÔNG TIN KHÁC\n");
            details.append("• Ngày tạo: ").append(
                record.getCreatedDate() != null ? record.getCreatedDate().format(dateFormatter) : "N/A"
            ).append("\n");
            details.append("• Trạng thái: ");
            switch (record.getStatus()) {
                case StayAbsenceRecord.STATUS_ACTIVE -> details.append("Đang hoạt động");
                case StayAbsenceRecord.STATUS_EXPIRED -> details.append("Đã hết hạn");
                case StayAbsenceRecord.STATUS_CANCELLED -> details.append("Đã hủy");
                default -> details.append("N/A");
            }
            details.append("\n");
            
            if (record.getRequestDesc() != null && !record.getRequestDesc().trim().isEmpty()) {
                details.append("• Lý do: ").append(record.getRequestDesc()).append("\n");
            }
            
            InformationDialog.showNotification("Chi tiết hồ sơ", details.toString());
            
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Không thể hiển thị chi tiết hồ sơ!");
            e.printStackTrace();
        }
    }
    
    private void cancelRecord(StayAbsenceRecord record) {
        try {
            String recordType = StayAbsenceRecord.TYPE_TEMPORARY_STAY.equals(record.getRecordType()) ? 
                               "tạm trú" : "tạm vắng";
            String personName = record.getTempResidentName() != null ? 
                               record.getTempResidentName() : record.getResidentName();
            
            String message = "Bạn có chắc chắn muốn hủy hồ sơ " + recordType + " cho " + personName + "?\n\n" +
                           "Hồ sơ #" + record.getRecordId() + " sẽ được đánh dấu là 'Đã hủy'.\n" +
                           "Hành động này có thể được hoàn tác bằng cách cập nhật trạng thái.";
                           
            String result = ConfirmationDialog.getOption(message);
            
            if ("YES".equals(result)) {
                boolean success = stayAbsenceService.cancelRecord(record.getRecordId());
                
                if (success) {
                    InformationDialog.showNotification("Thành công", 
                        "✅ Đã hủy hồ sơ " + recordType + " cho " + personName + "!");
                    
                    performSearch();
                } else {
                    ErrorDialog.showError("Lỗi", "Không thể hủy hồ sơ!");
                }
            }
            
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi hệ thống", "Không thể hủy hồ sơ: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 