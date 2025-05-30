package views.fee;

import controllers.AddFeeFormController;
import dto.fee.AddFeeDTO;
import exception.InvalidInputException;
import javafx.event.ActionEvent;
import models.Fee;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;
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
    
    @Override
    protected void handleSaveButtonAction(ActionEvent event) {
        try {
            if (!validateInput()) {
                return;
            }
            
            Fee fee = getFeeData();
            AddFeeDTO dto = new AddFeeDTO(
                fee.getName(),
                fee.getCreatedDate(),
                fee.getIsMandatory(),
                fee.getDescription()
            );

            boolean result = controller.saveAddFee(dto);
            if (result) {
                InformationDialog.showNotification("Thành công", "Thêm khoản thu thành công!");
                this.stage.close();
            }
        } catch (InvalidInputException e) {
            ErrorDialog.showError("Lỗi dữ liệu", e.getMessage());
        } catch (Exception e) {
            ErrorDialog.showError("Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }
} 