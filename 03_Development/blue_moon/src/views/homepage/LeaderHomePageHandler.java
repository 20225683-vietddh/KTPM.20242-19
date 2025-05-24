package views.homepage;

import javafx.stage.Stage;

public class LeaderHomePageHandler extends HomePageHandler {
	public LeaderHomePageHandler(Stage stage, String userName) throws Exception {
		super(stage, utils.Configs.LEADER_HOME_PAGE_PATH);
		loader.setController(this);
		this.setContent();
		this.setScene();
		super.lblUserName.setText(userName);
	}
}
