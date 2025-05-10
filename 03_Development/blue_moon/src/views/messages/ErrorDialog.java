package views.messages;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonType;

public class ErrorDialog {
	public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Lỗi!");
        alert.setHeaderText(null);

        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.setStyle(
            "-fx-background-color: #fef2f2; " +
            "-fx-border-color: #f87171; " +
            "-fx-border-width: 2px; " +
            "-fx-padding: 20px; " +
            "-fx-font-family: 'Segoe UI';"
        );

        dialogPane.lookup(".content.label").setStyle(
            "-fx-text-fill: #b91c1c; " +
            "-fx-font-size: 16px;"
        );

        dialogPane.lookupButton(ButtonType.OK).setStyle(
            "-fx-background-color: #ef4444; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 6px" +
            "-fx-padding: 20px" +
            "-fx-cursor: hand;"
        );

        alert.showAndWait();
    }
}
