package views.homepage;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import views.BaseScreenHandler;
import views.campaignfee.CampaignFeeListHandler;
import views.messages.ErrorDialog;

public class AccountantHomePageHandler extends HomePageHandler {
	@FXML
	private Button btnViewCampaignFees;
	
	@FXML
	private Button btnViewFees;
	
	@FXML
	private Button btnViewHouseHolds;
	
	@FXML
	private Button btnTrackCampaignFee;
	
	public AccountantHomePageHandler(Stage stage, String userName) throws Exception {
		super(stage, utils.Configs.ACCOUNTANT_HOME_PAGE_PATH);
		loader.setController(this);
		this.setContent();
		this.setScene();
		super.lblUserName.setText(userName);
	}
	
	@FXML
	public void initialize() {
		super.initialize();
		btnViewCampaignFees.setOnAction(e -> handleViewCampaignFees());
	}
	
	private void handleViewCampaignFees() {
		try {
			BaseScreenHandler handler = new CampaignFeeListHandler(this.stage, lblUserName.getText());
			handler.show();
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể tải trang danh sách đợt thu phí!");
			e.printStackTrace();
		}
	}
}
