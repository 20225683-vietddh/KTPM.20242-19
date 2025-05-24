package views.campaignfee;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import java.util.List;
import views.BaseScreenWithLogoutAndGoBackHandler;
import views.messages.ErrorDialog;
import models.CampaignFee;
import services.CampaignFeeService;

public class CampaignFeeListHandler extends BaseScreenWithLogoutAndGoBackHandler {
	@FXML
	private Label lblUserName;
	
	@FXML
	private VBox vbCampaignFeeList;
	
	@FXML 
	private Button btnAddCampaignFee;
	
	private final CampaignFeeService service = new CampaignFeeService();
	
	public CampaignFeeListHandler(Stage stage, String userName) throws Exception {
		super(stage, utils.Configs.CAMPAIGN_FEE_LIST_PATH, utils.Configs.LOGO_PATH, "Danh sách khoản thu");
		loader.setController(this);
		this.setContent();
		this.setScene();
		this.lblUserName.setText(userName);
	}
	
	@FXML
	public void initialize() {
		super.initialize();
		loadCampaignFeeList();
		btnAddCampaignFee.setOnAction(e -> handleAddCampaignFee());
	}
	
	private void loadCampaignFeeList() {
		List<CampaignFee> campaignFees = service.getAllCampaignFees();
		
		if (vbCampaignFeeList != null) {
			vbCampaignFeeList.getChildren().clear();
			for (CampaignFee campaignFee : campaignFees) {
				CampaignFeeCell cell = new CampaignFeeCell(this.stage, campaignFee, service);
				vbCampaignFeeList.getChildren().add(cell);
				vbCampaignFeeList.setSpacing(20);
			}
		}
	}
	
	private void handleAddCampaignFee() {
		try {
			new NewCampaignFeeHandler(this.stage);
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể hiển thị form điền thông tin đợt thu mới");
			e.printStackTrace();
		}
	}
}
