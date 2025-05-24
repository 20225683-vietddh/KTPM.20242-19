//package views.citizen;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.control.TableColumn;
//import javafx.stage.Stage;
//import models.Citizen;
//import controllers.CitizenController;
//import views.BaseScreenHandler;
//import java.util.List;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.control.TableCell;
//import javafx.scene.control.Button;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//
//import java.time.LocalDate;
//
//public class CitizenManagementPageHandler extends BaseScreenHandler {
//    @FXML private TableView<Citizen> tableCitizens;
//    @FXML private TableColumn<Citizen, Integer> colId;
//    @FXML private TableColumn<Citizen, String> colFullName;
//    @FXML private Button btnAddCitizen;
//    
//    public CitizenManagementPageHandler(Stage stage) throws Exception {
//        super(stage, utils.Configs.CITIZEN_MANAGEMENT_PAGE_PATH, utils.Configs.LOGO_PATH, "Quản lý nhân khẩu");
//        loader.setController(this);
//        this.setContent();
//        this.setScene();
//    }
//
//    @FXML
//    public void initialize() {
//        CitizenController controller = new CitizenController();
//        List<Citizen> citizens = controller.getAllCitizens();
//
//        ObservableList<Citizen> citizenList = FXCollections.observableArrayList(citizens);
//        tableCitizens.setItems(citizenList);
//
//        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
//        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
//  
//    }
//}

package views.citizen;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Citizen;
import controllers.CitizenController;
import views.BaseScreenHandler;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.time.LocalDate;

public class CitizenManagementPageHandler extends BaseScreenHandler {
    @FXML private TableView<Citizen> tableCitizens;
    @FXML private TableColumn<Citizen, Integer> colId;
    @FXML private TableColumn<Citizen, String> colFullName;
    @FXML private TableColumn<Citizen, Void> colActions;
    @FXML private Button btnAddCitizen;

    public CitizenManagementPageHandler(Stage stage) throws Exception {
        super(stage, utils.Configs.CITIZEN_MANAGEMENT_PAGE_PATH, utils.Configs.LOGO_PATH, "Quản lý nhân khẩu");
        loader.setController(this);
        this.setContent();
        this.setScene();
    }

    @FXML
    public void initialize() {
        CitizenController controller = new CitizenController();
        List<Citizen> citizens = controller.getAllCitizens();

        ObservableList<Citizen> citizenList = FXCollections.observableArrayList(citizens);
        tableCitizens.setItems(citizenList);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        // Cột Sửa/Xóa
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Sửa");
            private final Button deleteButton = new Button("Xóa");

            {
                editButton.setStyle("-fx-background-color: #4facfe; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #ff6f91; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Citizen citizen = getTableView().getItems().get(getIndex());
                    handleEditCitizen(citizen);
                });
                deleteButton.setOnAction(event -> {
                    Citizen citizen = getTableView().getItems().get(getIndex());
                    handleDeleteCitizen(citizen);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });

        // Gắn sự kiện onAction cho nút Thêm
        btnAddCitizen.setOnAction(event -> handleAddCitizen());
    }

    @FXML
    private void handleAddCitizen() {
        // Tạo dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Thêm nhân khẩu mới");

        // Tạo các trường nhập liệu
        TextField txtId = new TextField();
        txtId.setPromptText("ID");
        TextField txtFullName = new TextField();
        txtFullName.setPromptText("Họ tên");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Ngày sinh");
        ChoiceBox<String> genderChoice = new ChoiceBox<>();
        genderChoice.getItems().addAll("Nam", "Nữ");
        genderChoice.setValue("Nam");
        TextField txtAddress = new TextField();
        txtAddress.setPromptText("Địa chỉ");
        TextField txtIdentityNumber = new TextField();
        txtIdentityNumber.setPromptText("Số CMND");

        // Tạo layout cho dialog
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
            new Label("ID:"), txtId,
            new Label("Họ tên:"), txtFullName,
            new Label("Ngày sinh:"), datePicker,
            new Label("Giới tính:"), genderChoice,
            new Label("Địa chỉ:"), txtAddress,
            new Label("Số CMND:"), txtIdentityNumber
        );

        // Nút xác nhận và hủy
        Button btnConfirm = new Button("Xác nhận");
        Button btnCancel = new Button("Hủy");
        btnConfirm.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                String fullName = txtFullName.getText().trim();
                LocalDate dateOfBirth = datePicker.getValue();
                String gender = genderChoice.getValue();
                String address = txtAddress.getText().trim();
                String identityNumber = txtIdentityNumber.getText().trim();

                if (fullName.isEmpty() || dateOfBirth == null || gender.isEmpty() || address.isEmpty() || identityNumber.isEmpty()) {
                    showAlert("Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                // Kiểm tra ID trùng lặp
                if (tableCitizens.getItems().stream().anyMatch(c -> c.getId() == id)) {
                    showAlert("ID đã tồn tại!");
                    return;
                }

                Citizen newCitizen = new Citizen(id, fullName, dateOfBirth, gender, address, identityNumber);
                CitizenController controller = new CitizenController();
                controller.addCitizen(newCitizen);
                tableCitizens.getItems().add(newCitizen);
                dialogStage.close();
            } catch (NumberFormatException ex) {
                showAlert("ID phải là số nguyên!");
            }
        });
        btnCancel.setOnAction(e -> dialogStage.close());

        vbox.getChildren().addAll(btnConfirm, btnCancel);
        javafx.scene.Scene dialogScene = new javafx.scene.Scene(vbox, 300, 400);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private void handleEditCitizen(Citizen citizen) {
        // Tạo dialog chỉnh sửa
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Chỉnh sửa nhân khẩu");

        // Tạo các trường nhập liệu, điền sẵn thông tin hiện tại
        TextField txtId = new TextField(String.valueOf(citizen.getId()));
        txtId.setPromptText("ID");
        txtId.setDisable(true); // Không cho sửa ID
        TextField txtFullName = new TextField(citizen.getFullName());
        txtFullName.setPromptText("Họ tên");
        DatePicker datePicker = new DatePicker(citizen.getDateOfBirth());
        datePicker.setPromptText("Ngày sinh");
        ChoiceBox<String> genderChoice = new ChoiceBox<>();
        genderChoice.getItems().addAll("Nam", "Nữ");
        genderChoice.setValue(citizen.getGender());
        TextField txtAddress = new TextField(citizen.getAddress());
        txtAddress.setPromptText("Địa chỉ");
        TextField txtIdentityNumber = new TextField(citizen.getIdentityNumber());
        txtIdentityNumber.setPromptText("Số CMND");

        // Tạo layout cho dialog
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
            new Label("ID:"), txtId,
            new Label("Họ tên:"), txtFullName,
            new Label("Ngày sinh:"), datePicker,
            new Label("Giới tính:"), genderChoice,
            new Label("Địa chỉ:"), txtAddress,
            new Label("Số CMND:"), txtIdentityNumber
        );

        // Nút xác nhận và hủy
        Button btnConfirm = new Button("Xác nhận");
        Button btnCancel = new Button("Hủy");
        btnConfirm.setOnAction(e -> {
            try {
                String fullName = txtFullName.getText().trim();
                LocalDate dateOfBirth = datePicker.getValue();
                String gender = genderChoice.getValue();
                String address = txtAddress.getText().trim();
                String identityNumber = txtIdentityNumber.getText().trim();

                if (fullName.isEmpty() || dateOfBirth == null || gender.isEmpty() || address.isEmpty() || identityNumber.isEmpty()) {
                    showAlert("Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                // Cập nhật thông tin nhân khẩu
                citizen.setFullName(fullName);
                citizen.setDateOfBirth(dateOfBirth);
                citizen.setGender(gender);
                citizen.setAddress(address);
                citizen.setIdentityNumber(identityNumber);

                // Cập nhật trong TableView
                tableCitizens.refresh();
                dialogStage.close();
            } catch (Exception ex) {
                showAlert("Có lỗi xảy ra: " + ex.getMessage());
            }
        });
        btnCancel.setOnAction(e -> dialogStage.close());

        vbox.getChildren().addAll(btnConfirm, btnCancel);
        javafx.scene.Scene dialogScene = new javafx.scene.Scene(vbox, 300, 400);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private void handleDeleteCitizen(Citizen citizen) {
        // Hiển thị hộp thoại xác nhận
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn xóa nhân khẩu " + citizen.getFullName() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            CitizenController controller = new CitizenController();
            controller.deleteCitizen(citizen.getId());
            tableCitizens.getItems().remove(citizen);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}