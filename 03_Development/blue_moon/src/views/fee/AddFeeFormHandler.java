package views.fee;

import controllers.AddFeeFormController;
import dto.AddFeeDTO;
import exception.InvalidInputException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import models.Fee;
import utils.Configs;
import javafx.stage.Stage;

public class AddFeeFormHandler extends FeeFormHandler {
    private AddFeeFormController controller;

    public AddFeeFormHandler(Stage stage, String screenPath, String iconPath, String title) throws Exception {
        super(stage, screenPath, iconPath, title);
        this.controller = new AddFeeFormController();
        this.loader.setController(this);
        this.setContent();
        this.setScene();
    }

    public void handleSaveAddFee(ActionEvent event) {
        try {
            Fee fee = getFeeData();
            AddFeeDTO dto = new AddFeeDTO(
                fee.getName(),
                fee.getCreatedDate(),
                fee.getAmount(),
                fee.getIsMandatory(),
                fee.getStatus(),
                fee.getDescription()
            );

            boolean result = controller.saveAddFee(dto);
            if (result) {
                showAlert(AlertType.INFORMATION, "Thành công", "Thêm khoản thu thành công!");
                this.stage.close();
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

    public void clearForm() {
        if (tfName != null) tfName.clear();
        if (tfAmount != null) tfAmount.clear();
        if (cbMandatory != null) cbMandatory.setValue(null);
        if (tfDescription != null) tfDescription.clear();
    }
} 