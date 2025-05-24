package views.campaignfee;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import views.BaseScreenHandler;
import models.CampaignFee;

public class UpdateCampaignFeeHandler extends BaseScreenHandler {
	@FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private ComboBox<String> cbDueDay;

    @FXML
    private ComboBox<String> cbDueMonth;

    @FXML
    private ComboBox<String> cbDueYear;

    @FXML
    private ComboBox<String> cbStartDay;

    @FXML
    private ComboBox<String> cbStartMonth;

    @FXML
    private ComboBox<String> cbStartYear;

    @FXML
    private ComboBox<String> cbStatus;

    @FXML
    private TextArea taDescription;

    @FXML
    private TextField tfName;

    @FXML
    private VBox vbFeesList;
    
    private CampaignFee campaignFee;
    
	public UpdateCampaignFeeHandler(Stage ownerStage, CampaignFee campaignFee) throws Exception {
        super(new Stage(), utils.Configs.UPDATE_CAMPAIGN_FORM, utils.Configs.LOGO_PATH, "Chỉnh sửa thông tin khoản thu");
        loader.setController(this);
        
        this.campaignFee = campaignFee;
        this.setContent();  
        this.setupUI();
        this.setScene();    

        // Apply the blur effect to the parent stage
        Parent parentRoot = ownerStage.getScene().getRoot();
        GaussianBlur blur = new GaussianBlur(10);
        parentRoot.setEffect(blur);

        // Delete the blur effect after closing the pop up stage
        this.stage.setOnHidden(e -> parentRoot.setEffect(null));

        this.showPopup(ownerStage);
    }
	
	@FXML 
	public void initialize() {
		cbStartDay.getItems().addAll(utils.Configs.DAY);
		cbStartMonth.getItems().addAll(utils.Configs.MONTH);
		cbStartYear.getItems().addAll(utils.Configs.YEAR);
		cbDueDay.getItems().addAll(utils.Configs.DAY);
		cbDueMonth.getItems().addAll(utils.Configs.MONTH);
		cbDueYear.getItems().addAll(utils.Configs.YEAR);
		cbStatus.getItems().addAll(utils.Configs.STATUS);
	}
	
	private void setupUI() {
		tfName.setText(campaignFee.getName());
		cbStartDay.setValue(String.valueOf(campaignFee.getStartDate().getDayOfMonth()));
		cbStartMonth.setValue(String.valueOf(campaignFee.getStartDate().getMonthValue()));
		cbStartYear.setValue(String.valueOf(campaignFee.getStartDate().getYear()));
		cbDueDay.setValue(String.valueOf(campaignFee.getDueDate().getDayOfMonth()));
		cbDueMonth.setValue(String.valueOf(campaignFee.getDueDate().getMonthValue()));
		cbDueYear.setValue(String.valueOf(campaignFee.getDueDate().getYear()));
		cbStatus.setValue(campaignFee.getStatus());
		taDescription.setText(campaignFee.getDescription());
	}
}
