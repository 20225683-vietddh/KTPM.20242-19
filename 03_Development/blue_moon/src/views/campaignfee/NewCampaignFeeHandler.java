package views.campaignfee;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.effect.GaussianBlur;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;
import views.BaseScreenHandler;
import views.messages.*;
import views.ScreenNavigator;
import services.FeeService;
import models.Fee;
import dto.campaignfee.NewCampaignFeeDTO;
import controllers.AddCampaignFeeController;
import exception.*;
import java.time.DateTimeException;
import java.sql.SQLException;

public class NewCampaignFeeHandler extends BaseScreenHandler {
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
	private TextArea taDescription;

	@FXML
	private TextField tfName;

	@FXML
	private VBox vbFeesList;
	
	private final FeeService service = new FeeService();
	private List<Fee> allFees = service.getAllFees();
	private List<FeeCell> feeCells = new ArrayList<>();
	
	public NewCampaignFeeHandler(Stage ownerStage) throws Exception {
        super(new Stage(), utils.Configs.NEW_CAMPAIGN_FORM, utils.Configs.LOGO_PATH, "Thêm khoản thu mới");
        loader.setController(this);
        this.setContent();  
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
		btnDelete.setOnAction(e -> handleClose());
		btnSave.setOnAction(e -> handleSave());
		cbStartDay.getItems().addAll(utils.Configs.DAY);
		cbStartMonth.getItems().addAll(utils.Configs.MONTH);
		cbStartYear.getItems().addAll(utils.Configs.YEAR);
		cbDueDay.getItems().addAll(utils.Configs.DAY);
		cbDueMonth.getItems().addAll(utils.Configs.MONTH);
		cbDueYear.getItems().addAll(utils.Configs.YEAR);
		FeeCell firstCell = new FeeCell(allFees, this::handleAddNewFee);
		vbFeesList.getChildren().add(firstCell.getContainer());
		feeCells.add(firstCell);
	}
	
	private void handleClose() {
		 Stage stage = (Stage) btnDelete.getScene().getWindow();
		 stage.close();
	}
	
	private void handleAddNewFee() {
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
	
	private void handleSave() {
		NewCampaignFeeDTO requestDTO = getUserInputs();
		
		try {
			this.controller = new AddCampaignFeeController();
			((AddCampaignFeeController) controller).handleAddCampaignFee(requestDTO);
			Stage stage = (Stage) btnSave.getScene().getWindow(); 
			stage.close();
			InformationDialog.showNotification("Thêm vào CSDL thành công",  "Chúc mừng bạn! Đợt thu phí " + requestDTO.getName() + " đã được thêm thành công!");
		    ScreenNavigator.goBack();
		} catch (InvalidInputException e) {
			ErrorDialog.showError("Lỗi nhập liệu", e.getMessage());
		} catch (DateTimeException e) {
			ErrorDialog.showError("Lỗi ngày tháng", e.getMessage());
		} catch (InvalidDateRangeException e) {
			ErrorDialog.showError("Lỗi ngày tháng", e.getMessage());
		} catch (SQLException e) {
			ErrorDialog.showError("Lỗi hệ thống", e.getMessage());
		}
	}
	
	private NewCampaignFeeDTO getUserInputs() {
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
		return new NewCampaignFeeDTO(name, sDay, sMonth, sYear, dDay, dMonth, dYear, description, selectedFeeIds);
	}
}
