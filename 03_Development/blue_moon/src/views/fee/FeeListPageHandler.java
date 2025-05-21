package views.fee;

import java.util.List;

import controllers.FeeListController;
import dto.FeeListDTO;
import exception.InvalidInputException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import models.Fee;
import views.BaseScreenWithLogoutAndGoBackHandler;
import views.messages.ErrorDialog;
import views.homepage.AccountantHomePageHandler;
import utils.Status;
import utils.Mandatory;

public class FeeListPageHandler extends BaseScreenWithLogoutAndGoBackHandler {
    @FXML private Button btnHome;
    @FXML private Button btnCampaignFees;
    @FXML private Button btnFees;
    @FXML private Button btnHouseholds;
    @FXML private Button btnStatistics;

    @FXML private Button btnAddFee;

    @FXML private ToggleButton tabToday;
    @FXML private ToggleButton tabYesterday;
    @FXML private ToggleButton tabWeek;
    @FXML private ToggleButton tabMonth;

    @FXML private TextField tfSearch;

    @FXML private TableView<Fee> tblFees;
    @FXML private TableColumn<Fee, String> colId;
    @FXML private TableColumn<Fee, String> colName;
    @FXML private TableColumn<Fee, String> colCreatedDate;
    @FXML private TableColumn<Fee, Double> colAmount;
    @FXML private TableColumn<Fee, Mandatory> colMandatory;
    @FXML private TableColumn<Fee, Status> colStatus;
    @FXML private TableColumn<Fee, String> colDescription;
    @FXML private TableColumn<Fee, Void> colActions;

    @FXML private Label lblUnpaidCount;

    private FeeListController controller;
    private ObservableList<Fee> allFees = FXCollections.observableArrayList();
    private ObservableList<Fee> filteredFees = FXCollections.observableArrayList();

    public FeeListPageHandler(Stage stage, String screenPath) throws Exception {
        super(stage, screenPath, utils.Configs.LOGO_PATH, "Danh sách khoản thu");
        this.controller = new FeeListController();
        loader.setController(this);
        this.setContent();
        this.setScene();
        initialize();
    }

    @FXML
    public void initialize() {
        super.initialize();
        if (tblFees != null) {
            setupTable();
            loadFeeData();
            tblFees.setItems(allFees);
        }
        if (lblUnpaidCount != null) {
            lblUnpaidCount.setText("1342"); // demo
        }
        if (tfSearch != null) {
            tfSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        }
        setupTabFilters();
        if (btnAddFee != null) {
            btnAddFee.setOnAction(e -> handleAddFee(e));
        }
        setupNavbarEvents();
    }

    private void setupTable() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colName != null) colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (colCreatedDate != null) {
            colCreatedDate.setCellValueFactory(cellData -> {
                if (cellData.getValue().getCreatedDate() != null)
                    return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreatedDate().toString());
                else
                    return new javafx.beans.property.SimpleStringProperty("");
            });
        }
        if (colAmount != null) colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        if (colMandatory != null) colMandatory.setCellValueFactory(new PropertyValueFactory<>("isMandatory"));
        if (colStatus != null) colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (colDescription != null) colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        if (colActions != null) {
            colActions.setCellFactory(param -> new TableCell<Fee, Void>() {
                private final Button btnUpdate = new Button("Sửa");
                private final Button btnDelete = new Button("Xóa");
                {
                    btnUpdate.setOnAction((ActionEvent event) -> {
                        Fee fee = getTableView().getItems().get(getIndex());
                        handleUpdateFee(event, fee);
                    });
                    btnDelete.setOnAction((ActionEvent event) -> {
                        Fee fee = getTableView().getItems().get(getIndex());
                        handleDeleteFee(event, fee);
                    });
                    btnUpdate.setStyle("-fx-background-color: #43A5DC; -fx-text-fill: white; -fx-background-radius: 6px;");
                    btnDelete.setStyle("-fx-background-color: #FF7BAC; -fx-text-fill: white; -fx-background-radius: 6px;");
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox box = new HBox(10, btnUpdate, btnDelete);
                        setGraphic(box);
                    }
                }
            });
        }
        
        // Custom cell factory for mandatory column to display Vietnamese text
        if (colMandatory != null) {
            colMandatory.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Mandatory mandatory, boolean empty) {
                    super.updateItem(mandatory, empty);
                    if (empty || mandatory == null) {
                        setText(null);
                    } else {
                        setText(mandatory.getDisplayName());
                    }
                }
            });
        }
        
        // Custom cell factory for status column to display Vietnamese text
        if (colStatus != null) {
            colStatus.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Status status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) {
                        setText(null);
                    } else {
                        setText(status.getDisplayName());
                    }
                }
            });
        }
    }

    private void setupTabFilters() {
        tabToday.setOnAction(e -> selectTab(tabToday));
        tabYesterday.setOnAction(e -> selectTab(tabYesterday));
        tabWeek.setOnAction(e -> selectTab(tabWeek));
        tabMonth.setOnAction(e -> selectTab(tabMonth));
    }

    private void setupNavbarEvents() {
        btnHome.setOnAction(e -> handleHome());
        btnCampaignFees.setOnAction(e -> handleCampaignFees());
        btnFees.setOnAction(e -> {}); // Đang ở trang này
        btnHouseholds.setOnAction(e -> handleHouseholds());
        btnStatistics.setOnAction(e -> handleStatistics());
    }

    private void selectTab(ToggleButton selected) {
        tabToday.setSelected(false);
        tabYesterday.setSelected(false);
        tabWeek.setSelected(false);
        tabMonth.setSelected(false);
        selected.setSelected(true);
        applyFilter();
    }

    private void applyFilter() {
        String search = tfSearch.getText() == null ? "" : tfSearch.getText().toLowerCase();
        filteredFees.setAll(allFees.filtered(fee ->
            (fee.getId().toLowerCase().contains(search) ||
             fee.getName().toLowerCase().contains(search))
            // Có thể bổ sung filter theo tab ở đây
        ));
    }

    private void loadFeeData() {
        try {
            allFees.clear();
            List<FeeListDTO> feeDTOs = controller.showFees();
            for (FeeListDTO dto : feeDTOs) {
                allFees.add(new Fee(
                    dto.getId(),
                    dto.getName(),
                    dto.getCreatedDate(),
                    dto.getAmount(),
                    dto.getIsMandatory(),
                    dto.getStatus(),
                    dto.getDescription()
                ));
            }
            tblFees.setItems(allFees);
        } catch (InvalidInputException e) {
            showAlert(AlertType.ERROR, "Lỗi dữ liệu", e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }
    
    private void handleHome() {
        try {
            // Create new instance of AccountantHomePage and show it
            AccountantHomePageHandler homePage = new AccountantHomePageHandler(this.stage);
            homePage.show();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi hệ thống", "Không thể quay về trang chủ!");
        }
    }

    private void handleCampaignFees() {
        try {
            // TODO: Tạo handler cho trang đợt thu
            // CampaignFeesPageHandler handler = new CampaignFeesPageHandler(this.stage, utils.Configs.CAMPAIGN_FEES_PAGE_PATH);
            // handler.show();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang danh sách đợt thu!");
        }
    }

    private void handleHouseholds() {
        try {
            // TODO: Tạo handler cho trang hộ dân
            // HouseholdsPageHandler handler = new HouseholdsPageHandler(this.stage, utils.Configs.HOUSEHOLDS_PAGE_PATH);
            // handler.show();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang danh sách hộ dân!");
        }
    }

    private void handleStatistics() {
        try {
            // TODO: Tạo handler cho trang thống kê
            // StatisticsPageHandler handler = new StatisticsPageHandler(this.stage, utils.Configs.STATISTICS_PAGE_PATH);
            // handler.show();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Lỗi hệ thống", "Không thể mở trang thống kê!");
        }
    }

    private void handleAddFee(ActionEvent event) {
        try {
            Stage addFeeStage = new Stage();
            addFeeStage.initModality(Modality.APPLICATION_MODAL);
            addFeeStage.initOwner(this.stage);
            addFeeStage.setTitle("Thêm khoản thu");
            AddFeeFormHandler addFeeFormHandler = new AddFeeFormHandler(addFeeStage, utils.Configs.ADD_FEE_FORM_PATH, utils.Configs.LOGO_PATH, "Tạo khoản thu");
            addFeeFormHandler.show();
            loadFeeData();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    private void handleUpdateFee(ActionEvent event, Fee fee) {
        try {
            Stage updateFeeStage = new Stage();
            updateFeeStage.initModality(Modality.APPLICATION_MODAL);
            updateFeeStage.initOwner(this.stage);
            updateFeeStage.setTitle("Sửa khoản thu");
            UpdateFeeFormHandler updateFeeFormHandler = new UpdateFeeFormHandler(updateFeeStage, utils.Configs.UPDATE_FEE_FORM_PATH, utils.Configs.LOGO_PATH, "Sửa khoản thu");
            updateFeeFormHandler.loadFeeData(fee.getId());
            updateFeeFormHandler.show();
            loadFeeData();
        } catch (InvalidInputException e) {
            showAlert(AlertType.ERROR, "Lỗi dữ liệu", e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    private void handleDeleteFee(ActionEvent event, Fee fee) {
        try {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText(null);
            alert.setContentText("Bạn có chắc chắn muốn xóa khoản thu này?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                boolean result = controller.deleteFee(fee.getId());
                if (result) {
                    showAlert(AlertType.INFORMATION, "Thành công", "Xóa khoản thu thành công!");
                    loadFeeData();
                }
            }
        } catch (InvalidInputException e) {
            showAlert(AlertType.ERROR, "Lỗi dữ liệu", e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 