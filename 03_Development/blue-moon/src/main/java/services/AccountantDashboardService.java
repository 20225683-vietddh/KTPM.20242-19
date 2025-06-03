package services;

import javafx.scene.chart.XYChart;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import dao.campaignfee.CampaignFeeDAO;
import dao.campaignfee.CampaignFeeDAOPostgreSQL;
import dao.fee.FeeDAO;
import dao.fee.FeeDAOPostgreSQL;

public class AccountantDashboardService implements DashboardService {
    //private final HouseholdDAO householdDAO;
    //private final ResidentDAO residentDAO;
    private final CampaignFeeDAO campaignFeeDAO;
    private final FeeDAO feeDAO;

    public AccountantDashboardService() {
        try {
            //this.householdDAO = new HouseholdDAO();
            //this.residentDAO = new ResidentDAO();
            this.campaignFeeDAO = new CampaignFeeDAOPostgreSQL();
            this.feeDAO = new FeeDAOPostgreSQL();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize dashboard service", e);
        }
    }

    @Override
    public int getTotalHouseholds() {
        //return householdDAO.countTotal();
        return 0; // Temporary return
    }

    @Override
    public int getTotalResidents() {
        //return residentDAO.countTotal();
        return 0; // Temporary return
    }

    @Override
    public XYChart.Series<String, Number> getCampaignFeeHistory() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Đợt thu phí");
        try {
            List<Map<String, Object>> campaigns = campaignFeeDAO.getLastNCampaigns(6);
            for (Map<String, Object> campaign : campaigns) {
                series.getData().add(new XYChart.Data<>(
                    campaign.get("name").toString(),
                    ((Number) campaign.get("total_amount")).doubleValue()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return series;
    }

    @Override
    public XYChart.Series<String, Number> getResidentChangeHistory() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Biến động nhân khẩu");
        // Temporary data
        series.getData().add(new XYChart.Data<>("2024-01", 0));
        series.getData().add(new XYChart.Data<>("2024-02", 0));
        series.getData().add(new XYChart.Data<>("2024-03", 0));
        return series;
    }

    @Override
    public List<Map<String, Object>> getTypicalFees() {
        try {
            return feeDAO.getLastNFees(5);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getRecentResidentChanges() {
        //return residentDAO.getRecentChanges(5);
        return new ArrayList<>(); // Temporary return
    }
} 