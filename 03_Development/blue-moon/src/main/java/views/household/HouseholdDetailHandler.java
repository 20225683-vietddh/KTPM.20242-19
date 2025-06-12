package views.household;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import models.Household;
import models.Resident;
import views.BaseScreenHandler;
import views.messages.ErrorDialog;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class HouseholdDetailHandler extends BaseScreenHandler {
    @FXML private Label lblHouseholdId;
    @FXML private Label lblHouseNumber;
    @FXML private Label lblAddress;
    @FXML private Label lblRegistrationDate;
    @FXML private Label lblNumberOfResidents;
    @FXML private Label lblAreas;
    @FXML private Label lblHeadResident;
    @FXML private TableView<Resident> tblResidents;
    @FXML private Button btnHistory;
    @FXML private Button btnClose;
    
    private final Stage ownerStage;
    private Household household;
    
    public HouseholdDetailHandler(Stage ownerStage, Household household) throws Exception {
        super(new Stage(), utils.Configs.HOUSEHOLD_DETAIL_POPUP, utils.Configs.LOGO_PATH, "Chi tiết hộ khẩu");
        loader.setController(this);
        this.ownerStage = ownerStage;
        this.household = household;
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
        setupHouseholdInfo();
        setupResidentsTable();
        btnHistory.setOnAction(e -> handleViewHistory());
        btnClose.setOnAction(e -> handleClose());
    }
    
    private void setupHouseholdInfo() {
        try {
            // Lấy thông tin đầy đủ từ service
            services.HouseholdService householdService = new services.HouseholdService();
            Household fullHousehold = householdService.getHouseholdWithResidents(household.getHouseholdId());
            
            if (fullHousehold != null) {
                this.household = fullHousehold; // Cập nhật với thông tin đầy đủ
            }
            
            lblHouseholdId.setText("ID: " + household.getHouseholdId());
            lblHouseNumber.setText(household.getHouseNumber() != null ? household.getHouseNumber() : "Chưa có thông tin");
            
            String fullAddress = (household.getStreet() != null ? household.getStreet() : "") + ", " + 
                               (household.getWard() != null ? household.getWard() : "") + ", " + 
                               (household.getDistrict() != null ? household.getDistrict() : "");
            lblAddress.setText(fullAddress);
            
            if (household.getRegistrationDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                lblRegistrationDate.setText(household.getRegistrationDate().format(formatter));
            } else {
                lblRegistrationDate.setText("Chưa có thông tin");
            }
            
            lblNumberOfResidents.setText(String.valueOf(household.getNumberOfResidents()));
            lblAreas.setText(household.getAreas() + " m²");
            
            // Hiển thị tên chủ hộ nếu có
            if (household.getResidents() != null && !household.getResidents().isEmpty()) {
                String headResidentName = household.getResidents().stream()
                    .filter(r -> "Chủ hộ".equals(r.getRelationshipWithHead()))
                    .map(Resident::getFullName)
                    .findFirst()
                    .orElse("Chưa xác định");
                lblHeadResident.setText(headResidentName);
            } else {
                lblHeadResident.setText("Chưa có thông tin");
            }
            
        } catch (Exception e) {
            // Fallback to basic info if service fails
            lblHouseholdId.setText("ID: " + household.getHouseholdId());
            lblHouseNumber.setText(household.getHouseNumber() != null ? household.getHouseNumber() : "Chưa có thông tin");
            lblHeadResident.setText("Lỗi tải thông tin");
            e.printStackTrace();
        }
    }
    
    private void setupResidentsTable() {
        TableColumn<Resident, String> nameCol = new TableColumn<>("Họ tên");
        nameCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFullName()));
        nameCol.setPrefWidth(180);
        
        TableColumn<Resident, String> birthCol = new TableColumn<>("Ngày sinh");
        birthCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateOfBirth() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return new SimpleStringProperty(cellData.getValue().getDateOfBirth().format(formatter));
            }
            return new SimpleStringProperty("");
        });
        birthCol.setPrefWidth(100);
        
        TableColumn<Resident, String> genderCol = new TableColumn<>("Giới tính");
        genderCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getGender()));
        genderCol.setPrefWidth(80);
        
        TableColumn<Resident, String> relationCol = new TableColumn<>("Quan hệ với chủ hộ");
        relationCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRelationshipWithHead()));
        relationCol.setPrefWidth(150);
        
        TableColumn<Resident, String> citizenIdCol = new TableColumn<>("CCCD/CMND");
        citizenIdCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCitizenId()));
        citizenIdCol.setPrefWidth(120);
        
        TableColumn<Resident, String> occupationCol = new TableColumn<>("Nghề nghiệp");
        occupationCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getOccupation()));
        occupationCol.setPrefWidth(120);
        
        tblResidents.getColumns().addAll(nameCol, birthCol, genderCol, relationCol, citizenIdCol, occupationCol);
        tblResidents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        loadResidentsData();
    }
    
    private void loadResidentsData() {
        try {
            // Lấy dữ liệu từ service
            services.HouseholdService householdService = new services.HouseholdService();
            Household fullHousehold = householdService.getHouseholdWithResidents(household.getHouseholdId());
            
            List<Resident> residents = new ArrayList<>();
            if (fullHousehold != null && fullHousehold.getResidents() != null) {
                residents = fullHousehold.getResidents();
            }
            
            ObservableList<Resident> residentList = FXCollections.observableArrayList(residents);
            tblResidents.setItems(residentList);
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Không thể tải danh sách nhân khẩu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleViewHistory() {
        try {
            new ChangeHistoryHandler(this.stage, household.getHouseholdId());
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Không thể mở lịch sử thay đổi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
} 