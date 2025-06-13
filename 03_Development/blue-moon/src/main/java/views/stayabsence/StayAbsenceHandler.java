package views.stayabsence;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import views.BaseScreenWithLogoutAndGoBackHandler;

public class StayAbsenceHandler extends BaseScreenWithLogoutAndGoBackHandler {
    @FXML private Button btnTemporaryStay;
    @FXML private Button btnTemporaryAbsence;
    @FXML private Button btnViewRecords;   
    @FXML private Label lblUserName;
    
    public StayAbsenceHandler(Stage stage, String userName) throws Exception {
        super(stage, utils.Configs.STAY_ABSENCE_SCREEN_PATH, utils.Configs.LOGO_PATH, "Quản lý tạm trú & tạm vắng");
        loader.setController(this);
        this.setContent();
        this.setScene();
        this.lblUserName.setText(userName);
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        setupEventHandlers();
    }
    
    private void setupEventHandlers() {
        btnTemporaryStay.setOnAction(e -> handleTemporaryStay());
        btnTemporaryAbsence.setOnAction(e -> handleTemporaryAbsence());
        btnViewRecords.setOnAction(e -> handleViewRecords());
    }
    
    private void handleTemporaryStay() {
        try {
            new TemporaryStayFormHandler(stage, this);
        } catch (Exception e) {
            e.printStackTrace();
            views.messages.ErrorDialog.showError("Lỗi", "Không thể mở form cấp tạm trú!");
        }
    }
    
    private void handleTemporaryAbsence() {
        try {
            new TemporaryAbsenceFormHandler(stage, this);
        } catch (Exception e) {
            e.printStackTrace();
            views.messages.ErrorDialog.showError("Lỗi", "Không thể mở form cấp tạm vắng!");
        }
    }
    
    private void handleViewRecords() {
        try {
            System.out.println("🔍 Opening records list...");
            String currentUserName = lblUserName.getText();
            StayAbsenceListHandler listHandler = new StayAbsenceListHandler(stage, currentUserName);
            listHandler.show();
        } catch (Exception e) {
            e.printStackTrace();
            views.messages.ErrorDialog.showError("Lỗi", "Không thể mở danh sách hồ sơ!");
        }
    }
    
    // Use the default goBack functionality from base class
} 