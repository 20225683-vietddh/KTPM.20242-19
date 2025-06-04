package views.homepage;

import javafx.stage.Stage;
import services.LeaderDashboardService;

public class LeaderHomePageHandler extends HomePageHandler {
	public LeaderHomePageHandler(Stage stage, String userName) throws Exception {
		super(stage, utils.Configs.LEADER_HOME_PAGE_PATH);
		this.dashboardService = new LeaderDashboardService();
		loader.setController(this);
		this.setContent();
		this.setScene();
		super.lblUserName.setText(userName);
	}
	
	@Override
	protected void loadDashboardData() {
		super.loadDashboardData(); 
	}
}
