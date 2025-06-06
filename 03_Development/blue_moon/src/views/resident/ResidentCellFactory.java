package views.resident;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.HBox;
import java.util.Optional;
import controllers.resident.ResidentController;
import models.Resident;
import views.messages.ErrorDialog;
import javafx.stage.Stage;

public class ResidentCellFactory extends TableCell<Resident, Void> {
    private final ResidentController controller;
    private final Runnable refreshCallback;

    public ResidentCellFactory(ResidentController controller, ResidentListHandler parentHandler) {
        this.controller = controller;
        this.refreshCallback = parentHandler::refreshTable;
        System.out.println("Khoi tao ResidentCellFactory, refreshCallback: " + (refreshCallback != null ? "not null" : "null"));
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            Button btnView = new Button("Xem");
            Button btnEdit = new Button("Sua");
            Button btnDelete = new Button("Xoa");

            btnView.setOnAction(e -> handleViewResident(getTableView().getItems().get(getIndex())));
            btnEdit.setOnAction(e -> handleEditResident(getTableView().getItems().get(getIndex())));
            btnDelete.setOnAction(e -> handleDeleteResident(getTableView().getItems().get(getIndex())));

            HBox actionTypes = new HBox(10, btnView, btnEdit, btnDelete);
            setGraphic(actionTypes);
        }
    }

    private void handleViewResident(Resident resident) {
        try {
            ResidentViewFormHandler formHandler = new ResidentViewFormHandler(new Stage(), resident);
            formHandler.show();
        } catch (Exception e) {
            ErrorDialog.showError("Loi hien thi thong tin", "Khong the mo form xem nhan khau: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditResident(Resident resident) {
        try {
            System.out.println("Mo ResidentFormHandler cho resident ID: " + resident.getId() + 
                    ", refreshCallback: " + (refreshCallback != null ? "not null" : "null"));
            if (refreshCallback == null) {
                System.err.println("refreshCallback la null trong handleEditResident, khong the lam moi TableView");
            }
            ResidentFormHandler formHandler = new ResidentFormHandler(resident, refreshCallback);
            formHandler.show();
        } catch (Exception e) {
            ErrorDialog.showError("Loi mo form", "Khong the sua nhan khau: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteResident(Resident resident) {
        try {
            // Tao confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xac nhan xoa");
            confirmAlert.setHeaderText("Ban co chac chan xoa nhan khau nay khong?");
            confirmAlert.setContentText("Ho ten: " + resident.getFullName() + "\nID: " + resident.getId());

            // Tuy chinh cac nut
            ButtonType buttonYes = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonNo = new ButtonType("Huy", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmAlert.getButtonTypes().setAll(buttonYes, buttonNo);

            // Hien thi dialog va cho user response
            Optional<ButtonType> result = confirmAlert.showAndWait();

            // Chi xoa khi user chon "OK"
            if (result.isPresent() && result.get() == buttonYes) {
                controller.deleteResident(resident.getCitizenId());
                if (refreshCallback != null) {
                    refreshCallback.run(); // Lam moi TableView
                }

                // Hien thi thong bao thanh cong
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Thanh cong");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Da xoa nhan khau thanh cong!");
                successAlert.showAndWait();
            }
        } catch (Exception e) {
            ErrorDialog.showError("Loi xoa nhan khau", "Khong the xoa nhan khau: " + e.getMessage());
            e.printStackTrace();
        }
    }
}