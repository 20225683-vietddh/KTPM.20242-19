package views.homepage;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import views.BaseScreenHandler;
import views.fee.FeeListPageHandler;
import views.campaignfee.CampaignFeeListHandler;
import views.messages.*;
import views.chargefee.ChargeFeeHandler;
import views.trackcampaignfee.TrackCampaignFeeHandler;
import models.CampaignFee;
import services.AccountantDashboardService;
import javafx.scene.control.TableColumn;
import java.util.Map;

public class AccountantHomePageHandler extends HomePageHandler {
	@FXML private Button btnViewCampaignFees;
	@FXML private Button btnViewFees;
	@FXML private Button btnViewHouseHolds;
	@FXML private Button btnTrackCampaignFee;
	
	public AccountantHomePageHandler(Stage stage) throws Exception {
		super(stage, utils.Configs.ACCOUNTANT_HOME_PAGE_PATH);
		this.dashboardService = new AccountantDashboardService();
		loader.setController(this);
		this.setContent();
		this.setScene();
	}

	public AccountantHomePageHandler(Stage stage, String userName) throws Exception {
		super(stage, utils.Configs.ACCOUNTANT_HOME_PAGE_PATH);
		this.dashboardService = new AccountantDashboardService();
		loader.setController(this);
		this.setContent();
		this.setScene();
		super.lblUserName.setText(userName);
	}
	
	@Override
	@FXML
	public void initialize() {
		super.initialize();
		btnViewCampaignFees.setOnAction(e -> handleViewCampaignFees());
		btnViewFees.setOnAction(e -> handleViewFees());
		btnTrackCampaignFee.setOnAction(e -> handleTrackCampaignFee());
		btnViewHouseHolds.setOnAction(e -> handleChargeFee());
	}
	
	@Override
	protected void loadDashboardData() {
		super.loadDashboardData(); 
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

	private void handleViewFees() {
		try {
			System.out.println("Gọi khoản thu");
			FeeListPageHandler feeListHandler = new FeeListPageHandler(this.stage, lblUserName.getText());
			feeListHandler.show();
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang danh sách khoản thu!");
			e.printStackTrace();
		}
	}

	private void handleTrackCampaignFee() {
		try {
			CampaignFeeChosenOption option = new CampaignFeeChosenOption();
			option.show();
			
			CampaignFee selected = option.getSelectedOption();
			
			if (selected == null) {
				ErrorDialog.showError("Lỗi", "Bạn chưa chọn đợt thu phí nào!");
			} else {
				BaseScreenHandler handler = new TrackCampaignFeeHandler(this.stage, lblUserName.getText(), selected);
				handler.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang thống kê!");
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
				handler.show();
			}
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể tải danh sách!");
			e.printStackTrace();
		}
	}
}
