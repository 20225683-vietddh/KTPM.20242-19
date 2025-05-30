package views.fee;

import controllers.UpdateFeeFormController;
import dto.fee.UpdateFeeDTO;
import exception.InvalidInputException;
import javafx.event.ActionEvent;
import models.Fee;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;
import javafx.stage.Stage;

public class UpdateFeeFormHandler extends FeeFormHandler {
    private UpdateFeeFormController controller;
    private int feeId;

    public UpdateFeeFormHandler(Stage stage, String screenPath, String iconPath, String title) throws Exception {
        super(stage, screenPath, iconPath, title);
        this.controller = new UpdateFeeFormController();
        this.loader.setController(this);
        this.setContent();
        this.setScene();
    }
    
    public UpdateFeeFormHandler(Stage stage, String screenPath, String iconPath, String title, int feeId) throws Exception {
        super(stage, screenPath, iconPath, title);
        this.controller = new UpdateFeeFormController();
        this.feeId = feeId;
        this.loader.setController(this);
        this.setContent();
        this.setScene();
        loadFeeData(feeId);
    }
    
    @Override
    protected void handleSaveButtonAction(ActionEvent event) {
        try {
            if (!validateInput()) {
                return;
            }
            
            Fee fee = getFeeData();
            UpdateFeeDTO dto = new UpdateFeeDTO(
                this.feeId,
                fee.getName(),
                fee.getCreatedDate(),
                fee.getIsMandatory(),
                fee.getDescription()
            );

            boolean result = controller.saveUpdateFee(dto);
            if (result) {
                InformationDialog.showNotification("Thành công", "Cập nhật khoản thu thành công!");
                this.stage.close();
            }
        } catch (InvalidInputException e) {
            ErrorDialog.showError("Lỗi dữ liệu", e.getMessage());
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    private void loadFeeData(int feeId) {
        try {
            this.feeId = feeId;
            UpdateFeeDTO feeToUpdate = controller.getFeeById(feeId);
            if (feeToUpdate != null) {
                Fee fee = new Fee(
                    feeToUpdate.getId(),
                    feeToUpdate.getName(),
                    feeToUpdate.getCreatedDate(),
                    feeToUpdate.getIsMandatory(),
                    feeToUpdate.getDescription()
                );
                setFeeData(fee);
            } else {
                ErrorDialog.showError("Lỗi", "Không tìm thấy khoản thu cần cập nhật");
                this.stage.close();
            }
        } catch (InvalidInputException e) {
            ErrorDialog.showError("Lỗi dữ liệu", e.getMessage());
            this.stage.close();
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
            this.stage.close();
        }
    }
} 