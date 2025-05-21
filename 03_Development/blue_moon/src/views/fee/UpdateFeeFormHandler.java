package views.fee;

import controllers.UpdateFeeFormController;
import dto.UpdateFeeDTO;
import exception.InvalidInputException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import models.Fee;
import utils.Configs;
import javafx.stage.Stage;

public class UpdateFeeFormHandler extends FeeFormHandler {
    private UpdateFeeFormController controller;
    private String feeId;

    public UpdateFeeFormHandler(Stage stage, String screenPath, String iconPath, String title) throws Exception {
        super(stage, screenPath, iconPath, title);
        this.controller = new UpdateFeeFormController();
    }

    public void handleSaveUpdateFee(ActionEvent event) {
        try {
            Fee fee = getFeeData();
            UpdateFeeDTO dto = new UpdateFeeDTO(
                this.feeId,
                fee.getName(),
                fee.getCreatedDate(),
                fee.getAmount(),
                fee.getIsMandatory(),
                fee.getStatus(),
                fee.getDescription()
            );

            boolean result = controller.saveUpdateFee(dto);
            if (result) {
                showAlert(AlertType.INFORMATION, "Thành công", "Cập nhật khoản thu thành công!");
                this.stage.close();
            }
        } catch (InvalidInputException e) {
            showAlert(AlertType.ERROR, "Lỗi dữ liệu", e.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    public void loadFeeData(String feeId) {
        try {
            this.feeId = feeId;
            UpdateFeeDTO feeToUpdate = controller.getFeeById(feeId);
            if (feeToUpdate != null) {
                Fee fee = new Fee(
                    feeToUpdate.getId(),
                    feeToUpdate.getName(),
                    feeToUpdate.getCreatedDate(),
                    feeToUpdate.getAmount(),
                    feeToUpdate.getIsMandatory(),
                    feeToUpdate.getStatus(),
                    feeToUpdate.getDescription()
                );
                setFeeData(fee);
            } else {
                showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy khoản thu cần cập nhật");
                this.stage.close();
            }
        } catch (InvalidInputException e) {
            showAlert(AlertType.ERROR, "Lỗi dữ liệu", e.getMessage());
            this.stage.close();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra: " + e.getMessage());
            this.stage.close();
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