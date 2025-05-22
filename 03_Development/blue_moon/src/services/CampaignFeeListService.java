package services;

import java.sql.SQLException;
import java.util.List;
import models.CampaignFee;
import dao.campaignfee.*;

public class CampaignFeeListService {
	private final CampaignFeeDAO campaignFeeDAO;
	
	public CampaignFeeListService() throws SQLException {
		this.campaignFeeDAO = new CampaignFeeDAOPostgreSQL();
	}
	
	public List<CampaignFee> getAllCampaignFees() {
		return campaignFeeDAO.getCampaignFees();
	}
}
