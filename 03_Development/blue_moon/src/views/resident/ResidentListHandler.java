package views.resident;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import controllers.ManageResidentController;
import javafx.stage.Stage;
import models.Resident;
import views.BaseScreenHandler;
import views.BaseScreenWithLogoutAndGoBackHandler;
import views.ScreenNavigator;
import views.messages.ErrorDialog;
import java.io.IOException;
import java.util.List;

public class ResidentListHandler extends BaseScreenWithLogoutAndGoBackHandler {

    @FXML 
    private TableView<Resident> tableResident;
    @FXML 
    private TableColumn<Resident, Integer> colId;
    @FXML 
    private TableColumn<Resident, String> colFullName;
    @FXML 
    private TableColumn<Resident, Integer> colHousehold_id;
    @FXML 
    private TableColumn<Resident, String> colRelationship_with_head;
    @FXML 
    private TableColumn<Resident, Void> colActions;
    @FXML 
    private Button btnAddResident;
    @FXML 
    private Button btnGoBack;
    @FXML 
    private Button btnLogout;
    
    private final ManageResidentController controller = new ManageResidentController();

    public ResidentListHandler(Stage stage) throws Exception {
        super(stage, utils.Configs.RESIDENT_LIST_PATH, utils.Configs.LOGO_PATH, "Quản lý nhân khẩu");
        loader.setController(this);
        this.setContent();
        this.setScene();
    }

    @FXML
    public void initialize() {
        initializeTable();
        loadResidentList();
        btnLogout.setOnAction(e -> handleLogout());
        if (btnAddResident != null) {
            btnAddResident.setOnAction(e -> handleAddResident());
        } else {
            System.err.println("btnAddResident is null. Check ResidentList.fxml for fx:id='btnAddResident'.");
        }
        btnGoBack.setOnAction(event -> handleGoBack());
    }

    private void initializeTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colHousehold_id.setCellValueFactory(new PropertyValueFactory<>("householdId"));
        colRelationship_with_head.setCellValueFactory(new PropertyValueFactory<>("relationshipWithHead"));
        colActions.setCellFactory(param -> new ResidentCellFactory(controller, this));
    }

    private void loadResidentList() {
        try {
            List<Resident> residents = controller.handleGetAllResidents();
            System.out.println("Tải danh sách nhân khẩu: " + residents.size() + " bản ghi");
            tableResident.getItems().setAll(residents);
            tableResident.refresh(); // Làm mới giao diện
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi tải danh sách", "Không thể tải danh sách nhân khẩu từ database.");
            e.printStackTrace();
        }
    }

    public void refreshTable() {
        tableResident.getItems().clear();
        loadResidentList();
        System.out.println("Đã làm mới TableView");
    }
    
    protected void handleGoBack() {
        System.out.println("Quay lại màn hình trước đó");
        ScreenNavigator.goBack();
    }

    private void handleAddResident() {
        try {
            System.out.println("Mở form thêm nhân khẩu mới");
            NewResidentFormHandler formHandler = new NewResidentFormHandler(tableResident, this::refreshTable);
            formHandler.show();
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi hệ thống", "Không thể hiển thị form thêm nhân khẩu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}