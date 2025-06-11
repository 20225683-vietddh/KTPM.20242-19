package views.household;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import controllers.household.HouseholdController;
import controllers.resident.ResidentController;
import exception.ServiceException;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Household;
import models.Resident;
import services.household.HouseholdServiceImpl;
import services.resident.ResidentServiceImpl;
import services.room.RoomServiceImpl;
import utils.AlertUtils;
import utils.Configs;
import utils.SceneUtils;
import utils.Utils;
import views.homepage.HomePageHandler;

public class HouseholdListHandler extends HomePageHandler implements Initializable {
	// Service layer
	private HouseholdController householdController = new HouseholdController();
	private ResidentServiceImpl residentService = new ResidentServiceImpl();
	private RoomServiceImpl roomService = new RoomServiceImpl();

	// Update your data collections field declarations
	private ObservableList<Household> householdList;
	private FilteredList<Household> filteredHouseholdList;
	private SortedList<Household> sortedHouseholdList;

	private final String userName;

	// Main action buttons
	@FXML
	private Button btnAddHousehold;
	@FXML
	private Button btnRefresh;
	@FXML
	private Button btnResidentList;
	@FXML
	private Button btnHomePage;
	@FXML
	private Button btnHouseholdList;

	// Search field
	@FXML
	private TextField txtSearch;

	// Table and columns
	@FXML
	private TableView<Household> tblHouseholds;
	@FXML
	private TableColumn<Household, Integer> colId;
	@FXML
	private TableColumn<Household, String> colOwnerName;
	@FXML
	private TableColumn<Household, String> colRoomNumber;
	@FXML
	private TableColumn<Household, Void> colActions;

	protected FXMLLoader loader;
	private Stage stage;

//	    public LeaderHomePageHandler(Stage stage, String userName) throws Exception {
//    		super(stage, utils.Configs.LEADER_HOME_PAGE_PATH);
//    		this.stage = stage;
//    		loader.setController(this);
//    		this.setContent();
//    		this.setScene();
//    		super.lblUserName.setText(userName);
//    	}

	public HouseholdListHandler(Stage stage, String userName) throws Exception {
		super(stage, Configs.HOUSEHOLD_LIST_PATH);
		this.householdController = new HouseholdController();
		this.userName = userName;

		// Set this instance as the controller before loading FXML
		super.loader.setController(this);

		// Load FXML content
		this.setContent();
		this.setScene();

		// Set the username
		super.lblUserName.setText(userName);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		super.initialize();
		// Initialize your components here
		setupTableColumns();
		setupSearch();
		setupButtonActions();
		loadHouseholdData();
	}

	private void setupTableColumns() {
		try {

			// First check if columns are properly initialized
			if (colId == null) {
				System.err.println("Error: Column ID is null. FXML not properly loaded.");
				AlertUtils.showErrorAlert("Error", "UI Initialization Failed",
						"Table columns were not properly initialized. Please report this issue.");
				return;
			}

			// Set up basic columns with property values
			colId.setCellValueFactory(new PropertyValueFactory<>("id"));
			colOwnerName.setCellValueFactory(cellData -> {
				Household household = cellData.getValue();
				try {
					Resident owner = residentService.getResidentById(household.getOwnerId());
					return new ReadOnlyStringWrapper(owner.getFullName());
				} catch (ServiceException e) {
					System.err.println("Error getting owner name: " + e.getMessage());
					return new ReadOnlyStringWrapper("Unknown");
				}
			});
			colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("houseNumber"));

			// Sử dụng tỷ lệ phần trăm
			colId.prefWidthProperty().bind(tblHouseholds.widthProperty().multiply(0.1)); // 10%
			colOwnerName.prefWidthProperty().bind(tblHouseholds.widthProperty().multiply(0.35)); // 35%
			colRoomNumber.prefWidthProperty().bind(tblHouseholds.widthProperty().multiply(0.35)); // 35%
			colActions.prefWidthProperty().bind(tblHouseholds.widthProperty().multiply(0.2)); // 20%

			// Set up action column with View/Edit/Delete buttons
			setupActionColumn();
		} catch (Exception e) {
			e.printStackTrace();
			AlertUtils.showErrorAlert("Error", "Table Setup Failed", e.getMessage());
		}
	}

	private void setupActionColumn() {
		Callback<TableColumn<Household, Void>, TableCell<Household, Void>> cellFactory = new Callback<TableColumn<Household, Void>, TableCell<Household, Void>>() {
			@Override
			public TableCell<Household, Void> call(final TableColumn<Household, Void> param) {
				return new TableCell<Household, Void>() {

					private final Button btnView = new Button("Xem");
					private final Button btnDelete = new Button("Xoa");

					{
						// Configure view button
						btnView.setStyle("-fx-background-color: #43A5DC; -fx-text-fill: white;");
						btnView.setOnAction((ActionEvent event) -> {
							Household household = getTableView().getItems().get(getIndex());
							openViewHouseholdDialog(household);
						});

						// Configure delete button
						btnDelete.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
						btnDelete.setOnAction((ActionEvent event) -> {
							Household household = getTableView().getItems().get(getIndex());
							deleteHousehold(household);
						});
					}

					@Override
					public void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							HBox buttons = new HBox(5);
							buttons.getChildren().addAll(btnView, btnDelete);
							setGraphic(buttons);
						}
					}
				};
			}
		};

		colActions.setCellFactory(cellFactory);
	}

	private void loadHouseholdData() {
		try {
			tblHouseholds.refresh();
			txtSearch.setText("");
			// Get all households from the controller
			householdList = FXCollections.observableArrayList(householdController.getAllHouseholds());

			// Initialize filtered list
			filteredHouseholdList = new FilteredList<>(householdList, p -> true);

			// Initialize sorted list
			sortedHouseholdList = new SortedList<>(filteredHouseholdList);
			sortedHouseholdList.comparatorProperty().bind(tblHouseholds.comparatorProperty());

			// Set the sorted/filtered data to the table
			tblHouseholds.setItems(sortedHouseholdList);

		} catch (ServiceException e) {
			System.err.println("Error loading household data: " + e.getMessage());
			AlertUtils.showErrorAlert("Error", "Failed to Load Data",
					"Could not load household data. Please try again later.\n\nError: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Unexpected error: " + e.getMessage());
			e.printStackTrace();
			AlertUtils.showErrorAlert("Error", "System Error",
					"An unexpected error occurred. Please contact support.\n\nError: " + e.getMessage());
		}
	}

	private void setupSearch() {
		txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredHouseholdList.setPredicate(household -> {
				// If search text is empty, show all items
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase();

				try {
					if (household.getOwnerName().toLowerCase().contains(lowerCaseFilter)) {
						return true; // Match by owner name
					} else if (household.getHouseNumber().toLowerCase().contains(lowerCaseFilter)) {
						return true; // Match by address
					} else if (household.getPhone() != null
							&& household.getPhone().toLowerCase().contains(lowerCaseFilter)) {
						return true; // Match by phone number
					}
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false; // No match
			});
		});
	}

	private void setupButtonActions() {
		// Add new household button
		btnAddHousehold.setOnAction(event -> openAddHouseholdDialog());

		// Refresh button
		btnRefresh.setOnAction(event -> loadHouseholdData());

		// Resident list button
		btnResidentList.setOnAction(event -> {
			try {
				Stage stage = (Stage) btnResidentList.getScene().getWindow();
				SceneUtils.navigateToResidentList(event);
			} catch (Exception e) {
				AlertUtils.showErrorAlert("Lỗi", "Không thể mở danh sách nhân khẩu", e.getMessage());
			}
		});

		// Both Home and Household list buttons do nothing since we're already on the
		// household list page
		btnHomePage.setOnAction(event -> {
			// Do nothing since we're already on the household list page
			loadHouseholdData(); // Just refresh the data
		});

		// Household list button (current page)
		btnHouseholdList.setOnAction(event -> {
			// Do nothing since we're already on the household list page
			loadHouseholdData(); // Just refresh the data
		});
	}

	private void openAddHouseholdDialog() {
		try {
			SceneUtils.openAddHouseholdDialog(householdController, residentService, roomService, btnAddHousehold,
					this::loadHouseholdData);
		} catch (Exception e) {
			AlertUtils.showErrorAlert("Lỗi", "Không thể mở form thêm hộ khẩu", e.getMessage());
			e.printStackTrace();
		}
	}

	private void openViewHouseholdDialog(Household household) {
		try {
			SceneUtils.openViewHouseholdDialog(householdController, residentService, household, btnAddHousehold,
					this::loadHouseholdData);
		} catch (Exception e) {
			AlertUtils.showErrorAlert("Lỗi", "Không thể mở chi tiết hộ khẩu", e.getMessage());
			e.printStackTrace();
		}
	}

	private void deleteHousehold(Household household) {
		// Confirm before deletion
		Optional<ButtonType> result = AlertUtils.showConfirmationAlert("Xác nhận xóa", "Xóa hộ khẩu",
				"Bạn có chắc chắn muốn xóa hộ khẩu này? Hành động này không thể hoàn tác.");

		if (result.isPresent() && result.get() == ButtonType.OK) {
			try {
				// Check if household has members
				int memberCount = householdController.getResidentCount(household.getId());
				if (memberCount > 0) {
					AlertUtils.showWarningAlert("Không thể xóa", "Hộ khẩu vẫn còn nhân khẩu",
							"Hộ khẩu này vẫn còn " + memberCount
									+ " nhân khẩu. Vui lòng chuyển hoặc xóa tất cả nhân khẩu trước khi xóa hộ khẩu.");
					return;
				}

				// Proceed with deletion
				boolean deleted = householdController.deleteHousehold(household.getId());
				if (deleted) {
					AlertUtils.showInfoAlert("Thành công", "Xóa hộ khẩu thành công",
							"Hộ khẩu đã được xóa khỏi hệ thống.");
					loadHouseholdData(); // Reload data
				} else {
					AlertUtils.showErrorAlert("Lỗi", "Không thể xóa hộ khẩu",
							"Có lỗi xảy ra khi xóa hộ khẩu. Vui lòng thử lại.");
				}
			} catch (Exception e) {
				AlertUtils.showErrorAlert("Lỗi", "Không thể xóa hộ khẩu", e.getMessage());
				e.printStackTrace();
			}
		}
	}

}