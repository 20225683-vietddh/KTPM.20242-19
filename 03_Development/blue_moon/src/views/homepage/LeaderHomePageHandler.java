package views.homepage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import views.citizen.CitizenManagementPageHandler;

public class LeaderHomePageHandler extends HomePageHandler {
    
    @FXML 
    private Button btnCitizenManagement; // Nút Quản lý nhân khẩu


    public LeaderHomePageHandler(Stage stage) throws Exception {
        super(stage, utils.Configs.LEADER_HOME_PAGE_PATH);
        loader.setController(this);
        this.setContent();
        this.setScene();
    }

  @FXML
    public void initialize() {
       btnCitizenManagement.setOnAction(e -> handleCitizenManagement()); // Gán sự kiện cho nút
    }

    private void handleCitizenManagement() {
        try {
            CitizenManagementPageHandler citizenScreen = new CitizenManagementPageHandler(this.stage);
            citizenScreen.show(); // Hiển thị màn hình CitizenManagement
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
   
}
