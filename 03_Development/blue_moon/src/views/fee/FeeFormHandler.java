package views.fee;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import models.Fee;
import views.BaseScreenHandler;
import views.messages.ErrorDialog;
import views.messages.InformationDialog;
import javafx.stage.Stage;
import java.time.LocalDate;
import javafx.event.ActionEvent;

public abstract class FeeFormHandler extends BaseScreenHandler {
    @FXML protected TextField tfName;
    @FXML protected TextField tfDescription;
    @FXML protected CheckBox chkMandatory;
    @FXML protected Button btnSaveAddFee;

    public FeeFormHandler(Stage stage, String screenPath, String iconPath, String title) throws Exception {
        super(stage, screenPath, iconPath, title);
    }
    
    @FXML
    public void initialize() {
        if (btnSaveAddFee != null) {
            btnSaveAddFee.setOnAction(this::handleSaveButtonAction);
        }
    }
    
    protected abstract void handleSaveButtonAction(ActionEvent event);

    protected Fee getFeeData() {
        String name = (tfName != null) ? tfName.getText().trim() : "";
        boolean mandatory = (chkMandatory != null) && chkMandatory.isSelected();
        String description = (tfDescription != null) ? tfDescription.getText().trim() : "";
        LocalDate createdDate = LocalDate.now();

        return new Fee(
            0, 
            name,
            createdDate,
            mandatory,
            description
        );
    }

    protected void setFeeData(Fee fee) {
        if (tfName != null) tfName.setText(fee.getName());
        if (chkMandatory != null) chkMandatory.setSelected(fee.getIsMandatory());
        if (tfDescription != null) tfDescription.setText(fee.getDescription());
    }
    
    protected boolean validateInput() {
        Fee fee = getFeeData();
        
        if (fee.getName() == null || fee.getName().isEmpty()) {
            ErrorDialog.showError("Lỗi dữ liệu", "Tên khoản thu không được để trống!");
            return false;
        }
        
        return true;
    }

    public void clearForm() {
        if (tfName != null) tfName.clear();
        if (tfDescription != null) tfDescription.clear();
        if (chkMandatory != null) chkMandatory.setSelected(false);
    }
}
