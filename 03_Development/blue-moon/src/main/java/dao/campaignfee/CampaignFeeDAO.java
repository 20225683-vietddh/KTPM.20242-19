package dao.campaignfee;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import models.CampaignFee;
import dto.campaignfee.CampaignFeeDTO;

public interface CampaignFeeDAO {
	public List<CampaignFee> getCampaignFees();

	public void addNewCampaignFee(CampaignFeeDTO dto) throws SQLException;
	
	public void deleteCampaignFee(CampaignFee campaignFee) throws SQLException;
	
	public void updateCampaignFee(CampaignFeeDTO dto) throws SQLException;

	List<Map<String, Object>> getLastNCampaigns(int n);
	List<Map<String, Object>> getLastNCampaignsWithStatus(int n);
}
