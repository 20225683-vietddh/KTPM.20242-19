package views.trackcampaignfee;

import views.BaseScreenHandler;
import views.messages.ErrorDialog;
import models.CampaignFee;
import models.Fee;
import services.TrackCampaignFeeService;
import dto.campaignfee.HouseholdFeeDetailDTO;
import java.sql.SQLException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

public class CompulsoryFeeDetailHandler extends BaseScreenHandler {
    @FXML private Button btnClose;
    @FXML private Label lblCampaignFeeName;
    @FXML private Label lblFeeName;
    @FXML private Label lblTotalExpected;
    @FXML private Label lblTotalPaid;
    @FXML private Label lblTotalRemaining;
    @FXML private VBox vbHouseholdList;
    
    private CampaignFee campaignFee;
    private Fee fee;
    private final TrackCampaignFeeService service = new TrackCampaignFeeService();
    
    public CompulsoryFeeDetailHandler(Stage ownerStage, CampaignFee campaignFee, Fee fee) throws Exception {
        super(new Stage(), utils.Configs.COMPULSORY_FEE_DETAIL_POPUP, utils.Configs.LOGO_PATH, "Chi tiết khoản thu bắt buộc");
        loader.setController(this);
        this.campaignFee = campaignFee;
        this.fee = fee;
        this.setContent();  
        this.setScene();    

        // Apply blur effect to parent stage
        Parent parentRoot = ownerStage.getScene().getRoot();
        GaussianBlur blur = new GaussianBlur(10);
        parentRoot.setEffect(blur); 

        // Remove blur effect when popup closes
        this.stage.setOnHidden(e -> parentRoot.setEffect(null));
        this.showPopup(ownerStage);
    }
    
    @FXML
    public void initialize() {
        lblCampaignFeeName.setText(campaignFee.getName());
        lblFeeName.setText(fee.getName());
        loadHouseholdDetails();
        btnClose.setOnAction(e -> handleClose());
    }
    
    private void loadHouseholdDetails() {
        try {
            vbHouseholdList.getChildren().clear();
            
            List<HouseholdFeeDetailDTO> householdDetails = service.getHouseholdDetailsByFee(campaignFee.getId(), fee.getId());
            
            if (householdDetails.isEmpty()) {
                Label lblEmpty = new Label("Không có dữ liệu nào cho khoản thu này!");
                lblEmpty.setPrefWidth(800);
                lblEmpty.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
                vbHouseholdList.getChildren().add(lblEmpty);
                
                lblTotalExpected.setText("0 đồng");
                lblTotalPaid.setText("0 đồng");
                lblTotalRemaining.setText("0 đồng");
            } else {
                // Calculate totals
                int totalExpected = 0;
                int totalPaid = 0;
                
                for (HouseholdFeeDetailDTO household : householdDetails) {
                    totalExpected += household.getExpectedAmount();
                    totalPaid += household.getPaidAmount();
                    
                    createHouseholdRow(household);
                }
                
                int totalRemaining = totalExpected - totalPaid;
                
                // Update summary labels
                lblTotalExpected.setText(utils.Utils.formatCurrency(totalExpected) + " đồng");
                lblTotalPaid.setText(utils.Utils.formatCurrency(totalPaid) + " đồng");
                lblTotalRemaining.setText(utils.Utils.formatCurrency(totalRemaining) + " đồng");
            }
            
        } catch (SQLException e) {
            ErrorDialog.showError("Lỗi hệ thống", "Không thể truy cập vào CSDL!");
            e.printStackTrace();
        }
    }
    
    private void createHouseholdRow(HouseholdFeeDetailDTO household) {
        // House number
        Label lblHouseNumber = new Label("Hộ " + household.getHouseNumber());
        lblHouseNumber.setPrefWidth(255);
        lblHouseNumber.setMaxWidth(255);
        lblHouseNumber.setStyle("-fx-font-size: 16px;");
        lblHouseNumber.setPadding(new Insets(0, 0, 0, 10));
        
        // Expected amount
        Label lblExpected = new Label();
        lblExpected.setPrefWidth(160);
        lblExpected.setMaxWidth(160);
        lblExpected.setStyle("-fx-font-size: 16px; -fx-alignment: center;");
        lblExpected.setText(utils.Utils.formatCurrency(household.getExpectedAmount()));
        
        // Paid amount
        Label lblPaid = new Label();
        lblPaid.setPrefWidth(160);
        lblPaid.setMaxWidth(160);
        lblPaid.setStyle("-fx-font-size: 16px; -fx-alignment: center;");
        lblPaid.setText(utils.Utils.formatCurrency(household.getPaidAmount()));
        
        // Remaining amount or status
        Label lblRemaining = new Label();
        lblRemaining.setPrefWidth(160);
        lblRemaining.setMaxWidth(160);
        lblRemaining.setStyle("-fx-font-size: 16px; -fx-alignment: center;");
        
        if (household.isFullyPaid()) {
            lblRemaining.setText("Đã thu đủ");
            lblRemaining.setStyle(lblRemaining.getStyle() + " -fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            lblRemaining.setText(utils.Utils.formatCurrency(household.getRemainingAmount()));
            if (household.getRemainingAmount() > 0) {
                lblRemaining.setStyle(lblRemaining.getStyle() + " -fx-text-fill: red;");
            }
        }
        
        // Status indicator
        Label lblStatus = new Label();
        lblStatus.setPrefWidth(100);
        lblStatus.setMaxWidth(100);
        lblStatus.setStyle("-fx-font-size: 14px; -fx-alignment: center; -fx-font-weight: bold;");
        
        if (household.isFullyPaid()) {
            lblStatus.setText("✓ Hoàn thành");
            lblStatus.setStyle(lblStatus.getStyle() + " -fx-text-fill: green;");
        } else if (household.getPaidAmount() > 0) {
            lblStatus.setText("◐ Chưa đủ");
            lblStatus.setStyle(lblStatus.getStyle() + " -fx-text-fill: orange;");
        } else {
            lblStatus.setText("✗ Chưa nộp");
            lblStatus.setStyle(lblStatus.getStyle() + " -fx-text-fill: red;");
        }
        
        HBox row = new HBox(lblHouseNumber, lblExpected, lblPaid, lblRemaining, lblStatus);
        row.setStyle("-fx-background-color: #F8F8F8; -fx-background-radius: 5px;");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));
        HBox.setMargin(row, new Insets(2, 0, 2, 0));
        
        vbHouseholdList.getChildren().add(row);
    }
    
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
} 