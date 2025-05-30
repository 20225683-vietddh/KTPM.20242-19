package views.homepage;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import views.BaseScreenWithLogoutAndGoBackHandler;

public abstract class HomePageHandler extends BaseScreenWithLogoutAndGoBackHandler {
	@FXML
	protected Label lblUserName;
	
	public HomePageHandler(Stage stage, String screenPath) throws Exception {
		super(stage, screenPath, utils.Configs.LOGO_PATH, "Trang chủ");
	}
}
