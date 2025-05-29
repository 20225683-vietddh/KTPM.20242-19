package views.homepage;


import javafx.stage.Stage;
import views.BaseScreenWithLogoutAndGoBackHandler;

public abstract class HomePageHandler extends BaseScreenWithLogoutAndGoBackHandler {

	

	public HomePageHandler(Stage stage, String screenPath) throws Exception {
		super(stage, screenPath, utils.Configs.LOGO_PATH, "Trang chủ");
	}
}
