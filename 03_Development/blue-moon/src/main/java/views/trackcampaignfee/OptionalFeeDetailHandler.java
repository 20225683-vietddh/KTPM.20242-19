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

public class OptionalFeeDetailHandler extends BaseScreenHandler {
    @FXML private Button btnClose;
    @FXML private Label lblCampaignFeeName;
    @FXML private Label lblFeeName;
    @FXML private Label lblTotalPaid;
    @FXML private Label lblParticipatingHouseholds;
    @FXML private VBox vbHouseholdList;
    
    private CampaignFee campaignFee;
    private Fee fee;
    private final TrackCampaignFeeService service = new TrackCampaignFeeService();
    
    public OptionalFeeDetailHandler(Stage ownerStage, CampaignFee campaignFee, Fee fee) throws Exception {
        super(new Stage(), utils.Configs.OPTIONAL_FEE_DETAIL_POPUP, utils.Configs.LOGO_PATH, "Chi tiết khoản thu tự nguyện");
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
                lblEmpty.setPrefWidth(700);
                lblEmpty.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
                vbHouseholdList.getChildren().add(lblEmpty);
                
                lblTotalPaid.setText("0 đồng");
                lblParticipatingHouseholds.setText("0/0 hộ tham gia");
            } else {
                // Calculate totals
                int totalPaid = 0;
                int participatingCount = 0;
                
                for (HouseholdFeeDetailDTO household : householdDetails) {
                    totalPaid += household.getPaidAmount();
                    if (household.getPaidAmount() > 0) {
                        participatingCount++;
                    }
                    
                    // Only show households that have paid (since it's optional)
                    if (household.getPaidAmount() > 0) {
                        createHouseholdRow(household);
                    }
                }
                
                // Update summary labels
                lblTotalPaid.setText(utils.Utils.formatCurrency(totalPaid) + " đồng");
                lblParticipatingHouseholds.setText(participatingCount + "/" + householdDetails.size() + " hộ tham gia");
                
                // If no households have paid, show appropriate message
                if (participatingCount == 0) {
                    Label lblEmpty = new Label("Chưa có hộ dân nào tham gia khoản thu tự nguyện này!");
                    lblEmpty.setPrefWidth(700);
                    lblEmpty.setStyle("-fx-font-size: 16px; -fx-alignment: center; -fx-text-fill: #666666;");
                    vbHouseholdList.getChildren().add(lblEmpty);
                }
            }
            
        } catch (SQLException e) {
            ErrorDialog.showError("Lỗi hệ thống", "Không thể truy cập vào CSDL!");
            e.printStackTrace();
        }
    }
    
    private void createHouseholdRow(HouseholdFeeDetailDTO household) {
        // House number
        Label lblHouseNumber = new Label("Hộ " + household.getHouseNumber());
        lblHouseNumber.setPrefWidth(250);
        lblHouseNumber.setMaxWidth(250);
        lblHouseNumber.setStyle("-fx-font-size: 16px;");
        lblHouseNumber.setPadding(new Insets(0, 0, 0, 10));
        
        // Paid amount
        Label lblPaid = new Label();
        lblPaid.setPrefWidth(200);
        lblPaid.setMaxWidth(200);
        lblPaid.setStyle("-fx-font-size: 16px; -fx-alignment: center;");
        lblPaid.setText(utils.Utils.formatCurrency(household.getPaidAmount()));
        
        // Contribution status
        Label lblStatus = new Label();
        lblStatus.setPrefWidth(150);
        lblStatus.setMaxWidth(150);
        lblStatus.setStyle("-fx-font-size: 14px; -fx-alignment: center; -fx-font-weight: bold;");
        
        if (household.getPaidAmount() > 0) {
            lblStatus.setText("✓ Đã đóng góp");
            lblStatus.setStyle(lblStatus.getStyle() + " -fx-text-fill: green;");
        } else {
            lblStatus.setText("○ Chưa tham gia");
            lblStatus.setStyle(lblStatus.getStyle() + " -fx-text-fill: #888888;");
        }
        
        // Appreciation level (based on amount contributed)
        Label lblAppreciation = new Label();
        lblAppreciation.setPrefWidth(100);
        lblAppreciation.setMaxWidth(100);
        lblAppreciation.setStyle("-fx-font-size: 12px; -fx-alignment: center;");
        
        int paidAmount = household.getPaidAmount();
        if (paidAmount >= 500000) { // 500k+
            lblAppreciation.setText("🌟 Xuất sắc");
            lblAppreciation.setStyle(lblAppreciation.getStyle() + " -fx-text-fill: gold;");
        } else if (paidAmount >= 200000) { // 200k+
            lblAppreciation.setText("⭐ Tốt");
            lblAppreciation.setStyle(lblAppreciation.getStyle() + " -fx-text-fill: blue;");
        } else if (paidAmount > 0) {
            lblAppreciation.setText("👍 Tham gia");
            lblAppreciation.setStyle(lblAppreciation.getStyle() + " -fx-text-fill: green;");
        }
        
        HBox row = new HBox(lblHouseNumber, lblPaid, lblStatus, lblAppreciation);
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