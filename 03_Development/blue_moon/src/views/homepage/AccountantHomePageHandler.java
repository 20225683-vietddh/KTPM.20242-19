package views.homepage;

import javafx.stage.Stage;

public class AccountantHomePageHandler extends HomePageHandler {
	public AccountantHomePageHandler(Stage stage) throws Exception {
		super(stage, utils.Configs.ACCOUNTANT_HOME_PAGE_PATH);
		loader.setController(this);
		this.setContent();
		this.setScene();
	}
}
