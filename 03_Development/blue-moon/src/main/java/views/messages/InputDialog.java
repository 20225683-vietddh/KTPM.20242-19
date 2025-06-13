package views.messages;

import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;

public class InputDialog {
	public static Optional<String> getInput(String initValue, String title, String headerText, String contentText) {
		TextInputDialog dialog = new TextInputDialog(initValue);
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle(
        		"-fx-font-size: 14px;" +
        		"-fx-text-fill: black;" +
                "-fx-background-color: white;" +
        		"-fx-padding: 10px;" +
                "-fx-font-family: 'System';"
        );
        
        dialogPane.lookup(".text-field").setStyle(
        		"-fx-background-color:  #FDF6F6;"
        );
        
        dialogPane.lookupButton(ButtonType.OK).setStyle(
                "-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC);" +
                "-fx-text-fill: white; " +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 5px;" +
                "-fx-padding: 10px;" +
                "-fx-cursor: hand;"
        );
        
        dialogPane.lookupButton(ButtonType.CANCEL).setStyle(
        		"-fx-background-color: linear-gradient(to right, #43A5DC, #FF7BAC);" +
                "-fx-text-fill: white; " +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 5px;" +
                "-fx-padding: 10px;" +
                "-fx-cursor: hand;"
        );

        Optional<String> result = dialog.showAndWait();
        return result;
	}
}