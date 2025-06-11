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
		btnExportReport.setOnAction(e -> handleExportReport());
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
			        lblFeeName.setPrefWidth(320);
			        lblFeeName.setMaxWidth(320);
			        lblFeeName.setStyle("-fx-font-size: 18px;");
			        lblFeeName.setPadding(new Insets(0, 0, 0, 10));
			        
			        Label lblExpectedAmount = new Label();
			        lblExpectedAmount.setPrefWidth(180);
			        lblExpectedAmount.setMaxWidth(180);
			        lblExpectedAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
			        lblExpectedAmount.setText(utils.Utils.formatCurrency(paidInfo.getExpectedAmount()));
			        
			        Label lblPaidAmount = new Label();
			        lblPaidAmount.setPrefWidth(180);
			        lblPaidAmount.setMaxWidth(180);
			        lblPaidAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
			        lblPaidAmount.setText(utils.Utils.formatCurrency(paidInfo.getPaidAmount()));
			        
			        Label lblInSufficientAmount = new Label();
			        lblInSufficientAmount.setPrefWidth(180);
			        lblInSufficientAmount.setMaxWidth(180);
			        lblInSufficientAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
			        if (paidInfo.isEnough()) {
			        	lblInSufficientAmount.setText("Đã thu đủ");
			        } else {
			        	lblInSufficientAmount.setText(utils.Utils.formatCurrency(paidInfo.remainingAmount()));
			        }
			        
			        Button btnViewDetailed = new Button("Chi tiết");
			        btnViewDetailed.setPrefWidth(125);
			        btnViewDetailed.setMaxWidth(125);
			        btnViewDetailed.setStyle("-fx-cursor:hand; -fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 18px;");
			        HBox.setMargin(btnViewDetailed, new Insets(0, 15, 0, 15));
			        btnViewDetailed.setOnAction(e -> handleViewDetailedCompulsoryFee(fee));
			        
			        HBox row = new HBox(lblFeeName, lblExpectedAmount, lblPaidAmount, lblInSufficientAmount, btnViewDetailed);
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
			        lblFeeName.setPrefWidth(500);
			        lblFeeName.setMaxWidth(500);
			        lblFeeName.setStyle("-fx-font-size: 18px;");
			        lblFeeName.setPadding(new Insets(0, 0, 0, 10));
			        
			        Label lblPaidAmount = new Label();
			        lblPaidAmount.setPrefWidth(250);
			        lblPaidAmount.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
			        lblPaidAmount.setText(utils.Utils.formatCurrency(paidInfo.getPaidAmount()));
			        
			        Button btnViewDetailed = new Button("Chi tiết");
			        btnViewDetailed.setPrefWidth(124);
			        btnViewDetailed.setStyle("-fx-cursor:hand; -fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); -fx-text-fill: white; -fx-font-size: 18px;");
			        HBox.setMargin(btnViewDetailed, new Insets(0, 70, 0, 70));
			        btnViewDetailed.setOnAction(e -> handleViewDetailedOptionalFee(fee));
			        
			        HBox row = new HBox(lblFeeName, lblPaidAmount, btnViewDetailed);
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
	
	private void handleExportReport() {
		try {
			utils.ExportCampaignFeeReport exporter = new utils.ExportCampaignFeeReport();
			String filePath = exporter.exportReport(campaignFee, lblUserName.getText());
			
			views.messages.InformationDialog.showNotification("Thành công", "Xuất báo cáo thành công. File được lưu tại: " + filePath);
			
			java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
		} catch (java.sql.SQLException e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể truy cập vào CSDL!");
			e.printStackTrace();
		} catch (java.io.IOException e) {
			ErrorDialog.showError("Lỗi ghi file", "Không thể ghi file báo cáo!");
			e.printStackTrace();
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi", "Có lỗi xảy ra khi xuất báo cáo: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void handleViewDetailedCompulsoryFee(Fee fee) {
		try {
			new CompulsoryFeeDetailHandler(stage, campaignFee, fee);
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi", "Không thể mở popup chi tiết khoản thu bắt buộc: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void handleViewDetailedOptionalFee(Fee fee) {
		try {
			new OptionalFeeDetailHandler(stage, campaignFee, fee);
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi", "Không thể mở popup chi tiết khoản thu tự nguyện: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
