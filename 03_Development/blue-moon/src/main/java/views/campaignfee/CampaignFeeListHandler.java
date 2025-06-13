package views.campaignfee;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.List;
import java.util.stream.Collectors;
import views.BaseScreenWithLogoutAndGoBackHandler;
import views.messages.ErrorDialog;
import models.CampaignFee;
import services.CampaignFeeService;

public class CampaignFeeListHandler extends BaseScreenWithLogoutAndGoBackHandler {
	@FXML private Label lblUserName;
	@FXML private VBox vbCampaignFeeList;
	@FXML private Button btnAddCampaignFee;
	@FXML private TextField tfSearch;
	private final CampaignFeeService service = new CampaignFeeService();
	
	public CampaignFeeListHandler(Stage stage, String userName) throws Exception {
		super(stage, utils.Configs.CAMPAIGN_FEE_LIST_PATH, utils.Configs.LOGO_PATH, "Danh sách đợt thu");
		loader.setController(this);
		this.setContent();
		this.setScene();
		this.lblUserName.setText(userName);
        if (this.scene != null) {
            this.scene.setUserData(this);
        }
	}
	
	@FXML
	public void initialize() {
		super.initialize();	
        loadCampaignFeeList("");
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            loadCampaignFeeList(newValue);
        });
		btnAddCampaignFee.setOnAction(e -> handleAddCampaignFee());
	}
	
	public void loadCampaignFeeList(String searchTerm) {
		try {
			List<CampaignFee> campaignFees = service.getAllCampaignFees();
			
			if (searchTerm != null && !searchTerm.isBlank()) {
	            String lowerSearchTerm = searchTerm.toLowerCase();
	            campaignFees = campaignFees.stream()
	                .filter(campaignFee -> campaignFee.getName().toLowerCase().contains(lowerSearchTerm))
	                .collect(Collectors.toList());
	        }
			
			if (vbCampaignFeeList != null) {
				vbCampaignFeeList.getChildren().clear();
				for (CampaignFee campaignFee : campaignFees) {
					CampaignFeeCell cell = new CampaignFeeCell(this.stage, campaignFee, service, this);
					vbCampaignFeeList.getChildren().add(cell);
					vbCampaignFeeList.setSpacing(20);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi", "Rất tiếc, đã có lỗi xảy ra!");
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
			
			popupStage = newCampaignFeeHandler.getStage();
			
			popupStage.setOnHiding(e -> loadCampaignFeeList(""));
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể hiển thị form điền thông tin đợt thu mới");
			e.printStackTrace();
		}
	}
}
