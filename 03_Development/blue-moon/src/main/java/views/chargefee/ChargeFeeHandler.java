package views.chargefee;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import views.BaseScreenWithLogoutAndGoBackHandler;
import models.CampaignFee;
import models.Household;
import services.ChargeFeeService;

public class ChargeFeeHandler extends BaseScreenWithLogoutAndGoBackHandler {
	@FXML private Label lblUserName;
	@FXML private Label lblCampaignFeeName;
	@FXML private VBox vbHouseholdsList;
	private CampaignFee campaignFee;
	private final ChargeFeeService service = new ChargeFeeService();
	
	public ChargeFeeHandler(Stage stage, String userName, CampaignFee campaignFee) throws Exception {
		super(stage, utils.Configs.CHARGE_FEE_SCREEN, utils.Configs.LOGO_PATH, "Thu phí");
		loader.setController(this);
		this.campaignFee = campaignFee;
		this.setContent();
		this.setScene();
		this.lblUserName.setText(userName);
	}
	
	@FXML
	public void initialize() {
		super.initialize();
		lblCampaignFeeName.setText(campaignFee.getName());
		loadHouseholdsList();
	}
	
	private void loadHouseholdsList() {
		List<Household> households = service.getAllHouseholds();
		
		if (vbHouseholdsList != null) {
			vbHouseholdsList.getChildren().clear();
			for (Household household : households) {
				HouseholdCell cell = new HouseholdCell(this.stage, campaignFee, household, service, this::loadHouseholdsList);
				vbHouseholdsList.getChildren().add(cell);
				vbHouseholdsList.setSpacing(20);
			}
		}
	}
}
