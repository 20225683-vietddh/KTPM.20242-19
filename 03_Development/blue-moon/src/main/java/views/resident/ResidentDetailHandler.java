package views.resident;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import models.Resident;
import models.Household;
import services.HouseholdService;
import views.BaseScreenHandler;
import views.messages.ErrorDialog;
import java.time.format.DateTimeFormatter;

public class ResidentDetailHandler extends BaseScreenHandler {
    @FXML private Label lblFullName;
    @FXML private Label lblDateOfBirth;
    @FXML private Label lblGender;
    @FXML private Label lblEthnicity;
    @FXML private Label lblReligion;
    @FXML private Label lblCitizenId;
    @FXML private Label lblDateOfIssue;
    @FXML private Label lblPlaceOfIssue;
    @FXML private Label lblOccupation;
    @FXML private Label lblRelationshipWithHead;
    @FXML private Label lblHouseholdInfo;
    @FXML private Label lblNotes;
    @FXML private Label lblAddedDate;
    @FXML private Button btnClose;
    
    private final Stage ownerStage;
    private Resident resident;
    private HouseholdService householdService;
    
    public ResidentDetailHandler(Stage ownerStage, Resident resident) throws Exception {
        super(new Stage(), utils.Configs.RESIDENT_DETAIL_POPUP, utils.Configs.LOGO_PATH, "Chi tiết nhân khẩu");
        loader.setController(this);
        this.ownerStage = ownerStage;
        this.resident = resident;
        this.householdService = new HouseholdService();
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
        populateResidentDetails();
        btnClose.setOnAction(e -> handleClose());
    }
    
    private void populateResidentDetails() {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            lblFullName.setText(resident.getFullName() != null ? resident.getFullName() : "N/A");
            lblDateOfBirth.setText(resident.getDateOfBirth() != null ? 
                resident.getDateOfBirth().format(dateFormatter) : "N/A");
            lblGender.setText(resident.getGender() != null ? resident.getGender() : "N/A");
            lblEthnicity.setText(resident.getEthnicity() != null ? resident.getEthnicity() : "N/A");
            lblReligion.setText(resident.getReligion() != null ? resident.getReligion() : "N/A");
            lblCitizenId.setText(resident.getCitizenId() != null ? resident.getCitizenId() : "N/A");
            lblDateOfIssue.setText(resident.getDateOfIssue() != null ? 
                resident.getDateOfIssue().format(dateFormatter) : "N/A");
            lblPlaceOfIssue.setText(resident.getPlaceOfIssue() != null ? resident.getPlaceOfIssue() : "N/A");
            lblOccupation.setText(resident.getOccupation() != null ? resident.getOccupation() : "N/A");
            lblRelationshipWithHead.setText(resident.getRelationshipWithHead() != null ? 
                resident.getRelationshipWithHead() : "N/A");
            lblNotes.setText(resident.getNotes() != null && !resident.getNotes().trim().isEmpty() ? 
                resident.getNotes() : "Không có ghi chú");
            lblAddedDate.setText(resident.getAddedDate() != null ? 
                resident.getAddedDate().format(dateFormatter) : "N/A");
            
            // Get household information
            if (resident.getHouseholdId() > 0) {
                Household household = householdService.getHouseholdById(resident.getHouseholdId());
                if (household != null) {
                    String householdInfo = "Hộ " + household.getHouseholdId() + " - " + household.getHouseNumber() +
                        ", " + household.getStreet() + ", " + household.getWard() + ", " + household.getDistrict();
                    lblHouseholdInfo.setText(householdInfo);
                } else {
                    lblHouseholdInfo.setText("Hộ khẩu ID: " + resident.getHouseholdId() + " (Không tìm thấy thông tin)");
                }
            } else {
                lblHouseholdInfo.setText("Chưa xác định");
            }
            
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Không thể tải thông tin chi tiết: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
} 