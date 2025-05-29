package views.homepage;

import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import views.resident.ResidentListHandler;
public class LeaderHomePageHandler extends HomePageHandler {

	@FXML 
	private Button btnResidentList;
	
	public LeaderHomePageHandler(Stage stage, String userName) throws Exception {
		super(stage, utils.Configs.LEADER_HOME_PAGE_PATH);
		loader.setController(this);
		this.setContent();
		this.setScene();
		super.lblUserName.setText(userName);
	}
	
	@FXML
    public void initialize() {
		btnResidentList.setOnAction(e -> handleResidentList()); // Gán sự kiện cho nút
    }

    private void handleResidentList() {
        try {
            ResidentListHandler residentScreen = new ResidentListHandler(this.stage);
            residentScreen.show(); // Hiển thị màn hình residentList
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
}
