package views.campaignfee;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
	@FXML
	protected Button btnDelete;

	@FXML
	protected Button btnSave;

	@FXML
	protected ComboBox<String> cbDueDay;

	@FXML
	protected ComboBox<String> cbDueMonth;

	@FXML
	protected ComboBox<String> cbDueYear;

	@FXML
	protected ComboBox<String> cbStartDay;

	@FXML
	protected ComboBox<String> cbStartMonth;

	@FXML
	protected ComboBox<String> cbStartYear;

	@FXML
	protected TextArea taDescription;

	@FXML
	protected TextField tfName;

	@FXML
	protected VBox vbFeesList;
	
	private final FeeService service = new FeeService();
	protected List<Fee> allFees = service.getAllFees();
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
	
	protected void handleAddNewFee() {
	    FeeCell lastCell = feeCells.get(feeCells.size() - 1);
	    Fee selected = lastCell.getSelectedFee();

	    if (selected == null) {
	        ErrorDialog.showError("Lỗi", "Vui lòng chọn khoản thu trước!");
	        return;
	    }

	    Set<Fee> selectedFees = feeCells.stream()
	        .map(FeeCell::getSelectedFee)
	        .filter(Objects::nonNull)
	        .collect(Collectors.toSet());

	    List<Fee> remaining = allFees.stream()
	        .filter(f -> !selectedFees.contains(f))
	        .collect(Collectors.toList());

	    if (remaining.isEmpty()) {
	        ErrorDialog.showError("Lỗi", "Không còn khoản thu nào!");
	        return;
	    }

	    FeeCell newRow = new FeeCell(remaining, this::handleAddNewFee);
	    vbFeesList.getChildren().add(newRow.getContainer());
	    feeCells.add(newRow);
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
}
