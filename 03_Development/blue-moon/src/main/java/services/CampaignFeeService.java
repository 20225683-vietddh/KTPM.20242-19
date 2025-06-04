package services;

import java.sql.SQLException;
import java.util.List;
import models.CampaignFee;
import dao.campaignfee.*;
import dto.campaignfee.CampaignFeeDTO;

public class CampaignFeeService {
	private final CampaignFeeDAO campaignFeeDAO;
	
	public CampaignFeeService() throws SQLException {
		this.campaignFeeDAO = new CampaignFeeDAOPostgreSQL();
	}
	
	public List<CampaignFee> getAllCampaignFees() {
		return campaignFeeDAO.getCampaignFees();
	}
	
	public void addNewCampaignFee(CampaignFeeDTO dto) throws SQLException {
		campaignFeeDAO.addNewCampaignFee(dto);
	}
	
	public void deleteCampaignFee(CampaignFee campaignFee) throws SQLException {
		campaignFeeDAO.deleteCampaignFee(campaignFee);
	}
	
	public void updateCampaignFee(CampaignFeeDTO dto) throws SQLException {
		campaignFeeDAO.updateCampaignFee(dto);
	}
	
	public boolean isFeesExisted(int campaignFeeId, List<Integer> feeIds) throws SQLException {
		return campaignFeeDAO.isFeesExisted(campaignFeeId, feeIds);
	}
}
