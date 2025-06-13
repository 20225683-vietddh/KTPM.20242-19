package views.statistics;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import services.StatisticsService;
import utils.Configs;
import views.BaseScreenWithLogoutAndGoBackHandler;
import java.util.Map;

public class ResidentStatisticsHandler extends BaseScreenWithLogoutAndGoBackHandler {
    @FXML private VBox ageStatsContent;
    @FXML private VBox genderStatsContent;
    @FXML private VBox residenceStatsContent;
    @FXML private VBox birthYearStatsContent;
    @FXML private Label lblUserName;
    
    private StatisticsService statisticsService;
    
    public ResidentStatisticsHandler(Stage stage, String userName) throws Exception {
        super(stage, Configs.RESIDENT_STATISTICS_SCREEN_PATH, Configs.LOGO_PATH, "Thống kê nhân khẩu");
        this.statisticsService = new StatisticsService();
        loader.setController(this);
        this.setContent();
        this.setScene();
        this.lblUserName.setText(userName);
    }
    
    @FXML
    public void initialize() {
        super.initialize();
        loadStatistics();
    }
    
    private void loadStatistics() {
        try {
            // Load age statistics
            loadStatSection(ageStatsContent, statisticsService.getAgeStatistics());
            
            // Load gender statistics
            loadStatSection(genderStatsContent, statisticsService.getGenderStatistics());
            
            // Load residence status statistics
            loadStatSection(residenceStatsContent, statisticsService.getResidenceStatusStatistics());
            
            // Load birth year statistics
            loadStatSection(birthYearStatsContent, statisticsService.getBirthYearStatistics());
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi khi tải thống kê: " + e.getMessage());
        }
    }
    
    private void loadStatSection(VBox container, Map<String, Integer> stats) {
        if (container == null) return;
        
        container.getChildren().clear();
        int total = stats.values().stream().mapToInt(Integer::intValue).sum();
        
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            HBox row = new HBox(10);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            Label nameLabel = new Label(entry.getKey());
            nameLabel.setPrefWidth(150);
            nameLabel.setStyle("-fx-font-size: 14px;");
            
            Label countLabel = new Label(String.valueOf(entry.getValue()));
            countLabel.setPrefWidth(50);
            countLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            double percentage = total > 0 ? (entry.getValue() * 100.0 / total) : 0;
            Label percentLabel = new Label(String.format("%.1f%%", percentage));
            percentLabel.setPrefWidth(60);
            percentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            
            ProgressBar progressBar = new ProgressBar(percentage / 100);
            progressBar.setPrefWidth(200);
            progressBar.setStyle("-fx-accent: #3498db;");
            
            row.getChildren().addAll(nameLabel, countLabel, percentLabel, progressBar);
            container.getChildren().add(row);
        }
    }
    
    private void showError(String message) {
        // Hiển thị lỗi trong phần thống kê đầu tiên
        if (ageStatsContent != null) {
            Label errorLabel = new Label("❌ " + message);
            errorLabel.setStyle("-fx-text-fill: red;");
            ageStatsContent.getChildren().add(errorLabel);
        }
    }
} 