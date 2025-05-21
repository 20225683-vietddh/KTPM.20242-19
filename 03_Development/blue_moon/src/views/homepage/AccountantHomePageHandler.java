package views.homepage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import views.ScreenNavigator;
import views.fee.FeeListPageHandler;
import views.messages.ErrorDialog;


public class AccountantHomePageHandler extends HomePageHandler {
	@FXML private Button btnViewCampaignFees;
	@FXML private Button btnViewFees;
	@FXML private Button btnViewHouseHolds;
	@FXML private Button btnTrackCampaignFee;

	public AccountantHomePageHandler(Stage stage) throws Exception {
		super(stage, utils.Configs.ACCOUNTANT_HOME_PAGE_PATH);
		loader.setController(this);
		this.setContent();
		this.setScene();
	}
	
	@FXML
	public void initialize() {
		super.initialize();
		btnViewCampaignFees.setOnAction(e -> handleViewCampaignFees());
		btnViewFees.setOnAction(e -> handleViewFees());
		btnViewHouseHolds.setOnAction(e -> handleViewHouseHolds());
		btnTrackCampaignFee.setOnAction(e -> handleTrackCampaignFee());
	}

	private void handleViewCampaignFees() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang danh sách đợt thu!");
		}
	}

	private void handleViewFees() {
		try {
			FeeListPageHandler feeListHandler = new FeeListPageHandler(this.stage, utils.Configs.FEE_LIST_PAGE_PATH);
			feeListHandler.show();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang danh sách khoản thu!");
		}
	}

	private void handleViewHouseHolds() {
		try {
		
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang danh sách hộ dân!");
		}
	}

	private void handleTrackCampaignFee() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang thống kê!");
		}
	}
}