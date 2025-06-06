package views.trackcampaignfee;

import views.BaseScreenWithLogoutAndGoBackHandler;
import views.messages.ErrorDialog;
import models.CampaignFee;
import models.Fee;
import services.TrackCampaignFeeService;
import dto.campaignfee.PaidFeeResponseDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

public class TrackCampaignFeeHandler extends BaseScreenWithLogoutAndGoBackHandler {
	 @FXML private Button btnExportReport;
	 @FXML private Label lblCampaignFeeName;
	 @FXML private Label lblTotalCompulsoryPaidAmount;
     @FXML private Label lblTotalExpectedAmount;
     @FXML private Label lblTotalOptionalPaidAmount;
     @FXML private Label lblUserName;
	 @FXML private VBox vbCompulsoryFees;
	 @FXML private VBox vbOptionalFees;
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
		setupCompulsoryFeesBox();
		setupOptionalFeesBox();
	}
	
	private void setupCompulsoryFeesBox() {
		try {
			if (!service.isCampaignFeeAssigned(campaignFee.getId())) {
				lblTotalExpectedAmount.setText("0 đồng");
				lblTotalCompulsoryPaidAmount.setText("0 đồng");
				Label noti = new Label("Đợt thu phí này chưa có khoản thu nào!");
				noti.setPrefWidth(1019);
				noti.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
				vbCompulsoryFees.getChildren().add(noti);
			} else {
				lblTotalExpectedAmount.setText(utils.Utils.formatCurrency(service.getExpectedAmount(campaignFee.getId())) + " đồng");
				lblTotalCompulsoryPaidAmount.setText(utils.Utils.formatCurrency(service.getTotalCompulsoryPaidAmount(campaignFee.getId())) + " đồng");
				
				List<Fee> compulsoryFees = this.getCompulsoryFees();
			    
				for (Fee fee : compulsoryFees) {
					PaidFeeResponseDTO paidInfo = service.getExpectedAndPaidAmount(campaignFee.getId(), fee.getId());
			        
					Label lblFeeName = new Label(fee.getName());
			        lblFeeName.setPrefWidth(445);
			        lblFeeName.setMaxWidth(445);
			        lblFeeName.setStyle("-fx-font-size: 18px;");
			        lblFeeName.setPadding(new Insets(0, 0, 0, 10));
			        
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
			        
			        HBox row = new HBox(lblFeeName, lblExpectedAmount, lblPaidAmount, lblInSufficientAmount);
			        row.setStyle("-fx-background-color: #F8F8F8; -fx-background-radius: 5px;");
			        row.setAlignment(Pos.CENTER_LEFT);
			        row.setPadding(new Insets(10, 0, 10, 0));
			        vbCompulsoryFees.getChildren().add(row);
			    }
			}
		} catch (SQLException e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể truy cập vào CSDL!");
			e.printStackTrace();
		}
	}
	
	private void setupOptionalFeesBox() {
		try {
			if (!service.isCampaignFeeAssigned(campaignFee.getId())) {
				lblTotalOptionalPaidAmount.setText("0 đồng");
				Label noti = new Label("Đợt thu phí này chưa có khoản thu tự nguyện nào!");
				noti.setPrefWidth(1019);
				noti.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
				vbCompulsoryFees.getChildren().add(noti);
			} else {
				lblTotalExpectedAmount.setText(utils.Utils.formatCurrency(service.getExpectedAmount(campaignFee.getId())) + " đồng");
				lblTotalOptionalPaidAmount.setText(utils.Utils.formatCurrency(service.getTotalOptionalPaidAmount(campaignFee.getId())) + " đồng");
				
				List<Fee> optionalFees = this.getOptionalFees();
			    
				for (Fee fee : optionalFees) {
					PaidFeeResponseDTO paidInfo = service.getExpectedAndPaidAmount(campaignFee.getId(), fee.getId());
			        
					Label lblFeeName = new Label(fee.getName());
			        lblFeeName.setPrefWidth(700);
			        lblFeeName.setMaxWidth(700);
			        lblFeeName.setStyle("-fx-font-size: 18px;");
			        lblFeeName.setPadding(new Insets(0, 0, 0, 10));
			        
			        Label lblPaidAmount = new Label();
			        lblPaidAmount.setPrefWidth(315);
			        lblPaidAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
			        lblPaidAmount.setText(utils.Utils.formatCurrency(paidInfo.getPaidAmount()));
			        
			        HBox row = new HBox(lblFeeName, lblPaidAmount);
			        row.setStyle("-fx-background-color: #F8F8F8; -fx-background-radius: 5px;");
			        row.setAlignment(Pos.CENTER_LEFT);
			        row.setPadding(new Insets(10, 0, 10, 0));
			        vbOptionalFees.getChildren().add(row);
			    }
			}
		} catch (SQLException e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể truy cập vào CSDL!");
			e.printStackTrace();
		}
	}
	
	private List<Fee> getOptionalFees() {
		List<Fee> fees = campaignFee.getFees();
		List<Fee> optionalFees = new ArrayList<>();
		for (Fee fee : fees) {
			if (!fee.getIsMandatory()) {
				optionalFees.add(fee);
			}
		}
		return optionalFees;
	}
	
	private List<Fee> getCompulsoryFees() {
		List<Fee> fees = campaignFee.getFees();
		List<Fee> compulsoryFees = new ArrayList<>();	
		for (Fee fee : fees) {
			if (fee.getIsMandatory()) {
				compulsoryFees.add(fee);
			}
		}
	    return compulsoryFees;
	}
}
