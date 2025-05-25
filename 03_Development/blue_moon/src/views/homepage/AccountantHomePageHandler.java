package views.homepage;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import views.BaseScreenHandler;
import views.campaignfee.CampaignFeeListHandler;
import views.messages.*;
import views.chargefee.ChargeFeeHandler;
import models.CampaignFee;

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
		btnViewHouseHolds.setOnAction(e -> handleChargeFee());
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
	
	private void handleChargeFee() {
		try {
			CampaignFeeChosenOption option = new CampaignFeeChosenOption();
			option.show();
			
			CampaignFee selected = option.getSelectedOption();
			
			if (selected == null) {
				ErrorDialog.showError("Lỗi", "Bạn chưa chọn đợt thu phí nào!");
			} else {
				BaseScreenHandler handler = new ChargeFeeHandler(this.stage, lblUserName.getText(), selected);
				System.out.println(selected.getId() + selected.getName() + selected.getFees());
				handler.show();
			}
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể tải danh sách!");
			e.printStackTrace();
		}
	}
}
