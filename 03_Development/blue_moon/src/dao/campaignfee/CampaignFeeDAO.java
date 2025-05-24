package dao.campaignfee;

import java.sql.SQLException;
import java.util.List;
import models.CampaignFee;
import dto.campaignfee.NewCampaignFeeDTO;

public interface CampaignFeeDAO {
	public List<CampaignFee> getCampaignFees();

	public void addNewCampaignFee(NewCampaignFeeDTO dto) throws SQLException;
	
	public void deleteCampaignFee(CampaignFee campaignFee) throws SQLException;
}
