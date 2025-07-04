package views.campaignfee;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import dto.campaignfee.CampaignFeeDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Fee;
import services.FeeService;
import views.BaseScreenHandler;
import views.messages.ErrorDialog;

public abstract class CampaignFeeFormHandler extends BaseScreenHandler {
	@FXML protected Button btnDelete;
	@FXML protected Button btnSave;
	@FXML protected ComboBox<String> cbDueDay;
	@FXML protected ComboBox<String> cbDueMonth;
	@FXML protected ComboBox<String> cbDueYear;
	@FXML protected ComboBox<String> cbStartDay;
	@FXML protected ComboBox<String> cbStartMonth;
	@FXML protected ComboBox<String> cbStartYear;
	@FXML protected TextArea taDescription;
	@FXML protected TextField tfName;
	@FXML protected VBox vbFeesList;
	private final FeeService service = new FeeService();
	protected List<Fee> allFees = service.getAllFeesAsModel();
	protected List<FeeCell> feeCells = new ArrayList<>();
	
	public CampaignFeeFormHandler(Stage ownerStage, String path, String title) throws Exception {
        super(new Stage(), path , utils.Configs.LOGO_PATH, title);
    }

	@FXML
	public void initialize() {
		btnDelete.setOnAction(e -> handleClose());
		btnSave.setOnAction(e -> handleSave());
		cbStartDay.getItems().addAll(utils.Configs.DAY);
		cbStartMonth.getItems().addAll(utils.Configs.MONTH);
		cbStartYear.getItems().addAll(utils.Configs.YEAR);
		cbDueDay.getItems().addAll(utils.Configs.DAY);
		cbDueMonth.getItems().addAll(utils.Configs.MONTH);
		cbDueYear.getItems().addAll(utils.Configs.YEAR);
	}
	
	protected void handleClose() {
		 Stage stage = (Stage) btnDelete.getScene().getWindow();
		 stage.close();
	}
	
	protected abstract void handleAddNewFee();
	
	protected void handleDeleteFee(FeeCell cell) {
	    if (feeCells.size() == 1) {
	        ErrorDialog.showError("Lỗi", "Không được xóa. Phải có ít nhất một khoản thu.");
	        return;
	    }
	    vbFeesList.getChildren().remove(cell.getContainer());
	    feeCells.remove(cell);
	}

	protected abstract void handleSave();
	
	protected CampaignFeeDTO getUserInputs() {
		String name = tfName.getText().trim();
		String sDay = cbStartDay.getValue();
		String sMonth = cbStartMonth.getValue();
		String sYear = cbStartYear.getValue();
		String dDay = cbDueDay.getValue();
		String dMonth = cbDueMonth.getValue();
		String dYear = cbDueYear.getValue();
		String description = taDescription.getText().trim();
		List<Integer> selectedFeeIds = feeCells.stream()
		        .map(FeeCell::getSelectedFee)
		        .filter(Objects::nonNull)
		        .map(Fee::getId)
		        .collect(Collectors.toList());
		return new CampaignFeeDTO(name, sDay, sMonth, sYear, dDay, dMonth, dYear, description, selectedFeeIds);
	}

	public Stage getStage() {
		return this.stage;
	}
}