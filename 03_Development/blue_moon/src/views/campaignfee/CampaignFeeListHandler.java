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
		super(stage, utils.Configs.CAMPAIGN_FEE_LIST_PATH, utils.Configs.LOGO_PATH, "Danh sách đợt thu");
		loader.setController(this);
		this.setContent();
		this.setScene();
		this.lblUserName.setText(userName);
		
		// Store a reference to this handler in the scene userData
        if (this.scene != null) {
            this.scene.setUserData(this);
        }
	}
	
	@FXML
	public void initialize() {
		super.initialize();
		loadCampaignFeeList();
		btnAddCampaignFee.setOnAction(e -> handleAddCampaignFee());
	}
	
	public void loadCampaignFeeList() {
		List<CampaignFee> campaignFees = service.getAllCampaignFees();
		
		if (vbCampaignFeeList != null) {
			vbCampaignFeeList.getChildren().clear();
			for (CampaignFee campaignFee : campaignFees) {
				CampaignFeeCell cell = new CampaignFeeCell(this.stage, campaignFee, service, this);
				vbCampaignFeeList.getChildren().add(cell);
				vbCampaignFeeList.setSpacing(20);
			}
		}
	}
	
	public static CampaignFeeListHandler getHandlerFromStage(Stage stage) {
		if (stage != null && stage.getScene() != null && stage.getScene().getUserData() instanceof CampaignFeeListHandler) {
			return (CampaignFeeListHandler) stage.getScene().getUserData();
		}
		return null;
	}
	
	private void handleAddCampaignFee() {
		try {
			Stage popupStage = new Stage();
			NewCampaignFeeHandler newCampaignFeeHandler = new NewCampaignFeeHandler(this.stage);
			
			// Get reference to the popup stage that was created in the handler
			popupStage = newCampaignFeeHandler.getStage();
			
			// Add a listener to refresh the list when the form is closed
			popupStage.setOnHiding(e -> loadCampaignFeeList());
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể hiển thị form điền thông tin đợt thu mới");
			e.printStackTrace();
		}
	}
}
