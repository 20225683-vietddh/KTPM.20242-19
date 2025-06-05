package views.resident;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.HBox;
import java.util.Optional;
import controllers.resident.ManageResidentController;
import models.Resident;
import views.messages.ErrorDialog;
import javafx.stage.Stage;

public class ResidentCellFactory extends TableCell<Resident, Void> {
    private final ManageResidentController controller;
    private final Runnable refreshCallback;

    public ResidentCellFactory(ManageResidentController controller, ResidentListHandler parentHandler) {
        this.controller = controller;
        this.refreshCallback = parentHandler::refreshTable; // GÃ¡n callback
        System.out.println("Khá»Ÿi táº¡o ResidentCellFactory, refreshCallback: " + (refreshCallback != null ? "not null" : "null"));
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            Button btnView = new Button("Xem");
            Button btnEdit = new Button("Sá»­a");
            Button btnDelete = new Button("XÃ³a");

            btnView.setOnAction(e -> handleViewResident(getTableView().getItems().get(getIndex())));
            btnEdit.setOnAction(e -> handleEditResident(getTableView().getItems().get(getIndex())));
            btnDelete.setOnAction(e -> handleDeleteResident(getTableView().getItems().get(getIndex())));

            HBox actionTypes = new HBox(10, btnView, btnEdit, btnDelete);
            setGraphic(actionTypes);
        }
    }

    private void handleViewResident(Resident resident) {
        try {
            ResidentViewFormHandler formHandler = new ResidentViewFormHandler(new Stage(), resident); // Truyá»�n Stage
            formHandler.show();
        } catch (Exception e) {
            ErrorDialog.showError("Lá»—i hiá»ƒn thá»‹ thÃ´ng tin", "KhÃ´ng thá»ƒ má»Ÿ form xem nhÃ¢n kháº©u: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditResident(Resident resident) {
        try {
        	System.out.println("Má»Ÿ ResidentFormHandler cho resident ID: " + resident.getId() + 
                    ", refreshCallback: " + (refreshCallback != null ? "not null" : "null"));
 if (refreshCallback == null) {
     System.err.println("refreshCallback lÃ  null trong handleEditResident, khÃ´ng thá»ƒ lÃ m má»›i TableView");
 }
            ResidentFormHandler formHandler = new ResidentFormHandler(resident, refreshCallback); // Truyá»�n callback
            formHandler.show();
        } catch (Exception e) {
            ErrorDialog.showError("Lá»—i má»Ÿ form", "KhÃ´ng thá»ƒ sá»­a nhÃ¢n kháº©u: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteResident(Resident resident) {
        try {
            // Táº¡o confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("XÃ¡c nháº­n xÃ³a");
            confirmAlert.setHeaderText("Báº¡n cÃ³ cháº¯c cháº¯n xÃ³a nhÃ¢n kháº©u nÃ y khÃ´ng?");
            confirmAlert.setContentText("Há»� tÃªn: " + resident.getFullName() + "\nID: " + resident.getId());

            // TÃ¹y chá»‰nh cÃ¡c nÃºt
            ButtonType buttonYes = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonNo = new ButtonType("Há»§y", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmAlert.getButtonTypes().setAll(buttonYes, buttonNo);

            // Hiá»ƒn thá»‹ dialog vÃ  chá»� user response
            Optional<ButtonType> result = confirmAlert.showAndWait();

            // Chá»‰ xÃ³a khi user chá»�n "OK"
            if (result.isPresent() && result.get() == buttonYes) {
                controller.handleDeleteResident(resident.getId());
                if (refreshCallback != null) {
                    refreshCallback.run(); // LÃ m má»›i TableView
                }

                // Hiá»ƒn thá»‹ thÃ´ng bÃ¡o thÃ nh cÃ´ng
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("ThÃ nh cÃ´ng");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Ä�Ã£ xÃ³a nhÃ¢n kháº©u thÃ nh cÃ´ng!");
                successAlert.showAndWait();
            }
        } catch (Exception e) {
            ErrorDialog.showError("Lá»—i xÃ³a nhÃ¢n kháº©u", "KhÃ´ng thá»ƒ xÃ³a nhÃ¢n kháº©u: " + e.getMessage());
            e.printStackTrace();
        }
    }
}