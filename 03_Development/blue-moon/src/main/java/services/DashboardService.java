package services;
import javafx.scene.chart.XYChart;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    int getTotalHouseholds();
    int getTotalResidents();
    XYChart.Series<String, Number> getCampaignFeeHistory();
    XYChart.Series<String, Number> getResidentChangeHistory();
    List<Map<String, Object>> getTypicalFees();
    List<Map<String, Object>> getRecentResidentChanges();
} 