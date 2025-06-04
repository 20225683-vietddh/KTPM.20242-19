package views.homepage;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import views.BaseScreenWithLogoutAndGoBackHandler;
import services.DashboardService;
import java.util.Map;

public abstract class HomePageHandler extends BaseScreenWithLogoutAndGoBackHandler {
	@FXML protected Label lblUserName;
	@FXML protected Label lblTotalHouseHolds;
	@FXML protected Label lblTotalResidents;
	@FXML protected BarChart<String, Number> bChartCamPaignFees;
	@FXML protected LineChart<String, Number> lChartResidentChange;
	@FXML protected TableView<Map<String, Object>> tblTypicalFees;
	@FXML protected TableView<Map<String, Object>> tblAnnualResidentChange;
	protected DashboardService dashboardService;
	
	public HomePageHandler(Stage stage, String screenPath) throws Exception {
		super(stage, screenPath, utils.Configs.LOGO_PATH, "Trang chủ");
	}
	
	@FXML
	public void initialize() {
		super.initialize();
		initializeCharts();
		initializeTables();
		loadDashboardData();
	}
	
	protected void initializeCharts() {
		bChartCamPaignFees.setTitle("Lịch sử các đợt thu phí");
		lChartResidentChange.setTitle("Lịch sử biến động nhân khẩu");
		bChartCamPaignFees.setLegendVisible(false);
		lChartResidentChange.setLegendVisible(false);
	}
	
	protected void initializeTables() {
		// Initialize typical fees table
		TableColumn<Map<String, Object>, String> feeNameCol = new TableColumn<>("Tên khoản thu");
		feeNameCol.setCellValueFactory(cellData ->
			new javafx.beans.property.SimpleStringProperty(
				String.valueOf(cellData.getValue().get("name"))
			)
		);

		TableColumn<Map<String, Object>, String> feeMandatoryCol = new TableColumn<>("Bắt buộc");
		feeMandatoryCol.setCellValueFactory(cellData ->
			new javafx.beans.property.SimpleStringProperty(
				Boolean.TRUE.equals(cellData.getValue().get("is_mandatory")) ? "Có" : "Không"
			)
		);

		tblTypicalFees.getColumns().clear();
		tblTypicalFees.getColumns().addAll(feeNameCol, feeMandatoryCol);
		tblTypicalFees.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);

		// Initialize resident changes table
		TableColumn<Map<String, Object>, String> changeTypeCol = new TableColumn<>("Loại thay đổi");
		changeTypeCol.setCellValueFactory(cellData ->
			new javafx.beans.property.SimpleStringProperty(
				String.valueOf(cellData.getValue().get("type"))
			)
		);

		TableColumn<Map<String, Object>, String> changeDateCol = new TableColumn<>("Ngày thay đổi");
		changeDateCol.setCellValueFactory(cellData ->
			new javafx.beans.property.SimpleStringProperty(
				String.valueOf(cellData.getValue().get("date"))
			)
		);

		tblAnnualResidentChange.getColumns().clear();
		tblAnnualResidentChange.getColumns().addAll(changeTypeCol, changeDateCol);
		tblAnnualResidentChange.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
	}
	
	protected void loadDashboardData() {
		if (dashboardService == null) return;
		
		// Load total counts
		lblTotalHouseHolds.setText(String.valueOf(dashboardService.getTotalHouseholds()));
		lblTotalResidents.setText(String.valueOf(dashboardService.getTotalResidents()));
		
		// Load charts
		bChartCamPaignFees.getData().clear();
		bChartCamPaignFees.getData().add(dashboardService.getCampaignFeeHistory());
		
		lChartResidentChange.getData().clear();
		lChartResidentChange.getData().add(dashboardService.getResidentChangeHistory());
		
		// Load tables
		ObservableList<Map<String, Object>> typicalFees = FXCollections.observableArrayList(dashboardService.getTypicalFees());
		tblTypicalFees.setItems(typicalFees);
		
		ObservableList<Map<String, Object>> residentChanges = FXCollections.observableArrayList(dashboardService.getRecentResidentChanges());
		tblAnnualResidentChange.setItems(residentChanges);
	}
}
