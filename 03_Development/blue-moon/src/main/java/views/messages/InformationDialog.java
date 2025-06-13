package views.messages;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class InformationDialog {
	public static void showNotification(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);

        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.setStyle(
			"-fx-background-color: white; " + "-fx-padding: 15px; " + "-fx-font-family: 'System';"
        );

        dialogPane.lookup(".content.label").setStyle(
			"-fx-text-fill: black; " + "-fx-font-size: 16px;" + "-fx-padding: 12px;"
        );

        dialogPane.lookupButton(ButtonType.OK).setStyle(
				"-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); " + "-fx-text-fill: white; "
						+ "-fx-font-weight: bold; " + "-fx-background-radius: 6px;" + "-fx-padding: 10px;"
						+ "-fx-cursor: hand;"
        );

        alert.showAndWait();
    }
}
