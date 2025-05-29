package views.homepage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import views.messages.ErrorDialog;
import views.statistics.StatisticScreenHandler;

public class LeaderHomePageHandler extends HomePageHandler {

	@FXML
	private Button btnStatisticButton;

	@FXML
	@Override
	public void initialize(){
		super.initialize();
		btnStatisticButton.setOnAction(this::switchToThongKeScreen);
	}
	public void switchToThongKeScreen(ActionEvent event){
		try{

			
			StatisticScreenHandler statisticScreen = new StatisticScreenHandler(this.stage, utils.Configs.STATISTIC_SCREEN_PATH);
			statisticScreen.show();
		}catch(Exception e){
			ErrorDialog.showError("Lỗi hệ thống", "Không thể chuyển đến màn hình thống kê!");
            e.printStackTrace();
		}
		
	}

	public LeaderHomePageHandler(Stage stage) throws Exception {
		super(stage, utils.Configs.LEADER_HOME_PAGE_PATH);
		loader.setController(this);
		this.setContent();
		this.setScene();
		
	}


}