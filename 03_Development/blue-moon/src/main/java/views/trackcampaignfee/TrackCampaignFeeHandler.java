package views.trackcampaignfee;

import views.BaseScreenWithLogoutAndGoBackHandler;
import views.messages.ErrorDialog;
import models.CampaignFee;
import models.Fee;
import services.TrackCampaignFeeService;
import dto.campaignfee.PaidFeeResponseDTO;
import java.sql.SQLException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TrackCampaignFeeHandler extends BaseScreenWithLogoutAndGoBackHandler {
	@FXML
    private Label lblCampaignFeeName;

    @FXML
    private Label lblUserName;
    
    @FXML
    private Label lblExpectedAmount;

    @FXML
    private Label lblPaidAmount;
    
    @FXML
    private VBox vbFeesList;

    private CampaignFee campaignFee;
    private final TrackCampaignFeeService service = new TrackCampaignFeeService();
	
	public TrackCampaignFeeHandler(Stage stage, String userName, CampaignFee campaignFee) throws Exception {
		super(stage, utils.Configs.TRACK_CAMPAIGN_FEE_SCREEN, utils.Configs.LOGO_PATH, "Thống kê đợt thu phí");
		loader.setController(this);
		this.campaignFee = campaignFee;
		this.setContent();
		this.setScene();
		lblUserName.setText(userName);
		lblCampaignFeeName.setText(campaignFee.getName());
	}
	
	@FXML
	public void initialize() {
		super.initialize();
		try {
			if (!service.isCampaignFeeAssigned(campaignFee.getId())) {
				lblExpectedAmount.setText("0 đồng");
				lblPaidAmount.setText("0 đồng");
				Label noti = new Label("Các khoản thu chưa được gán cho bất kỳ hộ dân nào. Hãy gán các khoản thu trước.");
				noti.setPrefWidth(1019);
				noti.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
				vbFeesList.getChildren().add(noti);
			} else {
				lblExpectedAmount.setText(utils.Utils.formatCurrency(service.getExpectedAmount(campaignFee.getId())) + " đồng");
				lblPaidAmount.setText(utils.Utils.formatCurrency(service.getPaidAmount(campaignFee.getId())) + " đồng");
				
				List<Fee> fees = campaignFee.getFees();
			    
				for (Fee fee : fees) {
					PaidFeeResponseDTO paidInfo = service.getExpectedAndPaidAmount(campaignFee.getId(), fee.getId());
			        
					Label lblFeeName = new Label(fee.getName());
			        lblFeeName.setPrefWidth(250);
			        lblFeeName.setMaxWidth(250);
			        lblFeeName.setStyle("-fx-font-size: 18px;");
			        lblFeeName.setPadding(new Insets(0, 0, 0, 10));
			        
			        Label isMandatory = new Label(paidInfo.isMandatory() ? "✅" : "❌");
			        isMandatory.setPrefWidth(200);
			        isMandatory.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
			        
			        Label lblExpectedAmount = new Label();
			        lblExpectedAmount.setPrefWidth(190);
			        lblExpectedAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
			        lblExpectedAmount.setText(utils.Utils.formatCurrency(paidInfo.getExpectedAmount()));
			        
			        Label lblPaidAmount = new Label();
			        lblPaidAmount.setPrefWidth(190);
			        lblPaidAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
			        lblPaidAmount.setText(utils.Utils.formatCurrency(paidInfo.getPaidAmount()));
			        
			        Label lblInSufficientAmount = new Label();
			        lblInSufficientAmount.setPrefWidth(190);
			        lblInSufficientAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
			        if (paidInfo.isEnough()) {
			        	lblInSufficientAmount.setText("Đã thu đủ");
			        } else {
			        	lblInSufficientAmount.setText(utils.Utils.formatCurrency(paidInfo.remainingAmount()));
			        }
			        
			        HBox row = new HBox(lblFeeName, isMandatory, lblExpectedAmount, lblPaidAmount, lblInSufficientAmount);
			        row.setStyle("-fx-background-color: #F8F8F8; -fx-background-radius: 5px;");
			        row.setAlignment(Pos.CENTER_LEFT);
			        row.setPadding(new Insets(10, 0, 10, 0));
			        vbFeesList.getChildren().add(row);
			    }
			}
		} catch (SQLException e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể truy cập vào CSDL!");
			e.printStackTrace();
		}
	}
}
