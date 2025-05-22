package views.campaignfee;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.List;
import views.BaseScreenWithLogoutAndGoBackHandler;
import models.CampaignFee;
import services.CampaignFeeListService;

public class CampaignFeeListHandler extends BaseScreenWithLogoutAndGoBackHandler {
	@FXML
	private Label lblUserName;
	
	@FXML
	private VBox vbCampaignFeeList;
	
	private final CampaignFeeListService service = new CampaignFeeListService();
	
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
	}
	
	private void loadCampaignFeeList() {
		List<CampaignFee> campaignFees = service.getAllCampaignFees();
		
		if (vbCampaignFeeList != null) {
			vbCampaignFeeList.getChildren().clear();
			for (CampaignFee campaignFee : campaignFees) {
				CampaignFeeCell cell = new CampaignFeeCell(campaignFee);
				vbCampaignFeeList.getChildren().add(cell);
				vbCampaignFeeList.setSpacing(20);
			}
		}
	}
}
