package views.messages;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.DialogPane;
import java.util.Optional;

public class ConfirmationDialog {
	public static String getOption(String message) {
		ButtonType yesButton = new ButtonType("Yes", ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonData.NO);
        
		Alert alert = new Alert(AlertType.CONFIRMATION, message, yesButton, noButton);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Confirmation Required");

        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.setStyle(
			"-fx-background-color: white; " + "-fx-padding: 15px; " + "-fx-font-family: 'System';"
        );

        dialogPane.lookup(".content.label").setStyle(
			"-fx-text-fill: black; " + "-fx-font-size: 16px;" + "-fx-padding: 12px;"
        );

        dialogPane.lookupButton(yesButton).setStyle(
				"-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); " + "-fx-text-fill: white; "
						+ "-fx-font-weight: bold; " + "-fx-background-radius: 6px;" + "-fx-padding: 10px;"
						+ "-fx-cursor: hand;"
        );
        
        dialogPane.lookupButton(noButton).setStyle(
				"-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC); " + "-fx-text-fill: white; "
						+ "-fx-font-weight: bold; " + "-fx-background-radius: 6px;" + "-fx-padding: 10px;"
						+ "-fx-cursor: hand;"
            );

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == yesButton) {
                return "YES";
            } else if (result.get() == noButton) {
                return "NO";
            }
        }
        
        return "CLOSED";
    }
}
