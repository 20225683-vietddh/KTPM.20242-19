package views.resident;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.stream.Collectors;
import views.BaseScreenWithLogoutAndGoBackHandler;
import views.messages.ErrorDialog;
import models.Resident;
import models.Household;
import services.HouseholdService;
import services.ResidentService;

public class ResidentHandler extends BaseScreenWithLogoutAndGoBackHandler {
	@FXML private Label lblUserName;
	@FXML private VBox vbResidentList;
	@FXML private Button btnAddResident;
	@FXML private TextField tfSearch;
	@FXML private ComboBox<String> cbHouseholdFilter;
	private HouseholdService householdService;
	private ResidentService residentService;
	private List<Household> allHouseholds;
	
	public ResidentHandler(Stage stage, String userName) throws Exception {
		super(stage, utils.Configs.RESIDENT_SCREEN_PATH, utils.Configs.LOGO_PATH, "Danh sách nhân khẩu");
		this.householdService = new HouseholdService();
		this.residentService = new ResidentService();
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
		setupHouseholdFilter();
        loadResidentList("", "Tất cả hộ khẩu");
        
        // Setup event listeners
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            loadResidentList(newValue, cbHouseholdFilter.getValue());
        });
        
        cbHouseholdFilter.setOnAction(e -> {
        	loadResidentList(tfSearch.getText(), cbHouseholdFilter.getValue());
        });
        
		btnAddResident.setOnAction(e -> handleAddResident());
	}
	
	private void setupHouseholdFilter() {
		try {
			allHouseholds = householdService.getAllHouseholds();
			ObservableList<String> householdOptions = FXCollections.observableArrayList();
			householdOptions.add("Tất cả hộ khẩu");
			
			for (Household household : allHouseholds) {
				String option = "Hộ " + household.getHouseholdId() + " - " + household.getHouseNumber();
				householdOptions.add(option);
			}
			
			cbHouseholdFilter.setItems(householdOptions);
			cbHouseholdFilter.setValue("Tất cả hộ khẩu");
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi", "Không thể tải danh sách hộ khẩu!");
		}
	}
	
	public void loadResidentList(String searchTerm, String householdFilter) {
		try {
			List<Resident> residents;
			
			// Get residents based on household filter
			if (householdFilter == null || householdFilter.equals("Tất cả hộ khẩu")) {
				// Get all residents
				if (searchTerm != null && !searchTerm.trim().isEmpty()) {
					residents = residentService.searchResidents(searchTerm);
				} else {
					residents = residentService.getAllResidents();
				}
			} else {
				// Extract household ID from filter string
				int householdId = extractHouseholdIdFromFilter(householdFilter);
				residents = residentService.getResidentsByHouseholdId(householdId);
				
				// Apply search filter if needed
				if (searchTerm != null && !searchTerm.trim().isEmpty()) {
		            String lowerSearchTerm = searchTerm.toLowerCase();
		            residents = residents.stream()
		                .filter(resident -> 
		                	(resident.getFullName() != null && resident.getFullName().toLowerCase().contains(lowerSearchTerm)) ||
		                	(resident.getCitizenId() != null && resident.getCitizenId().toLowerCase().contains(lowerSearchTerm)) ||
		                	(resident.getOccupation() != null && resident.getOccupation().toLowerCase().contains(lowerSearchTerm))
		                )
		                .collect(Collectors.toList());
		        }
			}
			
			// Display residents
			if (vbResidentList != null) {
				vbResidentList.getChildren().clear();
				for (Resident resident : residents) {
					ResidentCell cell = new ResidentCell(this.stage, resident, this);
					vbResidentList.getChildren().add(cell);
					vbResidentList.setSpacing(15);
				}
				
				// Show message if no residents found
				if (residents.isEmpty()) {
					Label noDataLabel = new Label("Không tìm thấy nhân khẩu nào");
					noDataLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px; -fx-padding: 20px;");
					vbResidentList.getChildren().add(noDataLabel);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.showError("Lỗi", "Rất tiếc, đã có lỗi xảy ra khi tải danh sách nhân khẩu!");
		}
	}
	
	private int extractHouseholdIdFromFilter(String filter) {
		// Extract ID from "Hộ 1 - Nhà_6/Tầng_3/BlueMoon" format
		try {
			String[] parts = filter.split(" - ");
			String idPart = parts[0].replace("Hộ ", "");
			return Integer.parseInt(idPart);
		} catch (Exception e) {
			return 1; // Default fallback
		}
	}
	
	public static ResidentHandler getHandlerFromStage(Stage stage) {
		if (stage != null && stage.getScene() != null && stage.getScene().getUserData() instanceof ResidentHandler) {
			return (ResidentHandler) stage.getScene().getUserData();
		}
		return null;
	}
	
	private void handleAddResident() {
		try {
			AddResidentHandler addHandler = new AddResidentHandler(this.stage, this);
			System.out.println("Opening add resident form...");
		} catch (Exception e) {
			ErrorDialog.showError("Lỗi hệ thống", "Không thể hiển thị form thêm nhân khẩu mới: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public HouseholdService getHouseholdService() {
		return householdService;
	}
	
	public ResidentService getResidentService() {
		return residentService;
	}
	
	public List<Household> getAllHouseholds() {
		return allHouseholds;
	}
} 