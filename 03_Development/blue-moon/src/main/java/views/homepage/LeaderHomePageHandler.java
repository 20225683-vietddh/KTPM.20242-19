package views.homepage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import services.LeaderDashboardService;

public class LeaderHomePageHandler extends HomePageHandler {
	@FXML private Button btnManageHouseholds;
    @FXML private Button btnManageResident;
    @FXML private Button btnManageRoom;
    @FXML private Button btnStayAbsence;
    @FXML private Button btnTrackResident;

	public LeaderHomePageHandler(Stage stage, String userName) throws Exception {
		super(stage, utils.Configs.LEADER_HOME_PAGE_PATH);
		this.dashboardService = new LeaderDashboardService();
		loader.setController(this);
		this.setContent();
		this.setScene();
		super.lblUserName.setText(userName);
	}
	
	@Override
	protected void loadDashboardData() {
		super.loadDashboardData(); 
	}

	@FXML
	public void initialize() {
		super.initialize();
		btnManageHouseholds.setOnAction(e -> handleManageHouseholds());
		btnManageResident.setOnAction(e -> handleManageResident());
		btnManageRoom.setOnAction(e -> handleManageRoom());
		btnStayAbsence.setOnAction(e -> handleStayAbsence());
		btnTrackResident.setOnAction(e -> handleTrackResident());
	}
	
	@FXML
	private void handleManageHouseholds() {
		try {
			views.household.HouseholdHandler householdHandler = new views.household.HouseholdHandler(this.stage, this.lblUserName.getText());
			householdHandler.show();
		} catch (Exception e) {
			e.printStackTrace();
			views.messages.ErrorDialog.showError("Lỗi", "Không thể mở màn hình quản lý hộ khẩu!");
		}
	}
	
	@FXML
	private void handleManageResident() {
		try {
			views.resident.ResidentHandler residentHandler = new views.resident.ResidentHandler(this.stage, this.lblUserName.getText());
			residentHandler.show();
		} catch (Exception e) {
			e.printStackTrace();
			views.messages.ErrorDialog.showError("Lỗi", "Không thể mở màn hình quản lý nhân khẩu!");
		}
	}
	
	@FXML
	private void handleManageRoom() {
		try {
			views.room.RoomHandler roomHandler = new views.room.RoomHandler(this.stage, this.lblUserName.getText());
			roomHandler.show();
		} catch (Exception e) {
			e.printStackTrace();
			views.messages.ErrorDialog.showError("Lỗi", "Không thể mở màn hình quản lý phòng!");
		}
	}
	
	@FXML
	private void handleStayAbsence() {
		try {
			// TODO: Implement navigate to temporary stay/absence management screen
			System.out.println("Navigating to temporary stay/absence management...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleTrackResident() {
		try {
			// TODO: Implement navigate to resident statistics screen
			System.out.println("Navigating to resident statistics...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
