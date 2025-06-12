package views.household;

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
import models.Household;
import services.HouseholdService;

public class HouseholdHandler extends BaseScreenWithLogoutAndGoBackHandler {
	@FXML private Label lblUserName;
	@FXML private VBox vbHouseholdList;
	@FXML private Button btnAddHousehold;
	@FXML private TextField tfSearch;
	private HouseholdService householdService;
	
	public HouseholdHandler(Stage stage, String userName) throws Exception {
		super(stage, utils.Configs.HOUSEHOLD_SCREEN_PATH, utils.Configs.LOGO_PATH, "Danh sách hộ khẩu");
		this.householdService = new HouseholdService();
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
        loadHouseholdList("");
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            loadHouseholdList(newValue);
        });
		btnAddHousehold.setOnAction(e -> handleAddHousehold());
	}
	
	public void loadHouseholdList(String searchTerm) {
		try {
			List<Household> households;
			
			if (searchTerm != null && !searchTerm.isBlank()) {
	            households = householdService.searchHouseholds(searchTerm);
	        } else {
	        	households = householdService.getAllHouseholds();
	        }
			
			if (vbHouseholdList != null) {
				vbHouseholdList.getChildren().clear();
				for (Household household : households) {
					HouseholdCell cell = new HouseholdCell(this.stage, household, this);
					vbHouseholdList.getChildren().add(cell);
					vbHouseholdList.setSpacing(20);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi", "Rất tiếc, đã có lỗi xảy ra!");
		}
		
	}
	
	public static HouseholdHandler getHandlerFromStage(Stage stage) {
		if (stage != null && stage.getScene() != null && stage.getScene().getUserData() instanceof HouseholdHandler) {
			return (HouseholdHandler) stage.getScene().getUserData();
		}
		return null;
	}
	
	private void handleAddHousehold() {
		try {
			AddHouseholdHandler addHandler = new AddHouseholdHandler(this.stage, this);
			System.out.println("Opening add household form...");
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể hiển thị form thêm hộ khẩu mới");
			e.printStackTrace();
		}
	}
} 