package views;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public abstract class BaseScreenWithLogoutAndGoBackHandler extends BaseScreenHandler {
	@FXML
	protected Button btnLogout;
	
	@FXML
	protected Button btnGoBack;
	
	public BaseScreenWithLogoutAndGoBackHandler(Stage stage, String screenPath, String iconPath, String title) throws Exception {
		super(stage, screenPath, iconPath, title);
	}
}
