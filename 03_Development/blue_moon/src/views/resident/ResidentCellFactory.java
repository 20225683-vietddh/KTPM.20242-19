package views.resident;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.HBox;
import java.util.Optional;
import controllers.ManageResidentController;
import models.Resident;
import views.messages.ErrorDialog;
import javafx.stage.Stage;

public class ResidentCellFactory extends TableCell<Resident, Void> {
    private final ManageResidentController controller;
    private final Runnable refreshCallback;

    public ResidentCellFactory(ManageResidentController controller, ResidentListHandler parentHandler) {
        this.controller = controller;
        this.refreshCallback = parentHandler::refreshTable; // Gán callback
        System.out.println("Khởi tạo ResidentCellFactory, refreshCallback: " + (refreshCallback != null ? "not null" : "null"));
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            Button btnView = new Button("Xem");
            Button btnEdit = new Button("Sửa");
            Button btnDelete = new Button("Xóa");

            btnView.setOnAction(e -> handleViewResident(getTableView().getItems().get(getIndex())));
            btnEdit.setOnAction(e -> handleEditResident(getTableView().getItems().get(getIndex())));
            btnDelete.setOnAction(e -> handleDeleteResident(getTableView().getItems().get(getIndex())));

            HBox actionTypes = new HBox(10, btnView, btnEdit, btnDelete);
            setGraphic(actionTypes);
        }
    }

    private void handleViewResident(Resident resident) {
        try {
            ResidentViewFormHandler formHandler = new ResidentViewFormHandler(new Stage(), resident); // Truyền Stage
            formHandler.show();
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi hiển thị thông tin", "Không thể mở form xem nhân khẩu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditResident(Resident resident) {
        try {
        	System.out.println("Mở ResidentFormHandler cho resident ID: " + resident.getId() + 
                    ", refreshCallback: " + (refreshCallback != null ? "not null" : "null"));
 if (refreshCallback == null) {
     System.err.println("refreshCallback là null trong handleEditResident, không thể làm mới TableView");
 }
            ResidentFormHandler formHandler = new ResidentFormHandler(resident, refreshCallback); // Truyền callback
            formHandler.show();
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi mở form", "Không thể sửa nhân khẩu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteResident(Resident resident) {
        try {
            // Tạo confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xác nhận xóa");
            confirmAlert.setHeaderText("Bạn có chắc chắn xóa nhân khẩu này không?");
            confirmAlert.setContentText("Họ tên: " + resident.getFullName() + "\nID: " + resident.getId());

            // Tùy chỉnh các nút
            ButtonType buttonYes = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonNo = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmAlert.getButtonTypes().setAll(buttonYes, buttonNo);

            // Hiển thị dialog và chờ user response
            Optional<ButtonType> result = confirmAlert.showAndWait();

            // Chỉ xóa khi user chọn "OK"
            if (result.isPresent() && result.get() == buttonYes) {
                controller.handleDeleteResident(resident.getId());
                if (refreshCallback != null) {
                    refreshCallback.run(); // Làm mới TableView
                }

                // Hiển thị thông báo thành công
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Thành công");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Đã xóa nhân khẩu thành công!");
                successAlert.showAndWait();
            }
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi xóa nhân khẩu", "Không thể xóa nhân khẩu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}