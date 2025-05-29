package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Utility class for displaying alert dialogs in the application.
 */
public class AlertUtils {

    /**
     * Shows an information alert dialog.
     *
     * @param title    The title of the alert dialog
     * @param header   The header text of the alert dialog
     * @param content  The content text of the alert dialog
     */
    public static void showInfoAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        configureAlert(alert, title, header, content);
        alert.showAndWait();
    }

    /**
     * Shows a warning alert dialog.
     *
     * @param title    The title of the alert dialog
     * @param header   The header text of the alert dialog
     * @param content  The content text of the alert dialog
     */
    public static void showWarningAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        configureAlert(alert, title, header, content);
        alert.showAndWait();
    }

    /**
     * Shows an error alert dialog.
     *
     * @param title    The title of the alert dialog
     * @param header   The header text of the alert dialog
     * @param content  The content text of the alert dialog
     */
    public static void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        configureAlert(alert, title, header, content);
        alert.showAndWait();
    }

    /**
     * Shows a confirmation alert dialog and returns the result.
     *
     * @param title    The title of the alert dialog
     * @param header   The header text of the alert dialog
     * @param content  The content text of the alert dialog
     * @return An Optional containing the ButtonType selected by the user
     */
    public static Optional<ButtonType> showConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        configureAlert(alert, title, header, content);
        return alert.showAndWait();
    }

    /**
     * Shows a custom alert dialog with specific buttons.
     *
     * @param alertType The type of the alert dialog
     * @param title     The title of the alert dialog
     * @param header    The header text of the alert dialog
     * @param content   The content text of the alert dialog
     * @param buttons   The button types to display on the alert dialog
     * @return An Optional containing the ButtonType selected by the user
     */
    public static Optional<ButtonType> showCustomAlert(Alert.AlertType alertType, String title, String header, 
                                                       String content, ButtonType... buttons) {
        Alert alert = new Alert(alertType, content, buttons);
        configureAlert(alert, title, header, content);
        return alert.showAndWait();
    }

    /**
     * Configures common settings for an alert dialog.
     *
     * @param alert    The Alert to configure
     * @param title    The title of the alert dialog
     * @param header   The header text of the alert dialog
     * @param content  The content text of the alert dialog
     */
    private static void configureAlert(Alert alert, String title, String header, String content) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        // Apply custom styling to the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(AlertUtils.class.getResource("/assets/css/style.css").toExternalForm());
        
        // Set the alert dialog to be application modal
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.setAlwaysOnTop(true);
    }
}