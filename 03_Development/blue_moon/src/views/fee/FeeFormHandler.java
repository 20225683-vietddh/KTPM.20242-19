package views.fee;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import models.Fee;
import views.BaseScreenHandler;
import javafx.stage.Stage;
import utils.Mandatory;
import utils.Status;
import javafx.util.StringConverter;
import java.time.LocalDate;

public abstract class FeeFormHandler extends BaseScreenHandler {
    @FXML protected TextField tfName;
    @FXML protected TextField tfAmount;
    protected TextField tfCreatedDate;
    @FXML protected ComboBox<Mandatory> cbMandatory;
    @FXML protected ComboBox<Status> cbStatus;
    @FXML protected TextField tfDescription;

    public FeeFormHandler(Stage stage, String screenPath, String iconPath, String title) throws Exception {
        super(stage, screenPath, iconPath, title);
        tfCreatedDate = new TextField();
        cbStatus = new ComboBox<>();
        setupStatusComboBox();
    }

    @FXML
    protected void initialize() {
        if (cbMandatory != null) {
            cbMandatory.getItems().clear();
            cbMandatory.getItems().addAll(Mandatory.values());
            cbMandatory.setConverter(new StringConverter<Mandatory>() {
                @Override
                public String toString(Mandatory mandatory) {
                    return mandatory == null ? "" : mandatory.getDisplayName();
                }

                @Override
                public Mandatory fromString(String string) {
                    return null;
                }
            });
        }
    }

    private void setupStatusComboBox() {
        if (cbStatus != null) {
            cbStatus.getItems().clear();
            cbStatus.getItems().addAll(Status.values());
            cbStatus.setConverter(new StringConverter<Status>() {
                @Override
                public String toString(Status status) {
                    return status == null ? "" : status.getDisplayName();
                }

                @Override
                public Status fromString(String string) {
                    return null;
                }
            });
        }
    }

    public Fee getFeeData() {
        String name = (tfName != null) ? tfName.getText() : "";
        String amountStrInput = (tfAmount != null) ? tfAmount.getText() : "0";
        Mandatory mandatory = (cbMandatory != null) ? cbMandatory.getValue() : Mandatory.OPTIONAL;
        Status status = (cbStatus != null) ? cbStatus.getValue() : Status.UNPAID;
        String description = (tfDescription != null) ? tfDescription.getText() : "";

        String amountStrCleaned = amountStrInput.replaceAll("[^\\d]", "");
        double amount = 0;
        if (amountStrCleaned != null && !amountStrCleaned.isEmpty()) {
            try {
                amount = Double.parseDouble(amountStrCleaned);
            } catch (NumberFormatException e) {
                System.err.println("Invalid amount format: " + amountStrInput + ". Parsed as: " + amountStrCleaned);
                amount = 0;
            }
        }

        LocalDate createdDate = LocalDate.now();

        return new Fee(
            null,
            name,
            createdDate,
            amount,
            mandatory,
            status,
            description
        );
    }

    public void setFeeData(Fee fee) {
        if (tfName != null) tfName.setText(fee.getName());
        if (tfAmount != null) tfAmount.setText(String.valueOf(fee.getAmount()));
        if (cbMandatory != null) cbMandatory.setValue(fee.getIsMandatory());
        if (cbStatus != null) cbStatus.setValue(fee.getStatus());
        if (tfDescription != null) tfDescription.setText(fee.getDescription());
    }
}
