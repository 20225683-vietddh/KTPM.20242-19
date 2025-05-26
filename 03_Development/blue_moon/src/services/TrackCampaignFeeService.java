package services;

import dao.trackcampaignfee.*;
import dto.campaignfee.PaidFeeResponseDTO;
import java.sql.SQLException;

public class TrackCampaignFeeService {
	private final TrackCampaignFeeDAO dao;
	
	public TrackCampaignFeeService() throws SQLException {
		this.dao = new TrackCampaignFeeDAOPostgreSQL();
	}
	
	public int getExpectedAmount(int campaignFeeId) throws SQLException {
		return dao.getExpectedAmount(campaignFeeId);
	}
	
	public int getPaidAmount(int campaignFeeId) throws SQLException {
		return dao.getPaidAmount(campaignFeeId);
	}
	
	public boolean isCampaignFeeAssigned(int campaignFeeId) throws SQLException {
		return dao.count(campaignFeeId) > 0 ? true : false;
	}
	
	public PaidFeeResponseDTO getExpectedAndPaidAmount(int campaignFeeId, int feeId) throws SQLException {
		return dao.getExpectedAndPaidAmount(campaignFeeId, feeId);
	}
}
